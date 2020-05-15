package deadlocks.example;

/**
 * Example of transactionally independent groups of variables.
 * 
 * @author S.Samokhodkin
 */

public class Groups {
   int A, B, C, D;
   
   synchronized void transaction1(){
      A += B<0? 1: -1;
      B += A>0? 1: -1;
   }
   
   synchronized void transaction2(){
      C += D<0? 1: -1;
      D += C>0? 1: -1;
   }
}
