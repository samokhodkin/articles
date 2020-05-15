package deadlocks.toybank.api;

/**
 * A balance is going to exceed the allowed limit
 * 
 * @author S.Samokhodkin
 */
@SuppressWarnings("serial")
public class BalanceOverflow extends Exception {
   
   public BalanceOverflow(Long accId, long amount, long currentBalance){
      super("Amount exceeds limit: " + amount + " at current balance " + currentBalance + " for account "+accId);
   }
   
}
