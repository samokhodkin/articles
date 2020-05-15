package deadlocks.toybank;

import java.util.HashMap;
import java.util.Map;

import deadlocks.toybank.exception.*;

public abstract class ToyBankBase implements ToyBank{
   protected final Map<Long,long[]> db=new HashMap<>();
   protected long idGenerator = 1;
   
   public Long createAccount(long initialDeposit) throws BalanceOverflow{
      if(initialDeposit < 0) throw new IllegalArgumentException("negative initialDeposit: " + initialDeposit);
      if(initialDeposit > MAX_BALANCE) throw new BalanceOverflow(null, initialDeposit, 0);
      
      Long id = idGenerator++; 
      db.put(id, new long[]{initialDeposit});
      
      return id;
   }
   
   public long deleteAccount(Long accountId) throws NotFound{
      long[] value = db.remove(accountId);
      if(value == null) throw new NotFound(accountId);
      
      return value[0];
   }
   
   public void deposit(Long accountId, long amount) throws NotFound, BalanceOverflow{
      if(amount < 0) throw new IllegalArgumentException("negative amount: " + amount);
      
      long[] value = db.get(accountId);
      if(value == null) throw new NotFound(accountId);
      if(amount > MAX_BALANCE-value[0]) throw new BalanceOverflow(accountId, amount, value[0]);
      
      value[0] += amount;
   }
   
   public void withdraw(Long accountId, long amount) throws NotFound, InsufficientBalance{
      if(amount < 0) throw new IllegalArgumentException("negative amount: " + amount);
      
      long[] value = db.get(accountId);
      if(value == null) throw new NotFound(accountId);
      if(amount > value[0]) throw new InsufficientBalance(accountId, amount, value[0]);
      
      value[0] -= amount;
   }
   
   public void transfer(Long srcId, Long dstId, long amount) throws NotFound, InsufficientBalance, BalanceOverflow{
      if(amount < 0) throw new IllegalArgumentException("negative amount: " + amount);
      
      long[] srcValue = db.get(srcId);
      if(srcValue == null) throw new NotFound(srcId);
      if(amount > srcValue[0]) throw new InsufficientBalance(srcId, amount, srcValue[0]);
      
      long[] dstValue = db.get(dstId);
      if(dstValue == null) throw new NotFound(dstId);
      if(amount > MAX_BALANCE-dstValue[0]) throw new BalanceOverflow(dstId, amount, dstValue[0]);
      
      srcValue[0] -= amount;
      dstValue[0] += amount;
   }
   
   public synchronized long getBalance(Long accountId) throws NotFound{
      long[] value = db.get(accountId);
      if(value == null) throw new NotFound(accountId);
      
      return value[0];
   }
   
   public long totalValue(){
      return db.values().stream().mapToLong(value->value[0]).sum();
   } 
}
