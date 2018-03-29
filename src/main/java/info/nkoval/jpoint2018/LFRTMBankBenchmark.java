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

@Warmup(iterations = 5)
@Measurement(iterations = 10)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(1)
@State(Scope.Benchmark)
public class LFRTMBankBenchmark {
    @Param({"100", "1000", "10000"})
//    @Param("10000") 
    private int n;
    @Param({"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"})
//    @Param({"1", "3", "5", "10"})
    private int attempts;

    private Bank bank;

    @Setup
    public void setup() {
        bank = new LFRTMBank(n, attempts);
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
        for (int t: threads) {
            Options opt = new OptionsBuilder()
                .include(LFRTMBankBenchmark.class.getSimpleName())
                .threads(t)
                .resultFormat(ResultFormatType.CSV)
                .result("jmh_result_lfrtm_t" + t + ".txt")
                .build();
            new Runner(opt).run();
        }
    }
}
