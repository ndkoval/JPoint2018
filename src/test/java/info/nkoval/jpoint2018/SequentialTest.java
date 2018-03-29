package info.nkoval.jpoint2018;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class SequentialTest {
    private final BankCreators bankCreator;

    public SequentialTest(BankCreators bankCreator) {
        this.bankCreator = bankCreator;
    }

    @Parameterized.Parameters(name = "{0}")
    public static List<Object[]> params() {
        return Arrays.stream(BankCreators.values())
            .map(bc -> new Object[] {bc})
            .collect(Collectors.toList());
    }

    @Test
    public void testTransfer() {
        Bank testBank = bankCreator.create(2);
        assertEquals(0, testBank.getAmount(0));
        assertEquals(0, testBank.getAmount(1));
        testBank.transfer(0, 1, 100);
        assertEquals(-100, testBank.getAmount(0));
        assertEquals(100, testBank.getAmount(1));
    }

    @Test
    public void testStress() {
        int N = 20;
        Bank expectedBank = new SeqBank(N);
        Bank testBank = bankCreator.create(N);
        Random rand = new Random(0);
        for (int t = 0; t < 1_000_000; t++) {
            switch (rand.nextInt(4)) {
            case 0:
                int id = rand.nextInt(N);
                assertEquals(expectedBank.getAmount(id), testBank.getAmount(id));
                break;
            case 1:
                id = rand.nextInt(N);
                long amount = rand.nextInt(100);
                expectedBank.deposit(id, amount);
                testBank.deposit(id, amount);
                break;
            case 2:
                id = rand.nextInt(N);
                amount = rand.nextInt(100);
                expectedBank.withdraw(id, amount);
                testBank.withdraw(id, amount);
                break;
            case 3:
                int idFrom = rand.nextInt(N);
                int idTo = rand.nextInt(N);
                amount = rand.nextInt(100);
                expectedBank.transfer(idFrom, idTo, amount);
                testBank.transfer(idFrom, idTo, amount);
                break;
            }
        }
    }
}
