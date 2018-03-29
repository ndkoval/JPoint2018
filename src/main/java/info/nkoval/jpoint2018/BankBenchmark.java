package info.nkoval.jpoint2018;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Warmup(iterations = 5, time = 100, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 10, time = 100, timeUnit = TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(1)
@State(Scope.Benchmark)
public class BankBenchmark {
    //@Param({"100", "1000", "10000"})
    @Param({"10000"})
    private int n;
    @Param
    private BankCreators bankCreator;

    private Bank bank;

    @Setup
    public void setup() {
        bank = bankCreator.create(n);
    }

    @Benchmark
    public void benchmark() {
        Random r = ThreadLocalRandom.current();
        switch (r.nextInt(4)) {
        case 0:
        case 1:
            bank.getAmount(r.nextInt(n));
            break;
        case 2:
            bank.deposit(r.nextInt(n), r.nextInt());
            break;
        case 3:
            bank.transfer(r.nextInt(n), r.nextInt(n), r.nextInt());
            break;
        }
    }

    public static void main(String[] args) throws Exception {
        int[] threads = {1, 2, 4, 10, 20, 40, 60, 80, 120};
        //boolean[] useRtmLockingOptions = {false, true};
        boolean[] useRtmLockingOptions = {true};
        for (int t: threads) {
            for (boolean useRtmLocking: useRtmLockingOptions) {
                String useRtmLockingSign = useRtmLocking ? "+" : "-";
                Options opt = new OptionsBuilder()
                    .include(BankBenchmark.class.getCanonicalName())
                    .threads(t)
                    .jvmArgs("-XX:" + useRtmLockingSign + "UseRTMLocking")
                    .resultFormat(ResultFormatType.CSV)
                    .result("jmh_result_banks_t" + t + (useRtmLocking ? "rtmlocking" : "") + ".txt")
                    .build();
                new Runner(opt).run();
            }
        }
    }
}
