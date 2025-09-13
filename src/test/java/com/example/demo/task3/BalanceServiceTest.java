package com.example.demo.task3;

import com.example.demo.task1.Account;
import com.example.demo.task1.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
class BalanceServiceTest {
    @Autowired
    private AccountRepository repo;
    @Autowired
    private BalanceService service;
    @Autowired
    private TransactionTemplate transactionTemplate;

    private Long id;

    @BeforeEach
    void setup() {
        Account account = new Account(1000);
        repo.save(account);
        id = account.getId();

    }

    @Test
    void testConcurrentUpdatesSerializableIsolation() throws InterruptedException {
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_SERIALIZABLE);

        int threadCount = 500;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);


        for (int i = 0; i < threadCount; i++) {
            executor.submit(updateBalance(id, 5, latch));
        }

        latch.await();
        executor.shutdown();

        Account updatedAccount = repo.findById(id).orElseThrow();
        System.out.println("Balance: " + updatedAccount.getBalance());
    }
    @Test
    void testConcurrent_READ_UNCOMMITTED_Isolation() throws InterruptedException {
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);

        int threadCount = 500;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);


        for (int i = 0; i < threadCount; i++) {
            executor.submit(updateBalance(id, 5, latch));
        }

        latch.await();
        executor.shutdown();

        Account updatedAccount = repo.findById(id).orElseThrow();
        System.out.println("Balance: " + updatedAccount.getBalance());
    }

    private Runnable updateBalance(Long id, int amount, CountDownLatch latch) {
        return () -> {
            boolean success = false;
            while (!success) {
                try {
                    transactionTemplate.execute(status -> {
                        service.changeUserBalance(id, amount);
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException ignored) {}
                        return null;
                    });
                    success = true;
                } catch (Exception e) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ignored) {}
                }
            }
            latch.countDown();
        };
    }

}