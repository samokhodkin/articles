package deadlocks;

public class FineGrainedLocking {
   long[] data;
   final Object[] locks;
   
   public FineGrainedLocking(int size){
      data = new long[size];
      locks = new Object[size];
      for(int i = size; i-->0; ) locks[i] = new Object();
   }
   
   void add(int to, long amount){
      synchronized (locks[to]) {
         data[to] += amount;
      }
   }
   
   void transfer(int from, int to, long amount){
      Object lock1 = locks[Math.min(from, to)]; 
      Object lock2 = locks[Math.max(from, to)]; 
      
      synchronized (lock1) {
         synchronized (lock2) {
            data[from] -= amount;
            data[to] += amount;
         }
      }
   }
   
   long sum(int from, int to){
      long sum=0;
      for(int i=from; i<to; i++){
         sum+=data[i];
      }
      return sum;
   }
}
