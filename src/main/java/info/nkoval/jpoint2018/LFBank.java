package info.nkoval.jpoint2018;

import java.util.concurrent.atomic.AtomicReferenceArray;

public class LFBank implements Bank {
    private final AtomicReferenceArray<Account> accounts;

    public LFBank(int n) {
        accounts = new AtomicReferenceArray<>(n);
        for (int i = 0; i < n; i++) {
            accounts.set(i, new Account(0));
        }
    }

    @Override
    public long getAmount(int id) {
        while (true) {
            Account account = accounts.get(id);
            if (!account.invokeOperation())
                return account.amount;
        }
    }

    @Override
    public void deposit(int id, long amount) {
        while (true) {
            Account account = accounts.get(id);
            if (!account.invokeOperation()) {
                Account updated = new Account(account.amount + amount);
                if (accounts.compareAndSet(id, account, updated))
                    return;
            }
        }
    }

    @Override
    public void withdraw(int id, long amount) {
        deposit(id, -amount);
    }

    @Override
    public void transfer(int fromId, int toId, long amount) {
        if (fromId == toId) return;
        TransferOp op = new TransferOp(fromId, toId, amount);
        op.invokeOperation();
    }

    private AcquiredAccount acquire(int index, Op op) {
        while (true) {
            Account account = accounts.get(index);
            if (op.completed) {
                return null;
            }
            if (account instanceof AcquiredAccount) {
                AcquiredAccount acquiredAccount = (AcquiredAccount) account;
                if (acquiredAccount.op == op) {
                    return acquiredAccount;
                } else {
                    acquiredAccount.invokeOperation();
                }
            } else {
                AcquiredAccount acquiredAccount = new AcquiredAccount(account.amount, op);
                if (accounts.compareAndSet(index, account, acquiredAccount)) {
                    return acquiredAccount;
                }
            }
        }
    }

    private void release(int index, Op op) {
        assert op.completed;
        Account account = accounts.get(index);
        if (account instanceof AcquiredAccount) {
            AcquiredAccount acquiredAccount = (AcquiredAccount) account;
            if (acquiredAccount.op == op) {
                Account updated = new Account(acquiredAccount.newAmount);
                accounts.compareAndSet(index, account, updated);
            }
        }
    }

    private static class Account {
        long amount;

        Account(long amount) {
            this.amount = amount;
        }

        boolean invokeOperation() {
            return false;
        }
    }

    // Account that was acquired as a part of in-progress operation that spans multiple accounts.
    private static class AcquiredAccount extends Account {
        final Op op;
        // New amount of funds in this account when op completes.
        long newAmount;

        AcquiredAccount(long amount, Op op) {
            super(amount);
            this.op = op;
            this.newAmount = amount;
        }

        @Override
        boolean invokeOperation() {
            op.invokeOperation();
            return true;
        }
    }

    private abstract class Op {
        volatile boolean completed;

        abstract void invokeOperation();
    }

    // Descriptor for {@link #transfer(int, int, long) transfer(...)} operation.
    private class TransferOp extends Op {
        final int fromId;
        final int toId;
        final long amount;

        TransferOp(int fromId, int toId, long amount) {
            this.fromId = fromId;
            this.toId = toId;
            this.amount = amount;
        }

        @Override
        void invokeOperation() {
            AcquiredAccount from, to;
            if (fromId < toId) {
                from = acquire(fromId, this);
                to = acquire(toId, this);
            } else {
                to = acquire(toId, this);
                from = acquire(fromId, this);
            }
            if (from != null && to != null) {
                from.newAmount = from.amount - amount;
                to.newAmount = to.amount + amount;
                completed = true;
            }
            release(fromId, this);
            release(toId, this);
        }
    }
}