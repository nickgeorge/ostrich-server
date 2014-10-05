package world.websocket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.websocket.api.UpgradeRequest;
import org.eclipse.jetty.websocket.api.UpgradeResponse;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import world.main.FellaServer;
import world.util.ControlledList;


public class WebSocketServer extends Thread {
  private Logger logger = Logger.getLogger("WebSocketServer");
  
  private FellaServer burnerServer;
  private Server jettyServer;
  private ControlledList<BoxWebSocket> webSocks = new ControlledList<>();
  
  private static byte nextSocketId = 1;

  public WebSocketServer(FellaServer burnerServer) {
    this.burnerServer = burnerServer;
    jettyServer = new Server(8080);

    ServletContextHandler sockHandler = new ServletContextHandler(
        ServletContextHandler.SESSIONS);
    sockHandler.setContextPath("/");
    sockHandler.addServlet(new ServletHolder(new SocketServlet()),
        "/websock");

    HandlerList handlers = new HandlerList();
    handlers.setHandlers(new Handler[] { sockHandler });
    jettyServer.setHandler(handlers);

    try {
      jettyServer.start();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void sendAll(ByteBuffer data) {
    webSocks.update();
    ByteBuffer wrappedData = ByteBuffer.wrap(data.array());
    for (BoxWebSocket dumbSock : webSocks) {
      if (dumbSock.isConnected()) {
        dumbSock.getRemote().sendBytesByFuture(wrappedData);
      }
    }
  }
  
  public class BoxWebSocket extends WebSocketAdapter {
    private final int id;

    public BoxWebSocket() {
      id = nextSocketId++;
      webSocks.add(this);
    }

    public int getId() {
      return id;
    }

    public void send(ByteBuffer buffer) {
      sendWrapped(ByteBuffer.wrap(buffer.array()));
    }

    public void sendWrapped(ByteBuffer buffer) {
      try {
        getRemote().sendBytes(buffer);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
      webSocks.remove(this);
      burnerServer.onExit(id);
    }

    @Override
    public void onWebSocketBinary(byte[] data, int offset, int len) {
      burnerServer.onMessage(this, data);
    }
    
    @Override
    public void onWebSocketError(Throwable cause) {
      cause.printStackTrace();
    }
  }
  
  private class SocketServlet extends WebSocketServlet {
    @Override
    public void configure(WebSocketServletFactory factory) {
      factory.getPolicy().setIdleTimeout(10000);
      factory.setCreator(new WebSocketCreator() {
        @Override
        public Object createWebSocket(UpgradeRequest req, UpgradeResponse resp) {
          logger.info("Connection from " + req.getOrigin());
          return new BoxWebSocket();
        }
      });
    }
  }
}
