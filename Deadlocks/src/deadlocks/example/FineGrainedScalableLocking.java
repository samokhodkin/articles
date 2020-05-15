package deadlocks.example;

/**
 * Example of fine-grained locking with lock pooling
 * 
 * @author S.Samokhodkin
 */

public class FineGrainedScalableLocking {
   static final int POOL_SIZE = 1<<8;
   static final int POOL_MASK = POOL_SIZE-1;
   
   final long[] data;
   final Object[] lockPool = new Object[POOL_SIZE];
   
   public FineGrainedScalableLocking(int size){
      data = new long[size];
      for(int i = POOL_SIZE; i-->0; ) lockPool[i] = new Object();
   }
   
   void add(int to, long amount){
      // map data index into pooled lock
      Object lock = lockPool[to & POOL_MASK];
      
      // protective block
      synchronized (lock) {
         
         // actual transaction
         data[to] += amount;
         
      }
   }
   
   void transfer(int from, int to, long amount){
      // 1. map data indices into locks 
      // 2. sort these locks according to *mapped* indices
      Object lock1 = lockPool[Math.min(from & POOL_MASK, to & POOL_MASK)];
      Object lock2 = lockPool[Math.max(from & POOL_MASK, to & POOL_MASK)];
      
      // protective block
      synchronized (lock1) {
         synchronized (lock2) {
            
            // actual transaction
            data[from] -= amount;
            data[to] += amount;
            
         }
      }
   }
}