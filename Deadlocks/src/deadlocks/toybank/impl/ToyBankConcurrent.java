package deadlocks.toybank.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import deadlocks.toybank.api.*;

/**
 * Fully concurrent implementation using fine-grained synchronization
 * 
 * @author S.Samokhodkin
 */

public class ToyBankConcurrent implements ToyBank{
   private static final long ACCOUNT_DELETED = -1;
   
   private final ConcurrentHashMap<Long, long[]> db = new ConcurrentHashMap<>();
   private final AtomicLong idGenerator = new AtomicLong(1);
   
   public Long createAccount(long initialDeposit) {
      if(initialDeposit > MAX_BALANCE) throw new IllegalArgumentException("initialDeposit exceeds MAX_BALANCE: "+initialDeposit); 
      
      Long id = idGenerator.getAndIncrement();
      db.put(id, new long[]{initialDeposit});
      
      return id;
   }

   public long deleteAccount(Long accountId) throws NotFound{
      long[] value = db.remove(accountId);
      if(value == null) throw new IllegalArgumentException("account not found: "+accountId);
      
      synchronized (value) {
         if(value[0] == ACCOUNT_DELETED) throw new NotFound(accountId);
         
         long v = value[0];
         value[0] = ACCOUNT_DELETED;
         
         return v;
      }
   }

   public void deposit(Long accountId, long amount) throws NotFound, BalanceOverflow{
      if(amount < 0) throw new IllegalArgumentException("negative amount: " + amount);
      
      long[] value = db.get(accountId);
      if(value == null) throw new NotFound(accountId);
      
      synchronized (value) {
         if(value[0] == ACCOUNT_DELETED) throw new NotFound(accountId);
         if(amount > MAX_BALANCE-value[0]) throw new BalanceOverflow(accountId, amount, value[0]);
         
         value[0] += amount;
      }
   }
   
   public void withdraw(Long accountId, long amount) throws NotFound, InsufficientBalance{
      if(amount < 0) throw new IllegalArgumentException("negative amount: " + amount);
      
      long[] value = db.get(accountId);
      if(value == null) throw new NotFound(accountId);
      
      synchronized (value) {
         if(value[0] == ACCOUNT_DELETED) throw new NotFound(accountId);
         if(amount > value[0]) throw new InsufficientBalance(accountId, amount, value[0]);
         
         value[0] -= amount;
      }
   }
   
   public void transfer(Long srcId, Long dstId, long amount) throws NotFound, InsufficientBalance, BalanceOverflow{
      if(amount < 0) throw new IllegalArgumentException("negative amount: " + amount);
      if(srcId.longValue() == dstId) return;
      
      long[] srcValue = db.get(srcId);
      if(srcValue == null) throw new NotFound(srcId);
      
      long[] dstValue = db.get(dstId);
      if(dstValue == null) throw new NotFound(dstId);
      
      Object lock1 = srcId < dstId? srcValue: dstValue;
      Object lock2 = srcId < dstId? dstValue: srcValue;
      
      synchronized (lock1) {
         synchronized (lock2) {
            if(srcValue[0] == ACCOUNT_DELETED) throw new NotFound(srcId);
            if(dstValue[0] == ACCOUNT_DELETED) throw new NotFound(dstId);
            if(amount > srcValue[0]) throw new InsufficientBalance(srcId, amount, srcValue[0]);
            if(amount > MAX_BALANCE-dstValue[0]) throw new BalanceOverflow(dstId, amount, dstValue[0]);
            
            srcValue[0] -= amount;
            dstValue[0] += amount;
         }
      }
   }
   
   public synchronized long getBalance(Long accountId) throws NotFound{
      long[] value = db.get(accountId);
      if(value == null) throw new NotFound(accountId);
      
      synchronized (value) {
         if(value[0] == ACCOUNT_DELETED) throw new NotFound(accountId);
         
         return value[0];
      }
   }
   
   public long totalValue(){
      return db.reduceValuesToLong(Long.MAX_VALUE, value->value[0], 0, (a,b)->a+b);
   } 
}
