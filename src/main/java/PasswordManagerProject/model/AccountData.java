package PasswordManagerProject.model;

import java.util.List;

public class AccountData {
    private String pin;
    private List<Account> accounts;

    public AccountData(String pin, List<Account> accounts) {
        this.pin = pin;
        this.accounts = accounts;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }
}
