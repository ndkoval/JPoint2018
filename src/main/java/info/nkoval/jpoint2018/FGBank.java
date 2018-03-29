package info.nkoval.jpoint2018;

public class FGBank implements Bank {
    private final Account[] accounts;

    public FGBank(int n) {
        accounts = new Account[n];
        for (int i = 0; i < n; i++)
            accounts[i] = new Account();
    }

    @Override
    public long getAmount(int id) {
        Account account = accounts[id];
        synchronized (account) {
            return accounts[id].amount;
        }
    }

    @Override
    public void deposit(int id, long amount) {
        Account account = accounts[id];
        synchronized (account) {
            accounts[id].amount += amount;
        }
    }

    @Override
    public void withdraw(int id, long amount) {
        Account account = accounts[id];
        synchronized (account) {
            accounts[id].amount -= amount;
        }
    }

    @Override
    public void transfer(int fromId, int toId, long amount) {
        if (fromId == toId) return;
        Account from = accounts[fromId];
        Account to = accounts[toId];
        if (fromId < toId) {
            synchronized (from) {
                synchronized (to) {
                    accounts[fromId].amount -= amount;
                    accounts[toId].amount += amount;
                }
            }
        } else {
            synchronized (to) {
                synchronized (from) {
                    accounts[fromId].amount -= amount;
                    accounts[toId].amount += amount;
                }
            }
        }
    }

    private static class Account {
        volatile long amount;
    }
}
