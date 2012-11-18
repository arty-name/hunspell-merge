// $Id$

package hunspell.merge;

import java.util.HashMap;
import java.util.Vector;

public class HashVector<T> {

  protected final Vector<T> values = new Vector<T>();
  protected final HashMap<String, T> hash = new HashMap<String, T>();

  public synchronized void add(String hashString, T t) {
    if (t == null) {
      return;
    }
    hash.put(hashString, t);
    values.add(t);
  }

  public synchronized void remove(T t) {
    if (t == null) {
      return;
    }
    hash.remove(t);
    values.remove(t);
  }

  public synchronized boolean contains(String key) {
    return hash.containsKey(key);
  }

  public synchronized T get(String key) {
    return hash.get(key);
  }

  public synchronized Vector<T> values() {
    return values;
  }

  public synchronized int size() {
    return values.size();
  }

  public synchronized T elementAt(int i) {
    return values.elementAt(i);
  }

  public synchronized void clear() {
    values.clear();
    hash.clear();
  }

  public boolean isEmpty() {
    return values.isEmpty();
  }
}
