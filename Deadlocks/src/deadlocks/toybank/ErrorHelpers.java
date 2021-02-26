package deadlocks.toybank.impl;

public interface ErrorHelpers {
   default void throwNegativeAmount(long amount){
      throw new IllegalArgumentException("Negative amount: " + amount);
   } 
   
   default void throwAccNotFound(Long accId){
      throw new IllegalArgumentException("Account no found: " + accId);
   } 
   
   default void throwBalanceOverflow(Long accId, long amount){
      throw new IllegalStateException("Deposit would overflow the balance: " + amount);
   } 
   
   default void throwBalanceUnderflow(Long accId, long amount){
      throw new IllegalStateException("Insufficient balance for withdrowal: " + amount);
   } 
}
