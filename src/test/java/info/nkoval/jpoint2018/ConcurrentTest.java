package info.nkoval.jpoint2018;

import com.devexperts.dxlab.lincheck.LinChecker;
import com.devexperts.dxlab.lincheck.annotations.Operation;
import com.devexperts.dxlab.lincheck.annotations.Param;
import com.devexperts.dxlab.lincheck.annotations.Reset;
import com.devexperts.dxlab.lincheck.paramgen.IntGen;
import com.devexperts.dxlab.lincheck.paramgen.LongGen;
import com.devexperts.dxlab.lincheck.stress.StressCTest;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(Parameterized.class)
public class ConcurrentTest {
    private static BankCreators bankCreator;

    public ConcurrentTest(BankCreators bc) {
        bankCreator = bc;
    }

    @Parameterized.Parameters(name = "{0}")
    public static List<Object[]> params() {
        return Arrays.stream(BankCreators.values())
            .map(bc -> new Object[] {bc})
            .collect(Collectors.toList());
    }

    @Test
    public void test() {
        LinChecker.check(ConcurrentTestImpl.class);
    }

    @Param(name = "id", gen = IntGen.class, conf = "0:4")
    @Param(name = "amount", gen = LongGen.class)
    @StressCTest(actorsPerThread = {"5:10", "5:10"})
    public static class ConcurrentTestImpl {
        private Bank bank;

        @Reset
        public void reset() {
            bank = bankCreator.create(5);
        }

        @Operation(params = {"id"})
        public long getAmount(int id) {
            return bank.getAmount(id);
        }

        @Operation(params = {"id", "amount"})
        public void deposit(int id, long amount) {
            bank.deposit(id, amount);
        }

        @Operation(params = {"id", "amount"})
        public void withdraw(int id, long amount) {
            bank.withdraw(id, amount);
        }

        @Operation(params = {"id", "id", "amount"})
        public void transfer(int idFrom, int idTo, long amount) {
            bank.transfer(idFrom, idTo, amount);
        }
    }
}