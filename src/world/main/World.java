package world.main;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import world.population.Effect;
import world.population.Fella;
import world.population.Projectile;
import world.population.Sentient;
import world.population.Thing;
import world.util.ThingList;

public class World {

  public final FellaServer server;
  public final Map<Integer, Thing> idMap = new HashMap<>();

  public final ThingList<Sentient> sentients = new ThingList<>();
  public final ThingList<Projectile> projectiles = new ThingList<>();
  public final ThingList<Effect> effects = new ThingList<>();

  private int nextId = 0;

  public World(FellaServer server) {
    this.server = server;
  }

  public void populate() {
    this.add(new Fella());
    updateLists();
  }

  public void advance(float deltaT) {
    if (Math.random() < .004) {
      int fellaCount = 0;
      for (Sentient thing : sentients) {
        if (thing instanceof Fella && thing.isAlive()) fellaCount++;
      }
      if (fellaCount < 10) this.add(new Fella());
    }
    for (Thing sentient : sentients) {
      sentient.advance(deltaT);
    }
    for (Projectile projectile : projectiles) {
      projectile.advance(deltaT);
    }
    for (Effect effect : effects) {
     effect.advance(deltaT);
    }
  }

  public void updateLists() {
    sentients.update();
    projectiles.update();
    effects.update();
  }

  public int add(Thing thing) {
    thing.setId(nextId++);
    idMap.put(thing.getId(), thing);

    if (thing instanceof Sentient) {
      sentients.add((Sentient) thing);
    }
    if (thing instanceof Projectile) {
      projectiles.add((Projectile) thing);
    }
    if (thing instanceof Effect) {
      effects.add((Effect) thing);
    }

    return thing.getId();
  }

  public void remove(Thing thing) {
    idMap.remove(thing.getId());

    if (thing instanceof Sentient) {
      sentients.remove((Sentient) thing);
    }
    if (thing instanceof Projectile) {
      projectiles.remove((Projectile) thing);
    }
    if (thing instanceof Effect) {
      effects.remove((Effect) thing);
    }
  }

  public int getBufferSize() {
    return sentients.getBufferSize()
        + projectiles.getBufferSize()
        + effects.getBufferSize();
  }

  public void writeToBuffer(ByteBuffer buffer) {
    sentients.writeToBuffer(buffer);
    buffer.put(MessageCode.SYNC);
    projectiles.writeToBuffer(buffer);
    buffer.put(MessageCode.SYNC);
    effects.writeToBuffer(buffer);
    buffer.put(MessageCode.SYNC);
  }



  public void addScore(Thing thing, int score) {
    server.addScore(thing, score);
  }
}
