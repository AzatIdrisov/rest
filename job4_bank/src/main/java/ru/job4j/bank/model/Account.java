package ru.job4j.bank.model;

import java.util.Objects;

public class Account extends Id {

    private String requisite;

    private double balance;

    private User user;

    public Account(String requisite, double balance) {
        this.requisite = requisite;
        this.balance = balance;
    }

    public String getRequisite() {
        return requisite;
    }

    public void setRequisite(String requisite) {
        this.requisite = requisite;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        Account account = (Account) o;
        return Double.compare(account.balance, balance) == 0
                && Objects.equals(requisite, account.requisite)
                && Objects.equals(user, account.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), requisite, balance, user);
    }
}
