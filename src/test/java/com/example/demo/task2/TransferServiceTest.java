package com.example.demo.task2;

import com.example.demo.task1.Account;
import com.example.demo.task1.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;



@SpringBootTest
class TransferServiceTest {
    @Autowired
    private AccountRepository repository;

    private TransferService service;

    @Autowired
    PlatformTransactionManager txManager;

    private Account sender;
    private Account getter;

    @BeforeEach
    void setup() {
        repository.deleteAll();
        sender = repository.save(new Account(1000));
        getter = repository.save(new Account(500));

        service = new TransferService(repository, txManager);
    }

    @Test
    void testTransferWithPropagationRequired() {
        int amount = 200;
        TransactionTemplate transactionTemplate = new TransactionTemplate(txManager);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        transactionTemplate.execute(status -> {
            Account before = repository.findById(sender.getId()).orElseThrow();
            System.out.println(before.getBalance());
            service.transfer(sender.getId(), getter.getId(), amount, TransactionDefinition.PROPAGATION_REQUIRED);
            Account after = repository.findById(sender.getId()).orElseThrow();
            System.out.println(after.getBalance());
            return null;
        });

        Account after2 = repository.findById(sender.getId()).orElseThrow();
        System.out.println(after2.getBalance());
    }

    @Test
    void testTransferWithPropagationRequiredNew() {
        //given
        int amount = 200;
        TransactionTemplate transactionTemplate = new TransactionTemplate(txManager);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        transactionTemplate.execute(status -> {
                Account before = repository.findById(sender.getId()).orElseThrow();
                 System.out.println(before.getBalance());
                service.transfer(sender.getId(), getter.getId(), amount, TransactionDefinition.PROPAGATION_REQUIRES_NEW);
                Account after = repository.findById(sender.getId()).orElseThrow();
                System.out.println(after.getBalance());
            return null;
        });

        Account after2 = repository.findById(sender.getId()).orElseThrow();
        System.out.println(after2.getBalance());

    }
}