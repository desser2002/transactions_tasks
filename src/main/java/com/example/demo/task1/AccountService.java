package com.example.demo.task1;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {
    private final AccountRepository repository;

    public AccountService(AccountRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void defaultRuntime(Long id) {
        Account account = repository.findById(id).orElseThrow();
        account.setBalance(account.getBalance() + 100);
        repository.save(account);
        System.out.println("Account balance up 100");
        throw new RuntimeException("Runtime exception rollback");
    }

    @Transactional(rollbackFor = Exception.class)
    public void rollbackForException(Long id) throws Exception {
        Account account = repository.findById(id).orElseThrow();
        account.setBalance(account.getBalance() + 100);
        repository.save(account);
        System.out.println("Account balance up 100");
        throw new Exception("Default exception for rollback");
    }

    @Transactional()
    public void ExceptionWithNoForRollBack(Long id) throws Exception {
        Account account = repository.findById(id).orElseThrow();
        account.setBalance(account.getBalance() + 100);
        repository.save(account);
        System.out.println("Account balance up 100");
        throw new Exception("Default exception with no rollback");
    }

    @Transactional(noRollbackFor = RuntimeException.class)
    public void RuntimeNoRollback(Long id) {
        Account account = repository.findById(id).orElseThrow();
        account.setBalance(account.getBalance() + 100);
        repository.save(account);
        System.out.println("Account balance up 100");
        throw new RuntimeException("Runtime exception with no rollback");
    }
}
