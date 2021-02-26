package deadlocks.example;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Example of a deadlock caused by a hidden locks.
 * 
 * ConcurrentHashMap contains a number of synchronized buckets. 
 * Calls to swapValues(1,2) and swapValues(2,1) put the two buckets' locks into entanglement.
 */

public class HiddenDeadlock2 {
   private final Map<Integer, String> map = new ConcurrentHashMap<>();
   {
      map.put(1, "1");
      map.put(2, "2");
   }

   public void swapValues(Integer key1, Integer key2) {
      map.compute(key1, (k1, v1) -> {
         return map.put(key2, v1);
      });
   }

   public static void main(String[] args) {
      int N = 5000;
      HiddenDeadlock2 obj = new HiddenDeadlock2();

      new Thread(() -> {
         for (int i = N; i-- > 0;)
            obj.swapValues(1, 2);
         
         System.out.println("First thread done");
      }).start();

      new Thread(() -> {
         for (int i = N; i-- > 0;)
            obj.swapValues(2, 1);
         
         System.out.println("Second thread done");
      }).start();
   }
}
