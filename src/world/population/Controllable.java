package world.population;

public interface Controllable {
  public void onKeyEvent(boolean isKeyDown, byte keyCode);
  public void onMouseMove(float dX, float dY);
}
