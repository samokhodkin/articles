package deadlocks.toybank.api;

/**
 * Account not found exception
 * 
 * @author S.Samokhodkin
 */

@SuppressWarnings("serial")
public class NotFound extends Exception{
   public NotFound(Long accId){
      super("Account not found: " + accId);
   }
}
