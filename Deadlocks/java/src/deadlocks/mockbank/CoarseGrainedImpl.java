package deadlocks.mockbank;

import java.util.*;

/**
 * This class demonstrates coarse-grained synchronization. The accounts database
 * is implemented as a standard HashMap which maps the account IDs of type Long
 * to values represented by long[1].
 * 
 * Thread safety is ensured by synchronizing all transactions on the instance
 * using synchronized methods.
 */
public class CoarseGrainedImpl implements MockBank, ErrorHelpers {
   // Mapping from an account ID to the balance value
   private final Map<Long, long[]> db = new HashMap<>();

   // Create fixed set of accounts with IDs in the range 1 .. numAccs
   public CoarseGrainedImpl(int numAccs) {
      for (int i = 1; i <= numAccs; i++) {
         db.put(new Long(i), new long[1]);
      }
   }

   public synchronized void deposit(Long accountId, long amount) {
      if (amount < 0)
         throwNegativeAmount(amount);

      long[] value = db.get(accountId);
      if (value == null)
         throwAccNotFound(accountId);
      if (value[0] + amount > MAX_BALANCE)
         throwBalanceOverflow(accountId, amount);

      value[0] += amount;
   }

   public synchronized void withdraw(Long accountId, long amount) {
      if (amount < 0)
         throwNegativeAmount(amount);

      long[] value = db.get(accountId);
      if (value == null)
         throwAccNotFound(accountId);
      if (amount > value[0])
         throwBalanceUnderflow(accountId, amount);

      value[0] -= amount;
   }

   public synchronized void transfer(Long srcId, Long dstId, long amount) {
      if (amount < 0)
         throwNegativeAmount(amount);

      long[] srcValue = db.get(srcId);
      if (srcValue == null)
         throwAccNotFound(srcId);
      if (amount > srcValue[0])
         throwBalanceUnderflow(srcId, amount);

      long[] dstValue = db.get(dstId);
      if (dstValue == null)
         throwAccNotFound(dstId);
      if (dstValue[0] + amount > MAX_BALANCE)
         throwBalanceOverflow(dstId, amount);

      srcValue[0] -= amount;
      dstValue[0] += amount;
   }

   public synchronized long getBalance(Long accountId) {
      long[] value = db.get(accountId);
      if (value == null)
         throwAccNotFound(accountId);

      return value[0];
   }

   @Override
   public synchronized long getTotal() {
      return db.values().stream().reduce(0L, (sum, v) -> sum + v[0], (sum1, sum2) -> sum1 + sum2);
   }
}
