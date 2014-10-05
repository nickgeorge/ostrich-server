package world.population;

import java.nio.ByteBuffer;

public interface Writable {

  public int getType();
  public int getBufferSize();
  public ByteBuffer writeToBuffer(ByteBuffer buffer);
}
