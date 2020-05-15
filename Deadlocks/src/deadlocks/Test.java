package deadlocks;

public class Test {
   public static void main(String[] args) throws Exception{
      int size = 1<<26; //64M
//      FineGrainedLocking subject = new FineGrainedLocking(size); 
      FineGrainedScalableLocking subject = new FineGrainedScalableLocking(size); 
      
      System.out.println("time\ttotal count\tcurr speed\tavg speed\tused mem");
      
      final long t0=System.currentTimeMillis();
      long t=t0;
      long totalCount=0;
      for(int n=30; n-->0;){ //;;){
         for(int i=size; i-->0;){
            subject.transfer(i, size-1-i, i);
         }
         
         long t1=System.currentTimeMillis();
         long dt = t1 - t;
         t = t1;
         long usedMemory= Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
         totalCount += size;
         float currSpeed = size*1000f/dt;
         float avgSpeed = totalCount*1000f/(t-t0);
         
         System.out.printf("%.3f\t%12d\t%.3e\t%.3e\t%12d\n", 0.001f*(t1-t0), totalCount, currSpeed, avgSpeed, usedMemory);
      }
      System.out.println("sum="+subject.sum(1000, 1005));
      System.out.println("sum="+subject.sum(100, 105));
      System.out.println("sum="+subject.sum(0, size));
   }
}
