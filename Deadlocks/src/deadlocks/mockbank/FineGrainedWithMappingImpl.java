package deadlocks.mockbank;

/**
 * This class demonstrates a memory-efficient version of the fine-grained
 * design.
 * 
 * To save space, the account values are arranged as a primitive long array,
 * where the value index is based on the account ID. 
 * 
 * There is also a separate array of locks objects. The size of the locks array
 * is a power of two, so mapping an account ID to a lock index may be done with
 * a simple bitwise OR. This size is also chosen to be large enough to make the
 * collisions over a lock as infrequent as possible, while keeping the memory
 * taken by the lock objects small enough.
 * 
 * The transactions are synchronized on the locks selected by the above mapping.
 * The locks are ordered by their actual indices
 */
public class FineGrainedWithMappingImpl implements MockBank, ErrorHelpers {
   private final long[] db;
   
   private final Object[] locks;
   private final int lockIndexMask;

   public FineGrainedWithMappingImpl(int numAccs, int numThreads) {
      db = new long[numAccs];
      
      locks = new Object[nextPow2(numThreads * 20)];
      for (int i = 0; i < locks.length; i++)
         locks[i] = new Object();
      
      lockIndexMask = locks.length - 1;
   }

   @Override
   public void deposit(Long accountId, long amount) {
      int id = accountId.intValue() - 1;
      if (id < 0 || id >= db.length)
         throwAccNotFound(accountId);

      synchronized (locks[id & lockIndexMask]) {
         if (db[id] + amount > MAX_BALANCE)
            throwBalanceOverflow(accountId, amount);

         db[id] += amount;
      }
   }

   @Override
   public void withdraw(Long accountId, long amount) {
      int id = accountId.intValue() - 1;
      if (id < 0 || id >= db.length)
         throwAccNotFound(accountId);

      synchronized (locks[id & lockIndexMask]) {
         if (db[id] < amount)
            throwBalanceUnderflow(accountId, amount);

         db[id] -= amount;
      }
   }

   @Override
   public void transfer(Long srcId, Long dstId, long amount) {
      int sid = srcId.intValue() - 1;
      int did = dstId.intValue() - 1;
      if (sid < 0 || sid >= db.length)
         throwAccNotFound(srcId);
      if (did < 0 || did >= db.length)
         throwAccNotFound(dstId);

      // map IDs to lock indices
      int i1 = sid & lockIndexMask;
      int i2 = did & lockIndexMask;

      // sort locks by mapped index
      Object lock1 = i1 < i2 ? locks[i1] : locks[i2];
      Object lock2 = i1 < i2 ? locks[i2] : locks[i1];

      synchronized (lock1) {
         synchronized (lock2) {
            if (db[sid] < amount)
               throwBalanceUnderflow(srcId, amount);
            if (db[did] + amount > MAX_BALANCE)
               throwBalanceOverflow(dstId, amount);

            db[sid] -= amount;
            db[did] += amount;
         }
      }
   }

   @Override
   public long getBalance(Long accountId) {
      int id = accountId.intValue() - 1;
      if (id < 0 || id >= db.length)
         throwAccNotFound(accountId);

      return db[id];
   }

   @Override
   public long getTotal() {
      long s = 0;
      for (int i = db.length; i-- > 0;)
         s += db[i];
      return s;
   }

   private static int nextPow2(int v) {
      v--;
      v |= v >>> 1;
      v |= v >>> 2;
      v |= v >>> 4;
      v |= v >>> 8;
      v |= v >>> 16;
      return v + 1;
   }
}
