package com.example.demo.task2;

import com.example.demo.task1.Account;
import com.example.demo.task1.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class TransferService {
    private final AccountRepository repository;
    private final TransactionTemplate transactionTemplate;

    public TransferService(AccountRepository repository, PlatformTransactionManager txManager) {
        this.repository = repository;
        this.transactionTemplate = new TransactionTemplate(txManager);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void transfer(Long from, Long to, int amount, int propagation) {
        {
            transactionTemplate.setPropagationBehavior(propagation);
            transactionTemplate.execute(status -> {
                Account sender = repository.findById(from).orElseThrow();
                Account getter = repository.findById(to).orElseThrow();
                sender.setBalance(sender.getBalance() - amount);
                getter.setBalance(getter.getBalance() + amount);
                repository.save(sender);
                repository.save(getter);
                return null;
            });
        }
    }
}
