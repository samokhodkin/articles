package deadlocks.toybank.impl;

import deadlocks.toybank.api.*;

/**
 * Implementation using coarse-grained synchronization
 * 
 * @author S.Samokhodkin
 */

public class ToyBankCoarse extends ToyBankBase {
   public synchronized Long createAccount(long initialDeposit) throws BalanceOverflow{
      return super.createAccount(initialDeposit);
   }
   
   public synchronized long deleteAccount(Long accountId) throws NotFound{
      return super.deleteAccount(accountId);
   }
   
   public synchronized void deposit(Long accountId, long amount) throws NotFound, BalanceOverflow{
      super.deposit(accountId, amount);
   }
   
   public synchronized void withdraw(Long accountId, long amount) throws NotFound, InsufficientBalance{
      super.withdraw(accountId, amount);
   }
   
   public synchronized void transfer(Long srcId, Long dstId, long amount) throws NotFound, InsufficientBalance, BalanceOverflow{
      super.transfer(srcId, dstId, amount);
   }
   
   public synchronized long getBalance(Long accountId) throws NotFound{
      return super.getBalance(accountId);
   }
   
   public synchronized long totalValue(){
      return super.totalValue();
   }
}
