package deadlocks.toybank.impl;

import java.util.concurrent.ConcurrentHashMap;

/**
 * This class demonstrates a vanilla version of the fine-grained
 * synchronization.
 * 
 * To make the account lookups concurrent yet thread-safe, the database is
 * implemented as a ConcurrentHashMap with the same key and value types as in
 * CoarseGrainedImpl.
 * 
 * The integrity of transactions is protected by synchronizing threads on the
 * values of the involved accounts, in the order of their IDs.
 */

public class FineGrainedImpl implements MockBank, ErrorHelpers {
   // Mapping from account ID to the balance value
   private final ConcurrentHashMap<Long, long[]> db = new ConcurrentHashMap<>();

   // Create fixed set of accounts with IDs in the range 1 .. numAccs
   public FineGrainedImpl(int numAccs) {
      for (int i = 1; i <= numAccs; i++) {
         db.put(new Long(i), new long[1]);
      }
   }

   public void deposit(Long accountId, long amount) {
      if (amount < 0)
         throwNegativeAmount(amount);

      long[] value = db.get(accountId);
      if (value == null)
         throwAccNotFound(accountId);

      synchronized (value) {
         if (amount > MAX_BALANCE - value[0])
            throwBalanceOverflow(accountId, amount);

         value[0] += amount;
      }
   }

   public void withdraw(Long accountId, long amount) {
      if (amount < 0)
         throwNegativeAmount(amount);

      long[] value = db.get(accountId);
      if (value == null)
         throwAccNotFound(accountId);

      synchronized (value) {
         if (amount > value[0])
            throwBalanceUnderflow(accountId, amount);

         value[0] -= amount;
      }
   }

   public void transfer(Long srcId, Long dstId, long amount) {
      if (amount < 0)
         throw new IllegalArgumentException("negative amount: " + amount);
      if (srcId.longValue() == dstId)
         return;

      long[] srcValue = db.get(srcId);
      if (srcValue == null)
         throwAccNotFound(srcId);

      long[] dstValue = db.get(dstId);
      if (dstValue == null)
         throwAccNotFound(dstId);

      // sort locks by account id
      Object lock1 = srcId < dstId ? srcValue : dstValue;
      Object lock2 = srcId < dstId ? dstValue : srcValue;

      synchronized (lock1) {
         synchronized (lock2) {
            if (amount > srcValue[0])
               throwBalanceUnderflow(srcId, amount);
            if (amount + dstValue[0] > MAX_BALANCE)
               throwBalanceOverflow(dstId, amount);

            srcValue[0] -= amount;
            dstValue[0] += amount;
         }
      }
   }

   public synchronized long getBalance(Long accountId) {
      long[] value = db.get(accountId);
      if (value == null)
         throwAccNotFound(accountId);

      // if there is an ongoing transaction with this account, wait for its end
      synchronized (value) {
         return value[0];
      }
   }

   @Override
   public long getTotal() {
      return db.reduceValuesToLong(10000, v -> v[0], 0, (a, b) -> a + b);
   }
}
