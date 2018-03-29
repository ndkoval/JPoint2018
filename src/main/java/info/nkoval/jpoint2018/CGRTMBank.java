package info.nkoval.jpoint2018;

import java.util.concurrent.locks.ReentrantLock;

import static java.util.concurrent.RTMSupport.*;

public class CGRTMBank implements Bank {
    private static final int MAX_RTM_ATTEMPTS = 10;
    private final Account[] accounts;
    private final ReentrantLock gLock = new ReentrantLock();

    public CGRTMBank(int n) {
        accounts = new Account[n];
        for (int i = 0; i < n; i++)
            accounts[i] = new Account();
    }

    @Override
    public long getAmount(int id) {
        int attempt = 0;
        while (attempt < MAX_RTM_ATTEMPTS) {
            if (xbegin() == XBEGIN_STARTED) {
                if (gLock.isLocked()) xabort();
                long res = accounts[id].amount;
                xend();
                return res;
            } else attempt++;
        }
        gLock.lock();
        try {
            return accounts[id].amount;
        } finally {
            gLock.unlock();
        }
    }

    @Override
    public void deposit(int id, long amount) {
        int attempt = 0;
        while (attempt < MAX_RTM_ATTEMPTS) {
            if (xbegin() == XBEGIN_STARTED) {
                if (gLock.isLocked()) xabort();
                accounts[id].amount += amount;
                xend();
                return;
            } else attempt++;
        }
        gLock.lock();
        try {
            accounts[id].amount += amount;
        } finally {
            gLock.unlock();
        }
    }

    @Override
    public void withdraw(int id, long amount) {
        int attempt = 0;
        while (attempt < MAX_RTM_ATTEMPTS) {
            if (xbegin() == XBEGIN_STARTED) {
                if (gLock.isLocked()) xabort();
                accounts[id].amount -= amount;
                xend();
                return;
            } else attempt++;
        }
        gLock.lock();
        try {
            accounts[id].amount -= amount;
        } finally {
            gLock.unlock();
        }
    }

    @Override
    public void transfer(int fromId, int toId, long amount) {
        if (fromId == toId)
            return;
        int attempt = 0;
        while (attempt < MAX_RTM_ATTEMPTS) {
            if (xbegin() == XBEGIN_STARTED) {
                if (gLock.isLocked()) xabort();
                accounts[fromId].amount -= amount;
                accounts[toId].amount += amount;
                xend();
                return;
            } else attempt++;
        }
        gLock.lock();
        try {
            accounts[fromId].amount -= amount;
            accounts[toId].amount += amount;
        } finally {
            gLock.unlock();
        }
    }

    private static class Account {
        volatile long amount;
    }
}
