package deadlocks.toybank.api;

@SuppressWarnings({ "serial" })
public class InsufficientBalance extends Exception {
   public InsufficientBalance(Long accId, long amount, long currentBalance){
      super("Amount exceeds balance: " + amount + " at current balance " + currentBalance + " for account "+accId);
   }
}
