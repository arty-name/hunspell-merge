// $Id$

package hunspell.merge;

public class HashDataVector<T extends HashData> extends HashVector<T> {

  public synchronized void add(T t) {
    super.add(t.getHashString(), t);
  }

  public synchronized boolean contains(T data) {
    return hash.containsKey(data.getHashString());
  }

  public synchronized T get(T value) {
    return hash.get(value.getHashString());
  }

  public synchronized void addAll(HashVector<T> data) {
    for (T value : data.values) {
      add(value);
    }
  }
}
