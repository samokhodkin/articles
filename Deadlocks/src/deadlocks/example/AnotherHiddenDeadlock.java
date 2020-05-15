package deadlocks.example;

import java.util.Hashtable;

/**
 * Deadlock caused by an explicit lock and a hidden one.
 * 
 * The explicit lock is used in the updateVersion().
 * The hidden one is used in all the Hashtable's methods.
 * The put() and increment() methods put these locks into entanglement.
 * 
 * Note the API call in the callback.
 * Avoid doing this in a real code.
 * 
 * @author S.Samokhodkin
 */

public class AnotherHiddenDeadlock {
   private Hashtable<String, Long> db = new Hashtable<>();
   private long version;
   
   public void put(String key, long value){
      updateVersion(key);
      db.put(key, value);
   }
   
   public void increment(String key){
      db.computeIfPresent(key, (k,v)->{
         updateVersion(k);
         return v+1;
      });
   }
   
   private synchronized void updateVersion(String key){
      db.put(key+".version", version++);
   }
   
   public static void main(String[] args) {
      int N=5000;
      AnotherHiddenDeadlock obj = new AnotherHiddenDeadlock();
      
      new Thread(() -> {
         for(int i=N; i-->0;) obj.put("key", i);
         System.out.println("puts done");
      }).start();
      
      new Thread(() -> {
         for(int i=N; i-->0;) obj.increment("key");
         System.out.println("increments done");
      }).start();
   }
}
