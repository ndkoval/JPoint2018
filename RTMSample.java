import static java.util.concurrent.RTMSupport.*;

public class RTMSample {
    private static final int MAX_WARMUP_ITERATIONS = 10_000_000;
    private static final int THREADS = 20;
    private static final int OPERATIONS = 1_000_000;

    private static volatile long counter = 0;

    public static void main(String[] args) throws Exception {
        runConcurrentExecution();
        int expected = THREADS * OPERATIONS;
        if (counter != expected) throw new IllegalStateException("NON-ATOMIC TRANSACTION, EXPECTED=" + expected + ", CURRENT=" + counter);
        System.out.println("Executed successfully");
    }

    static void incAtomic() {
        while (xbegin() != -1) {}
        counter++;
        xend();
    }

    static void runConcurrentExecution() throws Exception {
        Thread[] threads = new Thread[THREADS];
        for (int t = 0; t < THREADS; t++) {
            threads[t] = new Thread(() -> {
                for (int i = 0; i < OPERATIONS; i++) {
                    incAtomic();
                }
            });
        }
        for (int t = 0; t < THREADS; t++) threads[t].start();
        for (int t = 0; t < THREADS; t++) threads[t].join();
    }
}