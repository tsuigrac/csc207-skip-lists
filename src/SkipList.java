import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;
import java.util.function.BiConsumer;

/**
 * An implementation of skip lists.
 */
public class SkipList<K, V> implements SimpleMap<K, V> {

  // +-----------+---------------------------------------------------
  // | Constants |
  // +-----------+

  /**
   * The initial height of the skip list.
   */
  static final int INITIAL_HEIGHT = 16;

  // +---------------+-----------------------------------------------
  // | Static Fields |
  // +---------------+

  static Random rand = new Random();

  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  /**
   * Pointers to all the front elements.
   */
  ArrayList<SLNode<K, V>> front;

  /**
   * The comparator used to determine the ordering in the list.
   */
  Comparator<K> comparator;

  /**
   * The number of values in the list.
   */
  int size;

  /**
   * The current height of the skiplist.
   */
  int height;

  /**
   * The probability used to determine the height of nodes.
   */
  double prob = 0.5;

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create a new skip list that orders values using the specified comparator.
   */
  public SkipList(Comparator<K> comparator) {
    this.front = new ArrayList<SLNode<K, V>>(INITIAL_HEIGHT);
    for (int i = 0; i < INITIAL_HEIGHT; i++) {
      front.add(null);
    } // for
    this.comparator = comparator;
    this.size = 0;
    this.height = 0;
  } // SkipList(Comparator<K>)

  /**
   * Create a new skip list that orders values using a not-very-clever default comparator.
   */
  public SkipList() {
    this((k1, k2) -> k1.toString().compareTo(k2.toString()));
  } // SkipList()


  // +-------------------+-------------------------------------------
  // | SimpleMap methods |
  // +-------------------+

  @Override
  public V set(K key, V value) {
    if (key == null) {
      throw new NullPointerException("Key cannot be null");
    }

    // Special case: first element in list
    if (this.height == 0) {
      int newLevel = randomLevel();
      // Make and insert new node
      SLNode<K, V> newNode = new SLNode<K, V>(key, value, newLevel);
      this.height = newLevel;
      for(int i = 0; i < newLevel; i++) {
        front.set(i, newNode);
      }
      return null;
    }

    SLNode<K, V>[] update = new SLNode[SkipList.INITIAL_HEIGHT];
    SLNode<K,V> current = new SLNode<K,V>(null, null, this.height);
    current.next = front;
    for (int i = this.height - 1; i >= 0; i--) {
      while (current.next.get(i) != null
          && this.comparator.compare(current.next.get(i).key, key) < 0) {
        current = current.next.get(i);
      }
      update[i] = current;
    }
    if (current.next != null) {
      current = current.next.get(0);
    }
    // Replace existing key
    if (current != null && this.comparator.compare(current.key, key) == 0) {
      V cache = current.value;
      current.value = value;
      return cache;
    }
    int newLevel = randomLevel();
    // Make and insert new node
    SLNode<K, V> newNode = new SLNode<K, V>(key, value, newLevel);
    if (newLevel > this.height) {
      for (int i = this.height - 1; i < newLevel; i++) {
        front.set(i, newNode);
      }
    }

    for (int i = 0; i < newLevel; i++) {
      if (i < this.height && update[i].next != null) {
        newNode.next.set(i, update[i].next.get(i));
      } else {
        newNode.next.set(i, null);
      }
    }
    this.height = newLevel;
    this.size++;
    return null;
  }// set(K,V)

  private int randomLevel() {
    int newLevel = 1;
    int random = rand.nextInt(1);
    while (random < this.prob) {
      newLevel++;
      random = rand.nextInt(1);
    }
    return Math.min(newLevel, SkipList.INITIAL_HEIGHT);
  }

  @Override
  public V get(K key) {
    if (key == null) {
      throw new NullPointerException("null key");
    } // if
    SLNode<K, V> found = search(key);
    if (found != null) {
      return found.value;
    } else {
      throw new IndexOutOfBoundsException("key is not in list");
    }
  } // get(K,V)

  @Override
  public int size() {
    return this.size;
  } // size()

  @Override
  public boolean containsKey(K key) {
    return search(key) != null;
  } // containsKey(K)

  @Override
  public V remove(K key) {
    if (key == null) {
      throw new NullPointerException("Key cannot be null");
    }
    V cache = null;
    SLNode<K, V> update[] = new SLNode[SkipList.INITIAL_HEIGHT];
    SLNode<K, V> current = new SLNode<K,V>(null, null, this.height);
    current.next = front;
    for (int i = this.height - 1; i >= 0; i--) {
      while (current.next.get(i) != null
          && this.comparator.compare(current.next.get(i).key, key) < 0) {
        current = current.next.get(i);
      }
      update[i] = current;
    }
    if (current.next != null) {
      current = current.next.get(0);
    }
    if (current != null && this.comparator.compare(current.key, key) == 0) {
      cache = current.value;
      for (int i = 0; i < this.height; i++) {
        if (update[i].next.get(i) != current) {
          break;
        }
        update[i].next.set(i, current.next.get(i));
      }
      while (this.height > 0 && this.front.get(this.height - 1) == null) {
        this.height--;
      }
      size--;
      return cache;
    } else {
      throw new IndexOutOfBoundsException("key not found");
    }
  } // remove(K)


  // HELPER METHOD
  public SLNode<K, V> search(K key) {
    SLNode<K, V> current = this.front.get(this.height - 1);
    for (int i = this.height - 1; i >= 0; i--) {
      while (current.next.get(i) != null && comparator.compare(current.next.get(i).key, key) < 0) {
        current = current.next.get(i);
      }
    }
    current = current.next.get(0);
    if (comparator.compare(current.key, key) == 0) {
      return current;
    } else {
      return null;
    }
  }

  @Override
  public Iterator<K> keys() {
    return new Iterator<K>() {
      Iterator<SLNode<K, V>> nit = SkipList.this.nodes();

      @Override
      public boolean hasNext() {
        return nit.hasNext();
      } // hasNext()

      @Override
      public K next() {
        return nit.next().key;
      } // next()

      @Override
      public void remove() {
        nit.remove();
      } // remove()
    };
  } // keys()

  @Override
  public Iterator<V> values() {
    return new Iterator<V>() {
      Iterator<SLNode<K, V>> nit = SkipList.this.nodes();

      @Override
      public boolean hasNext() {
        return nit.hasNext();
      } // hasNext()

      @Override
      public V next() {
        return nit.next().value;
      } // next()

      @Override
      public void remove() {
        nit.remove();
      } // remove()
    };
  } // values()

  @Override
  public void forEach(BiConsumer<? super K, ? super V> action) {
    SLNode<K,V> current = front.get(0);
    while(current != null) {
      action.accept(current.key, current.value);
      current = current.next.get(0);
    }
  } // forEach

  // +----------------------+----------------------------------------
  // | Other public methods |
  // +----------------------+


  /**
   * Print some links (for dump).
   */
  void printLinks(PrintWriter pen, String leading) {
    pen.print(leading);
    for (int level = 0; level < this.height; level++) {
      pen.print(" |");
    } // for
    pen.println();
  } // printLinks


  /**
   * Dump the list to some output location.
   */
  public void dump(PrintWriter pen) {
    String leading = "          ";

    SLNode<K, V> current = front.get(0);

    // Print some X's at the start
    pen.print(leading);
    for (int level = 0; level < this.height; level++) {
      pen.print(" X");
    } // for
    pen.println();
    printLinks(pen, leading);

    while (current != null) {
      // Print out the key as a fixed-width field.
      // (There's probably a better way to do this.)
      String str;
      if (current.key == null) {
        str = "<null>";
      } else {
        str = current.key.toString();
      } // if/else
      if (str.length() < leading.length()) {
        pen.print(leading.substring(str.length()) + str);
      } else {
        pen.print(str.substring(0, leading.length()));
      } // if/else

      // Print an indication for the links it has.
      for (int level = 0; level < current.next.size(); level++) {
        pen.print("-*");
      } // for
      // Print an indication for the links it lacks.
      for (int level = current.next.size(); level < this.height; level++) {
        pen.print(" |");
      } // for
      pen.println();
      printLinks(pen, leading);

      current = current.next.get(0);
    } // while

    // Print some O's at the start
    pen.print(leading);
    for (int level = 0; level < this.height; level++) {
      pen.print(" O");
    } // for
    pen.println();

  } // dump(PrintWriter)


  // +---------+-----------------------------------------------------
  // | Helpers |
  // +---------+

  /**
   * Pick a random height for a new node.
   */
  int randomHeight() {
    int result = 1;
    while (rand.nextDouble() < prob) {
      result = result + 1;
    }
    return result;
  } // randomHeight()

  /**
   * Get an iterator for all of the nodes. (Useful for implementing the other iterators.)
   */
  Iterator<SLNode<K, V>> nodes() {
    return new Iterator<SLNode<K, V>>() {

      /**
       * A reference to the next node to return.
       */
      SLNode<K, V> next = SkipList.this.front.get(0);

      @Override
      public boolean hasNext() {
        return this.next != null;
      } // hasNext()

      @Override
      public SLNode<K, V> next() {
        if (this.next == null) {
          throw new IllegalStateException();
        }
        SLNode<K, V> temp = this.next;
        this.next = this.next.next.get(0);
        return temp;
      } // next();
    }; // new Iterator
  } // nodes()

  // +---------+-----------------------------------------------------
  // | Helpers |
  // +---------+

} // class SkipList


/**
 * Nodes in the skip list.
 */
class SLNode<K, V> {

  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  /**
   * The key.
   */
  K key;

  /**
   * The value.
   */
  V value;

  /**
   * Pointers to the next nodes.
   */
  ArrayList<SLNode<K, V>> next;

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create a new node of height n with the specified key and value.
   */
  public SLNode(K key, V value, int n) {
    this.key = key;
    this.value = value;
    this.next = new ArrayList<SLNode<K, V>>(n);
    for (int i = 0; i < n; i++) {
      this.next.add(null);
    } // for
  } // SLNode(K, V, int)

  // +---------+-----------------------------------------------------
  // | Methods |
  // +---------+



} // SLNode<K,V>
