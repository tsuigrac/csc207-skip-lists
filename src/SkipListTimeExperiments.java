public class SkipListTimeExperiments {
  public static void main(String args[]) {
    logTime(1);
    logTime(10);
    logTime(100);
    logTime(1000);
    logTime(2500);
    logTime(5000);
    logTime(7000);
    logTime(10000);
  }


  public static void logTime(int size) {
    SkipList<Integer, Integer> lst = new SkipList<Integer, Integer>();
    System.out.println("Experiment for size " + size + ": ");


    System.out.print("Set: ");
    long startTime = System.nanoTime();
    for (int i = 0; i < size; i++) {
      lst.set(i, i);
    }
    long endTime = System.nanoTime();
    System.out.println((endTime - startTime));


    System.out.print("Get: ");
    startTime = System.nanoTime();
    for (int i = 0; i < size; i++) {
      lst.get(i);
    }
    endTime = System.nanoTime();
    System.out.println((endTime - startTime));

    System.out.print("Remove: ");
    startTime = System.nanoTime();
    for (int i = 0; i < size; i++) {
      lst.remove(i);
    }
    endTime = System.nanoTime();
    System.out.println((endTime - startTime));

    System.out.println();
    System.out.println();
  }
}

// https://stackoverflow.com/questions/180158/how-do-i-time-a-methods-execution-in-java
