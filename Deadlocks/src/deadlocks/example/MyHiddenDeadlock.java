package deadlocks.example;

import java.util.concurrent.ConcurrentHashMap;

public class MyHiddenDeadlock {
   private final ConcurrentHashMap<Integer, Integer> map = new ConcurrentHashMap<>();
   {
      map.put(1, 0);
      map.put(2, 0);
   }
   
   public void method1(){
      map.compute(1, (key,value)->{
         map.put(2, 1);
         return value;
      });
   }
   
   public void method2(){
      map.compute(2, (key,value)->{
         map.put(1, 1);
         return value;
      });
   }
}
