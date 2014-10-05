package world.population;

import java.awt.event.KeyEvent;
import java.nio.ByteBuffer;

import world.main.World;
import world.util.Quat;
import world.util.Util;
import world.util.Vec3;

public class Osterich extends AbstractThing
    implements Sentient, Controllable {

  private boolean[] keyMap = new boolean[128];
  private int[] keyMove = new int[3];

  private float initialSledgeAngle = Util.PI;
  private float finalSledgeAngle = Util.PI / 3;
  private float deltaSledgeAngle = finalSledgeAngle - initialSledgeAngle;
  private float totalSledgeTime = .5f;

  private float sledgeAngle = initialSledgeAngle;
  private float sledgeT = 0;
  private boolean sledging = false;

  public Quat color = Quat.randomColor(.5f);
  public Quat viewOrientation = new Quat();

  private Vec3 hitLocation = new Vec3();
  private Vec3 hitOffset = new Vec3(4, 0, -10);

  private World world;

  private float sensitivityX = .0035f;
  private float sensitivityY = .0035f;

  public float vRMag = 25;

  private Quat rotY = new Quat();
  private Vec3 upJ = new Vec3();
  
  public ControlMode controlMode = ControlMode.FREE;
      
  public enum ControlMode {
    FREE, FPS
  }
  public void setControlMode(ControlMode mode) {
    controlMode = mode;
  }

  public Osterich(World world) {
    this.world = world;
    position = new Vec3(0, 0, 0);
    velocity = new Vec3(0, 0, 0);
  }

  @Override
  public int getType() {
    return 47;
  }

  @Override
  public void advance(float dt) {
    super.advance(dt);
    int sum = Math.abs(keyMove[0]) + Math.abs(keyMove[2]);
    float factor = sum == 2 ? 1/Util.ROOT_2 : 1;
    velocity.set(
        factor * vRMag * (keyMove[0]),
        0,
        factor * vRMag * (keyMove[2]));
    if (sum > 0) {
      if (controlMode == ControlMode.FREE) {
        upOrientation.rotateY(Quat.IDENTITY, Util.PI + Util.atan2(velocity.x, velocity.z));
      } else {
        velocity.transformQuat(upOrientation);
      }
    }


    if (sledging) {
      advanceSledge(dt);
    }
  }

  private void advanceSledge(float dt) {
    if (sledgeT < totalSledgeTime / 2) {
      float coef = sledgeT / (totalSledgeTime / 2);
      sledgeAngle = initialSledgeAngle + deltaSledgeAngle * coef;
    } else {
      float coef = (sledgeT - totalSledgeTime / 2) / (totalSledgeTime / 2);
      sledgeAngle = finalSledgeAngle - deltaSledgeAngle * coef;
    }
    if (sledgeT > totalSledgeTime) {
      sledgeAngle = initialSledgeAngle;
      sledging = false;
      sledgeT = 0;
    }

    if (sledgeT > .125 && sledgeT < .175) {
      for (Sentient sentient : world.sentients) {
        if (sentient.isAlive() && sentient instanceof Fella) {
          Fella fella = (Fella) sentient;
          hitLocation.transformQuat(hitOffset, upOrientation);

          hitLocation.add(getCenter());
          float distance = fella.getCenter().distanceSquaredXZ(
               hitLocation);

          if (distance < 10) {
            world.addScore(this, 1);
            fella.die();
            fella.velocity = new Vec3();
            ((Fella) sentient).color = new Quat(color);
          }
        }
      }
    }

    sledgeT += dt;
  }

  @Override
  public int getBufferSize() {
    return 77;
  }

  @Override
  public ByteBuffer writeToBuffer(ByteBuffer buffer) {
    super.writeToBuffer(buffer);
    upOrientation.writeToBuffer(buffer);
    color.writeToBuffer(buffer);
    viewOrientation.writeToBuffer(buffer);
    buffer.putFloat(sledgeAngle);
    return buffer;
  }

  @Override
  public void onKeyEvent(boolean isKeyDown, byte keyCode) {
    if (isKeyDown && keyMap[keyCode]) return;

    keyMap[keyCode] = isKeyDown;
    switch (keyCode) {
      case KeyEvent.VK_A:
        keyMove[0] = isKeyDown ? -1
            : (keyMap[KeyEvent.VK_D] ? 1 : 0);
        break;
      case KeyEvent.VK_D:
        keyMove[0] = isKeyDown ? 1
            : (keyMap[KeyEvent.VK_A] ? -1 : 0);
        break;
      case KeyEvent.VK_W:
        keyMove[2] = isKeyDown ? -1
            : (keyMap[KeyEvent.VK_S] ? 1 : 0);
        break;
      case KeyEvent.VK_S:
        keyMove[2] = isKeyDown ? 1
            : (keyMap[KeyEvent.VK_W] ? -1 : 0);
        break;
      case KeyEvent.VK_SPACE:
        if (!sledging && isKeyDown) {
          sledging = true;
        }
    }
  }

  @Override
  public boolean isAlive() {
    return true;
  }

  @Override
  public void die() {}

  @Override
  public void onKilled(Sentient sentient) {}

  @Override
  public void onMouseMove(float dX, float dY) {
    if (controlMode == ControlMode.FREE) return; 
    rotY.setAxisAngle(
        upJ.transformQuat(Vec3.J, upOrientation),
        -dX * sensitivityX);

    upOrientation.multiply(rotY);

    float delta = Math.abs(1-upOrientation.magnitudeSquared());
    if (delta > .001) {
      upOrientation.calculateW();
    }

     if ((viewOrientation.x < 1/Util.ROOT_2 || dY > 0) &&
         (viewOrientation.x > -1/Util.ROOT_2 || dY < 0)) {
       viewOrientation.rotateX(
           -dY * sensitivityY);
     }
  }
}
