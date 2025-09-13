package com.example.demo.task2;

import com.example.demo.task1.Account;
import com.example.demo.task1.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class TransferServiceTest {
    @Autowired
    private AccountRepository repository;

    @Autowired
    private TransferService service;

    private Account sender;
    private Account getter;

    @BeforeEach
    void setup() {
        repository.deleteAll();
        sender = repository.save(new Account(1000));
        getter = repository.save(new Account(500));
    }

    @Test
    public void testTransferWithPropagationRequired() {
        //given
        int amount = 200;
        //when
        try {
            wrapTransaction(sender.getId(),getter.getId(),amount,TransactionDefinition.PROPAGATION_REQUIRED);
        } catch (RuntimeException e) {
            System.out.println("Exception caught" + e.getMessage());
        }
        //then
        Account senderAfter = repository.findById(sender.getId()).orElseThrow();
        Account getterAfter = repository.findById(getter.getId()).orElseThrow();

        assertEquals(sender.getBalance(), senderAfter.getBalance());
        assertEquals(getter.getBalance(), getterAfter.getBalance());
    }

    @Test
    public void testTransferWithPropagationRequiredNew() {
        //given
        int amount = 200;
        int senderBalance = sender.getBalance();
        int getterBalance = getter.getBalance();
        //when
        try {
            wrapTransaction(sender.getId(),getter.getId(),amount,TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        } catch (RuntimeException e) {
            System.out.println("Exception caught" + e.getMessage());
        }
        //then
        Account senderAfter = repository.findById(sender.getId()).orElseThrow();
        Account getterAfter = repository.findById(getter.getId()).orElseThrow();

        assertEquals(senderBalance - amount, senderAfter.getBalance());
        assertEquals(getterBalance + amount, getterAfter.getBalance());
    }

    @Transactional
    protected void wrapTransaction(Long from, Long to, int amount, int propagation) {
        service.transfer(from,to,amount,propagation);
            throw new RuntimeException("Forcing rollback or transaction check");
   }

}