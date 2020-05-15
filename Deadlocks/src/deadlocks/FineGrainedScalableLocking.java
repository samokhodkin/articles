package deadlocks;

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
      int lockIndex = to & POOL_MASK;
      
      synchronized (lockPool[lockIndex]) {
         data[to] += amount;
      }
   }
   
   void transfer(int from, int to, long amount){
      Object lock1 = lockPool[Math.min(from & POOL_MASK, to & POOL_MASK)];
      Object lock2 = lockPool[Math.max(from & POOL_MASK, to & POOL_MASK)];
      
      synchronized (lock1) {
         synchronized (lock2) {
            data[from] -= amount;
            data[to] += amount;
         }
      }
   }
}