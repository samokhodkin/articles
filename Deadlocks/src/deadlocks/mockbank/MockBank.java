package deadlocks.mockbank;

/**
 * Main interface
 */

public interface MockBank {
   /**
    * the limit for every account's balance
    */
   public long MAX_BALANCE = 1L<<20;
   
   /**
    * Deposit money. The balance should exceed MAX_BALANCE.
    *  
    * @exception IllegalArgumentException if the amount is negative or the account doesn't exist
    * @exception IllegalStateException if the updated balance would overflow the MAX_BALANCE
    */
   public void deposit(Long accountId, long amount);
   
   /**
    * Withdraw money. The balance may not go negative.
    * 
    * @exception IllegalArgumentException if the amount is negative or the account doesn't exist
    * @exception IllegalStateException if the amount exceeds the balance
    */
   public void withdraw(Long accountId, long amount);
   
   /**
    * Transfer money between accounts.
    * The amount must match the source's balance and the destination's limit.
    *  
    * @exception IllegalArgumentException if the amount is negative or the account doesn't exist
    * @exception IllegalStateException if the updated destination's balance would overflow the MAX_BALANCE
    *                                  or if the amount exceeds the source's the balance
    */
   public void transfer(Long srcId, Long dstId, long amount);
   
   /**
    * @return account's total 
    * @exception NotFound if the account doesn't exist
    */
   public long getBalance(Long accountId);
   
   /**
    * @return bank's total 
    */
   public long getTotal();
   }
