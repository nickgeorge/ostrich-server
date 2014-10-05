package world.population;

import world.util.Vec3;

public class Star extends AbstractThing {

  public Star() {
    position = new Vec3(0, 0, 0);
    velocity = new Vec3(Math.random() * 10 - 5, 0, Math.random() * 10 - 5);
  }

  @Override
  public int getType() {
    return 48;
  }

  @Override
  public void advance(float dt) {

    super.advance(dt);

    if (Math.random() < .1) {
      // this.color = [
      // Math.random(),
      // Math.random(),
      // Math.random(),
      // 1
      // ];

      float speedX = (float) (Math.random() * 10);
      float speedZ = (float) Math.sqrt(100 - speedX * speedX);

      if (Math.random() < .5)
        speedX = -speedX;
      if (Math.random() < .5)
        speedZ = -speedZ;

      this.velocity.x = speedX;
      this.velocity.z = speedZ;
    }

  }
}
