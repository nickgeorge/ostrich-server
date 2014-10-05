package world.population;

public interface Sentient extends Thing, Alive {

  public void die();

  public void onKilled(Sentient sentient);

}
