package deadlocks.example;

/**
 * Most basic deadlock example. Note the order of locking.
 */
public class SimpleDeadlock {
	static final Object lock1 = new Object();
	static final Object lock2 = new Object();

	static int variable;

	static void increment() {
		synchronized (lock1) {
			synchronized (lock2) {
				variable++;
			}
		}
	}

	static void decrement() {
		synchronized (lock2) {
			synchronized (lock1) {
				variable--;
			}
		}
	}

	public static void main(String[] args) {
		int N = 50000;

		new Thread(() -> {
			for (int i = N; i-- > 0;)
				increment();
			
			System.out.println("Increments done");
		}).start();

		new Thread(() -> {
			for (int i = N; i-- > 0;)
				decrement();
			
			System.out.println("Decrements done");
		}).start();
	}
}