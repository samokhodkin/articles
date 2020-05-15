package deadlocks.example;

/**
 * Example of transactionally independent groups of variables.
 * 
 * @author S.Samokhodkin
 */

public class Groups {
   int A, B, C, D;
   
   final Object lock1 = new Object(); 
   final Object lock2 = new Object(); 
   
//   synchronized void transaction1(){
//      A += B<0? 1: -1;
//      B += A>0? 1: -1;
//   }
//   
//   synchronized void transaction2(){
//      C += D<0? 1: -1;
//      D += C>0? 1: -1;
//   }
   
   void transaction1(){
      synchronized (lock1) {
         A += B<0? 1: -1;
         B += A>0? 1: -1;
      }
   }
   
   void transaction2(){
      synchronized (lock2) {
         C += D<0? 1: -1;
         D += C>0? 1: -1;
      }
   }
}
