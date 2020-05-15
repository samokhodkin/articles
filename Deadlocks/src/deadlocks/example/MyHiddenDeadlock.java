package deadlocks.example;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Deadlock caused by hidden locks.
 * 
 * ConcurrentHashMap contains a number of synchronized bins.
 * Methods method1() and method2() put their locks into entanglement.
 * 
 * Note the API calls in the callbacks.
 * Doing this should be avoided in a real code. 
 * 
 * @author S.Samokhodkin
 */

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
   
   public static void main(String[] args) {
      int N=5000;
      MyHiddenDeadlock obj = new MyHiddenDeadlock();
      
      new Thread(() -> {
         for(int i=N; i-->0;) obj.method1();
         System.out.println("method1 done");
      }).start();
      
      new Thread(() -> {
         for(int i=N; i-->0;) obj.method2();
         System.out.println("method2 done");
      }).start();
   }
}
