package world.population;

import java.nio.ByteBuffer;

import world.util.Quat;
import world.util.Util;
import world.util.Vec3;

public class Fella extends AbstractThing implements Sentient {

//   private float[] upOrientation = { 0, 0, 0, 1 };

  public float rYaw = 0;

  public float vRMag = 20;

  public Quat color = Quat.randomColor(.5f);

  private Vec3 rotatedVelocity = new Vec3();
//  private float deathAge = 0;

  public Fella() {
    position = new Vec3(Util.random(-75, 75),
        0,
        Util.random(-75, 75));
    velocity = new Vec3(0, 0, 10);
  }

  @Override
  public int getType() {
    return 48;
  }

  @Override
  public void advance(float dt) {
    if (!alive) {
//      this.deathAge += dt;
      return;
   };
    position.add(position, rotatedVelocity.transformQuat(velocity,
        upOrientation), dt);
    upOrientation.rotateY(rYaw * dt);

    if (Math.random() < .02) {
      rYaw = Util.random()*2 - 1;
    }

    if (position.x < -100 || position.x > 100
        || position.z < -100 || position.z > 100) {
      upOrientation.rotateY(Util.PI);
    }
    
    position.clampX(-100, 100);
    position.clampZ(-100, 100);
  }

  @Override
  public int getBufferSize() {
    return 57;
  }

  @Override
  public ByteBuffer writeToBuffer(ByteBuffer buffer) {
    super.writeToBuffer(buffer);
    upOrientation.writeToBuffer(buffer);
    color.writeToBuffer(buffer);
    return buffer;
  }


  @Override
  public boolean isAlive() {
    return alive;
  }

  @Override
  public void die() {
    alive = false;
  }

  @Override
  public void onKilled(Sentient sentient) {
  }
}
