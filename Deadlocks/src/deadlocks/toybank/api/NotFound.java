package deadlocks.toybank.api;

@SuppressWarnings("serial")
public class NotFound extends Exception{
   public NotFound(Long accId){
      super("Account not found: " + accId);
   }
}
