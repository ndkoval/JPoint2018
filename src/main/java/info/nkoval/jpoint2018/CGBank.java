package info.nkoval.jpoint2018;

public class CGBank implements Bank {
    private final Account[] accounts;

    public CGBank(int n) {
        accounts = new Account[n];
        for (int i = 0; i < n; i++)
            accounts[i] = new Account();
    }

    @Override
    public synchronized long getAmount(int id) {
        return accounts[id].amount;
    }

    @Override
    public synchronized void deposit(int id, long amount) {
        accounts[id].amount += amount;
    }

    @Override
    public synchronized void withdraw(int id, long amount) {
        accounts[id].amount -= amount;
    }

    @Override
    public synchronized void transfer(int fromId, int toId, long amount) {
        if (fromId == toId) return;
        accounts[fromId].amount -= amount;
        accounts[toId].amount += amount;
    }

    private static class Account {
        volatile long amount;
    }
}