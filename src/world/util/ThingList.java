package world.util;

import java.nio.ByteBuffer;
import java.util.List;

import world.main.MessageCode;
import world.population.Thing;

public class ThingList<TYPE extends Thing> 
    extends ControlledList<TYPE> {


  public void writeToBuffer(ByteBuffer buffer) {
    writeList(buffer, addList);
    buffer.put(MessageCode.SYNC);
    writeRemoveList(buffer, removeList);
    buffer.put(MessageCode.SYNC);
    writeList(buffer, contents);
    buffer.put(MessageCode.SYNC);
  }

  private void writeList(ByteBuffer buffer, List<TYPE> list) {
    buffer.putInt(list.size());
    for (Thing thing : list) {
      buffer.putInt(thing.getId());
      buffer.putInt(thing.getType());
      int index = buffer.position();
      thing.writeToBuffer(buffer);
      if (buffer.position() - index != thing.getBufferSize()) {
        System.out.println("Wrong size!!! " + (buffer.position() - index)
            + " vs " + thing.getBufferSize());
      }
    }
  }

  private void writeRemoveList(ByteBuffer buffer, List<TYPE> list) {
    buffer.putInt(list.size());
    for (Thing thing : list) {
      buffer.putInt(thing.getId());
    }
  }

  public int getBufferSize() {
    int size = 4; // add list count
    for (Thing addThing : addList) {
      size += 8; // type + id
      size += addThing.getBufferSize();
    }
    size += 8; // Sync + remove count
    for (Thing removeThing : removeList) {
      size += 8; // type + id
      size += removeThing.getBufferSize();
    }
    size += 8; // sync + contents count
    for (Thing thing : contents) {
      size += 8; // type + id
      size += thing.getBufferSize();
    }
    size += 4; // sync
    return size;
  }
}
