package world.main;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import world.population.Fella;
import world.population.Osterich;
import world.population.Sentient;
import world.population.Thing;
import world.websocket.WebSocketServer;
import world.websocket.WebSocketServer.BoxWebSocket;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class FellaServer extends Thread {
  public WebSocketServer server;
  public World world;
  private long sysTime = System.currentTimeMillis();

  public final BiMap<Integer, Thing> ownerMap = HashBiMap.create();
  public final Map<Integer, String> nameMap = new HashMap<>();
  public final Map<Integer, Integer> scoreMap = new HashMap<>();

  public FellaServer() {
    server = new WebSocketServer(this);
    world = new World(this);
    world.populate();
    server.start();
    start();
  }

  @Override
  public void run() {
    while (true) {
      float deltaT = System.currentTimeMillis() - sysTime;
      if (deltaT < 4) {
        try {
          Thread.sleep((long) (4 - deltaT));
          deltaT = System.currentTimeMillis() - sysTime;
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      sysTime = System.currentTimeMillis();
      world.advance(deltaT / 1000.0f);
      ByteBuffer stateBuffer = ByteBuffer.allocate(8 + world.getBufferSize());
      stateBuffer.putInt(MessageCode.WORLD_UPDATE);
      world.writeToBuffer(stateBuffer);
      stateBuffer.put(MessageCode.EOM);
      server.sendAll(stateBuffer);

      world.updateLists();
    }
  }

  public static void main(String[] args) {
    new FellaServer();
  }

  public void onMessage(BoxWebSocket source, byte[] data) {
    byte code = data[0];
    switch (code) {
      case MessageCode.GET_STATE:
        sendState(source);
        break;
      case MessageCode.JOIN:
        Osterich osterich = new Osterich(world);
        world.add(osterich);
        scoreMap.put(source.getId(), 0);
        ownerMap.put(source.getId(), osterich);
        sendState(source);
        sendAllScore();
        sendAllNameMap();
        sendYouAre(source, osterich.getId());
        break;
      case MessageCode.MY_NAME_IS:
        String name = new String(data, 1, data.length - 1);
        nameMap.put(source.getId(), name);
        sendAllNameMap();
        break;
      case MessageCode.KEY_EVENT:
        byte isKeyDown = data[1];
        byte keyCode = data[2];
        if (ownerMap.containsKey(source.getId())) {
          ownerMap.get(source.getId()).onKeyEvent(isKeyDown == 1, keyCode);
        }
        break;
      case MessageCode.RESTART:
        for (Sentient sentient : world.sentients) {
          if (sentient instanceof Fella) world.remove(sentient);
        }
        for (int key : scoreMap.keySet()) {
          scoreMap.put(key, 0);
        }
        sendAllScore();
        break;
      case MessageCode.MOUSE_MOVE_EVENT:
        ByteBuffer mouseMoveBuffer = ByteBuffer.wrap(data);
        if (ownerMap.containsKey(source.getId())) {
         ownerMap.get(source.getId()).onMouseMove(
             mouseMoveBuffer.getFloat(1),
             mouseMoveBuffer.getFloat(5));
        }
        break;
      case MessageCode.MODE:
        ByteBuffer modeBuffer = ByteBuffer.wrap(data);
        Osterich.ControlMode mode = Osterich.ControlMode.values()[modeBuffer.getInt(1)];
        ((Osterich) ownerMap.get(source.getId())).setControlMode(mode);
        break;
//     case MessageCode.MOUSE_CLICK_EVENT:
//       ByteBuffer mouseClickBuffer = data;
//         if (ownerMap.containsKey(source.getId())) {
//         ownerMap.get(source.getId()).onMouseClick(mouseClickBuffer.get(1));
//       }
//       break;
     default:
       System.out.println("Unrecognized code: " + code);
    }
  }

  private void sendState(BoxWebSocket source) {
    ByteBuffer stateBuffer = ByteBuffer.allocate(8 + world.getBufferSize());
    stateBuffer.putInt(MessageCode.SET_STATE);
    world.writeToBuffer(stateBuffer);
    stateBuffer.put(MessageCode.EOM);
    source.send(stateBuffer);
  }

  public ByteBuffer getNameMapBuffer() {
    int size = 12 + 8*nameMap.size();
    for (Map.Entry<Integer, String> entry : nameMap.entrySet()) {
      size += entry.getValue().getBytes().length;
    }

    ByteBuffer buffer = ByteBuffer.allocate(size);
    buffer.putInt(MessageCode.NAME_MAP);
    buffer.putInt(nameMap.size());
    for (Map.Entry<Integer, String> entry : nameMap.entrySet()) {
      buffer.putInt(entry.getKey());
      byte[] bytes = entry.getValue().getBytes();
      buffer.putInt(bytes.length);
      buffer.put(bytes);
    }
    buffer.put(MessageCode.EOM);
    return buffer;
  }

  public ByteBuffer getScoreBuffer() {
    ByteBuffer scoreBuffer =
        ByteBuffer.allocate(12 + 12*scoreMap.size());
    writeScoreToBuffer(scoreBuffer);
    return scoreBuffer;
  }

  public void writeScoreToBuffer(ByteBuffer buffer) {
    buffer.putInt(MessageCode.SCORE);
    buffer.putInt(scoreMap.size());
    for (Map.Entry<Integer, Integer> entry : scoreMap.entrySet()) {
      buffer.putInt(entry.getKey());
      buffer.putInt(ownerMap.get(entry.getKey()).getId());
      buffer.putInt(entry.getValue());
    }
    buffer.put(MessageCode.EOM);
  }

  public void sendScore(BoxWebSocket target) {
    target.send(getScoreBuffer());
  }

  public void sendAllScore() {
    server.sendAll(getScoreBuffer());
  }

  public void sendYouAre(BoxWebSocket target, int id) {
    ByteBuffer buffer = ByteBuffer.allocate(12);
    buffer.putInt(MessageCode.YOU_ARE);
    buffer.putInt(id);
    buffer.put(MessageCode.EOM);
    target.send(buffer);
  }

  public void sendAllNameMap() {
    server.sendAll(getNameMapBuffer());
  }

  public void onExit(int socketId) {
    world.remove(ownerMap.get(socketId));
    scoreMap.remove(socketId);
    ownerMap.remove(socketId);
    sendAllScore();
  }

  public void addScore(Thing thing, int score) {
    int id = ownerMap.inverse().get(thing);
    int currentScore = scoreMap.containsKey(id) ? scoreMap.get(id) : 0;
    scoreMap.put(id, currentScore + score);
    sendAllScore();
  }
}
