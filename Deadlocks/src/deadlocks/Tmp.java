package deadlocks;

import java.util.HashMap;

public class Tmp {
   public static void main(String[] args) throws Exception{
      HashMap<Integer,Boolean> map = new HashMap<>();
      
      System.out.println("time\tsize\tcurr speed\tavg speed\tused mem");
      
      long t0=System.currentTimeMillis(), t=t0;
      int lastSize=0;
      for(int i=0; i<Integer.MAX_VALUE; i++){
         map.put(i, delay(i));
         long t1=System.currentTimeMillis();
//if(map.size() - lastSize > 50_000){
         if(t1-t > 3000){
            float avgSpeed = map.size() * 1000f / (t1-t0);
            float currSpeed = (map.size()-lastSize) * 1000f / (t1-t);
            long usedMemory= Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            //System.out.println((0.001f*(t1-t0)) + "\t" + map.size() + "\t" + currSpeed + "\t" + avgSpeed+"\t"+usedMemory);
            System.out.printf("%.3f\t%12d\t%.3e\t%.3e\t%12d\n", 0.001f*(t1-t0), map.size(), currSpeed, avgSpeed, usedMemory);
            t=t1;
            lastSize=map.size();
         }
      }
   }
   
   private static boolean delay(int i){
      int v=1;
      for(int k=1000; k-->0;) v*=i;
      return (v&1) == 1;
   }
}
