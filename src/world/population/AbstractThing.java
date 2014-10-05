package world.population;

import java.nio.ByteBuffer;

import world.util.Quat;
import world.util.Vec3;

public abstract class AbstractThing implements Thing {

  protected int id;
  public Vec3 position;
  public Vec3 velocity;
  protected boolean alive = true;

  
  public Quat upOrientation = new Quat(0, 0, 0, 1);
  
  @Override
  public int getBufferSize() {
    return 25;
  }

  @Override
  public ByteBuffer writeToBuffer(ByteBuffer buffer) {
    buffer.put((byte) (alive ? 1 : 0));
    position.writeToBuffer(buffer);
    velocity.writeToBuffer(buffer);
    return buffer;
  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public void setId(int id) {
    this.id = id;
  }

  @Override
  public void advance(float dt) {
    position.add(velocity, dt);
  }
  
  @Override
  public Vec3 getCenter() {
    return position;
  }
  
  @Override
  public void onMouseMove(float dX, float dY) {}
  
  @Override
  public void onKeyEvent(boolean isKeyDown, byte keyCode) {}

}
