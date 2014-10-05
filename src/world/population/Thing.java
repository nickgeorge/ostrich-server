package world.population;

import world.util.Vec3;


public interface Thing extends Writable, Controllable {
  public int getId();

  public void setId(int id);

  public void advance(float sec);
  public Vec3 getCenter();
}
