package deadlocks.mockbank;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Tests thread-safety and performance of the MockBank implementations. 
 * Three operations (deposit, withdraw, transfer) are run in parallel using configurable number of threads per operation. 
 * As not all the operations are successful due to balance limits, each thread keeps track of the successful operations, 
 * and in the end all the numbers reported by threads are summed up and compared against the bank’s total,
 * thus making a check for integrity.
 */
public class MockBankCrashTest {
   static long initialDeposit=1000;
   static int numAccs=100;
   static int numRepeats=100_000;
   static int threadsPerTransaction = 2;
   
   static AtomicLong deposited = new AtomicLong();
   static AtomicLong successfulDeposits = new AtomicLong();
   
   static AtomicLong withdrawn = new AtomicLong();
   static AtomicLong successfulWithdrawals = new AtomicLong();
   
   static AtomicLong transferred = new AtomicLong();
   static AtomicLong successfulTransfers = new AtomicLong();
   
   public static void main(String[] args) throws Exception{
      MockBank[] banks = {
            new CoarseGrainedImpl(numAccs),
            new FineGrainedImpl(numAccs),
            new FineGrainedWithMappingImpl(numAccs, threadsPerTransaction*3)
      };
      
      for(MockBank bank : banks) {
         System.out.println("Testing " + bank.getClass().getSimpleName());
         System.out.println("Wait, this may take few minutes");
         
         long initialTotalValue = bank.getTotal();
         
         List<Thread> threads = new ArrayList<>();
         
         for(int i=threadsPerTransaction; i-->0; ){
            threads.add(new Thread(()->runDeposits(bank)));
            threads.add(new Thread(()->runWithdrawals(bank)));
            threads.add(new Thread(()->runTransfers(bank)));
         }
         
         long t0 = System.currentTimeMillis();
         
         for(int i=threads.size(); i-->0; ){
            threads.get(i).start();
         }
         
         for(int i=threads.size(); i-->0; ){
            threads.get(i).join();
         }
         
         long dt = System.currentTimeMillis() - t0;
         
         System.out.println("   deposited="+deposited);
         System.out.println("   withdrawn="+withdrawn);
         System.out.println("   transferred="+transferred);
         
         System.out.println("   successfulDeposits="+successfulDeposits);
         System.out.println("   successfulWithdrawals="+successfulWithdrawals);
         System.out.println("   successfulTransfers="+successfulTransfers);
         
         long expectedTotalValue = initialTotalValue+deposited.get()-withdrawn.get();
         System.out.println("   expected totalValue="+expectedTotalValue);
         System.out.println("   actual totalValue="+bank.getTotal());
         if(expectedTotalValue != bank.getTotal()) throw new AssertionError("Thread-safety broken!");
         
         long totalOps = numAccs*numRepeats*threadsPerTransaction*3;
         long successfulOps = successfulDeposits.get() + successfulWithdrawals.get() + successfulTransfers.get();
         
         System.out.println("   total time: " + dt);
         System.out.println("   avg speed: " + (1000f*totalOps/dt) + " op/sec");
         System.out.println("   avg succsess: " + (100f*successfulOps/totalOps) + "%");
         
         
         deposited.set(0);
         successfulDeposits.set(0);
         withdrawn.set(0);
         successfulWithdrawals.set(0);
         transferred.set(0);
         successfulTransfers.set(0);
         
         System.out.println("--------------------\r\n");
      }
   }

   static void runDeposits(MockBank bank){
      long sum=0;
      long successes=0;
      for(int n=numRepeats; n-->0;){
         for(long id=1; id<=100; id++){
            try {
               bank.deposit(id, id);
               sum+=id;
               successes++;
            }
            catch (Exception e) {}
         }
      }
      deposited.addAndGet(sum);
      successfulDeposits.addAndGet(successes);
   }
   
   static void runWithdrawals(MockBank bank){
      long sum=0;
      long successes=0;
      for(int n=numRepeats; n-->0;){
         for(long id=1; id<=numAccs; id++){
            try {
               bank.withdraw(id, id);
               sum+=id;
               successes++;
            }
            catch (Exception e) {}
         }
      }
      withdrawn.addAndGet(sum);
      successfulWithdrawals.addAndGet(successes);
   }
   
   static void runTransfers(MockBank bank){
      long sum=0;
      long successes=0;
      for(int n=numRepeats; n-->0;){
         for(long id=1; id<=numAccs; id++){
            try {
               bank.transfer(id, numAccs+1-id, id);
               sum+=id;
               successes++;
            }
            catch (Exception e) {}
         }
      }
      transferred.addAndGet(sum);
      successfulTransfers.addAndGet(successes);
   }
}
