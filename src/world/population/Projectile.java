package world.population;

public interface Projectile extends Thing, Alive {
  public Sentient getParent();

  public void detonate();
}
