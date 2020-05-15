package deadlocks.toybank;

import deadlocks.toybank.exception.*;

public interface ToyBank {
   /**
    * the limit for every account's balance
    */
   public long MAX_BALANCE = 1L<<20;
   
   /**
    * Create account
    * @return account ID
    * @exception IllegalArgumentException if initialDeposit is negative
    * @exception BalanceOverflow if initialDeposit exceeds MAX_BALANCE
    */
   public Long createAccount(long initialDeposit) throws BalanceOverflow;
   
   /**
    * Delete account
    * @return account's balance
    * @exception NotFound if the account doesn't exist
    */
   public long deleteAccount(Long accountId) throws NotFound;
   
   /**
    * Deposit money.
    * The amount should match the account's limit. 
    * @exception IllegalArgumentException if the amount is negative
    * @exception NotFound if the account doesn't exist
    * @exception BalanceOverflow if the amount exceeds the limit
    */
   public void deposit(Long accountId, long amount) throws NotFound, BalanceOverflow;
   
   /**
    * Withraw money.
    * The amount should match the account's balance.
    * @exception IllegalArgumentException if the amount is negative
    * @exception NotFound if the account doesn't exist
    * @exception InsufficientBalance if the amount exceeds the balance
    */
   public void withdraw(Long accountId, long amount) throws NotFound, InsufficientBalance;
   
   /**
    * Transfer money between accounts.
    * The amount must match the source's balance and the destination's limit. 
    * @exception IllegalArgumentException if the amount is negative
    * @exception NotFound if the source or destination account doesn't exist
    * @exception InsufficientBalance if the amount exceeds the source's balance
    * @exception BalanceOverflow if the amount exceeds the destination's limit
    */
   public void transfer(Long srcId, Long dstId, long amount) throws NotFound, InsufficientBalance, BalanceOverflow;
   
   /**
    * @return account's total 
    * @exception NotFound if the account doesn't exist
    */
   public long getBalance(Long accountId) throws NotFound;
   
   /**
    * @return sum of all accounts 
    */
   public long totalValue();   
}
