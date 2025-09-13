package com.example.demo.task3;

import com.example.demo.task1.Account;
import com.example.demo.task1.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BalanceService {
    private final AccountRepository repo;

    public BalanceService(AccountRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public void changeUserBalance(Long id, int amount) {
        Account account = repo.findById(id).orElseThrow();
        account.setBalance(account.getBalance() + amount);
        repo.save(account);
    }
}
