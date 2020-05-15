package deadlocks.toybank.api;

/**
 * Insufficient balance for a transaction
 * 
 * @author S.Samokhodkin
 */

@SuppressWarnings({ "serial" })
public class InsufficientBalance extends Exception {
   public InsufficientBalance(Long accId, long amount, long currentBalance){
      super("Amount exceeds balance: " + amount + " at current balance " + currentBalance + " for account "+accId);
   }
}
