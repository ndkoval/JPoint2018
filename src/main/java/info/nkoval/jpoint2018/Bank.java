package info.nkoval.jpoint2018;

public interface Bank {
    // Returns current amount in the specified account.
    long getAmount(int id);

    // Deposits specified amount to account.
    void deposit(int id, long amount);

    // Withdraws specified amount from account.
    void withdraw(int id, long amount);

    // Transfers specified amount from one account to another account.
    void transfer(int fromId, int toId, long amount);
}
