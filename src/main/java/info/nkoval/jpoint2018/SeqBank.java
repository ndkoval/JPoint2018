package info.nkoval.jpoint2018;

public class SeqBank implements Bank {
    private final Account[] accounts;

    public SeqBank(int n) {
        accounts = new Account[n];
        for (int i = 0; i < n; i++)
            accounts[i] = new Account();
    }

    @Override
    public long getAmount(int id) {
        return accounts[id].amount;
    }

    @Override
    public void deposit(int id, long amount) {
        accounts[id].amount += amount;
    }

    @Override
    public void withdraw(int id, long amount) {
        accounts[id].amount -= amount;
    }

    @Override
    public void transfer(int fromId, int toId, long amount) {
        accounts[fromId].amount -= amount;
        accounts[toId].amount += amount;
    }

    private class Account {
        long amount;
    }
}
