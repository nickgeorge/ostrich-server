package world.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ControlledList<TYPE> implements Iterable<TYPE> {
  protected List<TYPE> contents = new ArrayList<>();
  protected List<TYPE> addList = new ArrayList<>();
  protected List<TYPE> removeList = new ArrayList<>();

  public void update() {
    contents.addAll(addList);
    contents.removeAll(removeList);
    addList.clear();
    removeList.clear();
  }
  
  public void add(TYPE type) {
    addList.add(type);
  }

  public void addAll(List<TYPE> type) {
    addList.addAll(type);
  }

  public void remove(TYPE typeList) {
    removeList.add(typeList);
  }

  public void removeAll(List<TYPE> typeList) {
    addList.removeAll(typeList);
  }

  public TYPE get(int i) {
    return contents.get(i);
  }

  public int size() {
    return contents.size();
  }

  public int addSize() {
    return addList.size();
  }

  public int removeSize() {
    return removeList.size();
  }

  @Override
  public Iterator<TYPE> iterator() {
    return contents.iterator();
  }

}
