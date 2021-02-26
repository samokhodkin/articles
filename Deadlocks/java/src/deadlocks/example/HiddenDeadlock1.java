package deadlocks.example;

import java.util.Hashtable;

/**
 * Example of a deadlock caused by a hidden lock.
 * 
 * There is an explicit lock in the updateVersion(). There is also a hidden one in the Hashtable's methods. 
 * The put() and increment() methods put these locks into entanglement.
 */

public class HiddenDeadlock1 {
   private Hashtable<String, Long> db = new Hashtable<>();
   private long version;

   public void put(String key, long value) {
      updateVersion(key);
      db.put(key, value);
   }

   public void increment(String key) {
      db.computeIfPresent(key, (k, v) -> {
         updateVersion(k);
         return v + 1;
      });
   }

   private synchronized void updateVersion(String key) {
      db.put(key + ".version", version++);
   }

   public static void main(String[] args) {
      int N = 5000;
      HiddenDeadlock1 obj = new HiddenDeadlock1();

      new Thread(() -> {
         for (int i = N; i-- > 0;)
            obj.put("key", i);
         
         System.out.println("Puts done");
      }).start();

      new Thread(() -> {
         for (int i = N; i-- > 0;)
            obj.increment("key");
         
         System.out.println("Increments done");
      }).start();
   }
}
