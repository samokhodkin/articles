package deadlocks.example;

/**
 * Example of fine-grained locking with lock ordering
 * 
 * @author S.Samokhodkin
 */

public class FineGrainedLocking {
   long[] data;
   final Object[] locks;
   
   public FineGrainedLocking(int size){
      data = new long[size];
      locks = new Object[size];
      for(int i = size; i-->0; ) locks[i] = new Object();
   }
   
   void add(int to, long amount){
      // protective block
      synchronized (locks[to]) {
         
         //actual transaction
         data[to] += amount;
         
      }
   }
   
   void transfer(int from, int to, long amount){
      // select and order the locks;
      // lock with smaller index comes first
      Object lock1 = locks[Math.min(from, to)]; 
      Object lock2 = locks[Math.max(from, to)]; 
      
      // protective block
      synchronized (lock1) {
         synchronized (lock2) {
            
            //actual transaction
            data[from] -= amount;
            data[to] += amount;
            
         }
      }
   }
}
