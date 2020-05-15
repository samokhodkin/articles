package deadlocks.toybank.impl;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import deadlocks.toybank.api.BalanceOverflow;
import deadlocks.toybank.api.InsufficientBalance;
import deadlocks.toybank.api.NotFound;

/**
 * Partially concurrent implementation using fine-grained synchronization
 * 
 * @author S.Samokhodkin
 */

public class ToyBankOrdered extends ToyBankBase {
   private ReadWriteLock rwLock = new ReentrantReadWriteLock();
   private Lock readLock = rwLock.readLock();
   private Lock writeLock = rwLock.writeLock();
   
   @Override
   public Long createAccount(long initialDeposit)  throws BalanceOverflow{
      if(initialDeposit > MAX_BALANCE) throw new BalanceOverflow(null, initialDeposit, 0); 
      
      writeLock.lock();
      try{
         Long id = idGenerator++;
         
         db.put(id, new long[]{initialDeposit});
         
         return id;
      }
      finally{
         writeLock.unlock();
      }
   }

   @Override
   public long deleteAccount(Long accountId) throws NotFound{
      writeLock.lock();
      try{
         long[] value = db.remove(accountId);
         if(value == null) throw new IllegalArgumentException("account not found: "+accountId);
         
         return value[0];
      }
      finally{
         writeLock.unlock();
      }
   }

   @Override
   public void deposit(Long accountId, long amount) throws NotFound, BalanceOverflow{
      if(amount < 0) throw new IllegalArgumentException("negative amount: " + amount);
      
      readLock.lock();
      try{
         long[] value = db.get(accountId);
         if(value == null) throw new NotFound(accountId);
         
         synchronized (value) {
            if(amount > MAX_BALANCE-value[0]) throw new BalanceOverflow(accountId, amount, value[0]);
            value[0] += amount;
         }
      }
      finally{
         readLock.unlock();
      }
   }

   @Override
   public void withdraw(Long accountId, long amount) throws NotFound, InsufficientBalance{
      if(amount < 0) throw new IllegalArgumentException("negative amount: " + amount);
      
      readLock.lock();
      try{
         long[] value = db.get(accountId);
         if(value == null) throw new NotFound(accountId);
         
         synchronized (value) {
            if(amount > value[0]) throw new InsufficientBalance(accountId, amount, value[0]);
            value[0] -= amount;
         }
      }
      finally{
         readLock.unlock();
      }
   }

   @Override
   public void transfer(Long srcId, Long dstId, long amount) throws NotFound, InsufficientBalance, BalanceOverflow{
      if(amount < 0) throw new IllegalArgumentException("negative amount: " + amount);
      if(srcId.longValue() == dstId) return;
      
      readLock.lock();
      try{
         long[] srcValue = db.get(srcId);
         if(srcValue == null) throw new NotFound(srcId);
         
         long[] dstValue = db.get(dstId);
         if(dstValue == null) throw new NotFound(dstId);
         
         // Use the value arrays as locks. Locking precedence is determined by the account id 
         // to ensure the global locking order. The lock with the lower id comes first.
         Object lock1 = srcId < dstId? srcValue: dstValue;
         Object lock2 = srcId < dstId? dstValue: srcValue;
         
         synchronized (lock1) {
            synchronized (lock2) {
               if(amount > srcValue[0]) throw new InsufficientBalance(srcId, amount, srcValue[0]);
               if(amount > MAX_BALANCE-dstValue[0]) throw new BalanceOverflow(dstId, amount, dstValue[0]);
               
               srcValue[0] -= amount;
               dstValue[0] += amount;
            }
         }
      }
      finally{
         readLock.unlock();
      }
   }

   @Override
   public long getBalance(Long accountId) throws NotFound{
      readLock.lock();
      try{
         long[] value = db.get(accountId);
         if(value == null) throw new NotFound(accountId);
         
         synchronized (value) {
            return value[0];
         }
      }
      finally{
         readLock.unlock();
      }
   }
   
   @Override
   public long totalValue(){
      writeLock.lock();
      try{
         return super.totalValue();
      }
      finally{
         writeLock.unlock();
      }
   }
}
