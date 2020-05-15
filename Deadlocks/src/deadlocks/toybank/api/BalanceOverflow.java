package deadlocks.toybank.api;

@SuppressWarnings("serial")
public class BalanceOverflow extends Exception {
   public BalanceOverflow(Long accId, long amount, long currentBalance){
      super("Amount exceeds limit: " + amount + " at current balance " + currentBalance + " for account "+accId);
   }
}
