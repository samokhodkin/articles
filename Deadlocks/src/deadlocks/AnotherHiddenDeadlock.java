package deadlocks;

import java.util.Hashtable;

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
}
