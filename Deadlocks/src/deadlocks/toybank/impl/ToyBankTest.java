package deadlocks.toybank.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import deadlocks.toybank.api.ToyBank;

public class ToyBankTest {
   static long initialDeposit=1000;
   static int numAccs=100;
   static int numRepeats=500_000;
   static int threadsPerTransaction = 2;
   
   static AtomicLong deposited = new AtomicLong();
   static AtomicLong successfulDeposits = new AtomicLong();
   
   static AtomicLong withdrawn = new AtomicLong();
   static AtomicLong successfulWithdrawals = new AtomicLong();
   
   static AtomicLong transferred = new AtomicLong();
   static AtomicLong successfulTransfers = new AtomicLong();
   
   public static void main(String[] args) throws Exception{
//      ToyBank bank = new ToyBankCoarse();
      ToyBank bank = new ToyBankOrdered();
      
      for(int i=numAccs; i-->0;){
         bank.createAccount(initialDeposit);
      }
      
      long initialTotalValue = bank.totalValue();
      
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
      
      System.out.println("Wait, this may take few minutes");
      
      System.out.println("deposited="+deposited);
      System.out.println("withdrawn="+withdrawn);
      System.out.println("transferred="+transferred);
      
      System.out.println("successfulDeposits="+successfulDeposits);
      System.out.println("successfulWithdrawals="+successfulWithdrawals);
      System.out.println("successfulTransfers="+successfulTransfers);
      
      long expectedTotalValue = initialTotalValue+deposited.get()-withdrawn.get();
      System.out.println("expected totalValue="+expectedTotalValue);
      System.out.println("actual totalValue="+bank.totalValue());
      if(expectedTotalValue != bank.totalValue()) throw new AssertionError("Thread-safety broken!");
      
      long totalOps = numAccs*numRepeats*threadsPerTransaction*3;
      long successfulOps = successfulDeposits.get() + successfulWithdrawals.get() + successfulTransfers.get();
      
      System.out.println("total time: " + dt);
      System.out.println("avg speed: " + (1000f*totalOps/dt) + " op/sec");
      System.out.println("avg succsess: " + (100f*successfulOps/totalOps) + "%");
   }

   static void runDeposits(ToyBank bank){
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
   
   static void runWithdrawals(ToyBank bank){
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
   
   static void runTransfers(ToyBank bank){
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
