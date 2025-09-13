package com.example.demo.task1;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AccountServiceTest {
    @Autowired
    AccountRepository repo;
    @Autowired
    AccountService service;

    @Test
    void rollbackDefaultRunTime()
    {
        int init_balance=1000;
        Account before = new Account(init_balance);
        repo.save(before);
        Long id = before.getId();
        try
        {
            service.defaultRuntime(before.getId());
        } catch (Exception e) {
        }

        Account after = repo.findById(id).orElseThrow();
        assertEquals(init_balance,after.getBalance());

    }

    @Test
    void rollbackForException()
    {
        int init_balance=1000;
        Account before = new Account(init_balance);
        repo.save(before);
        Long id = before.getId();
        try
        {
            service.rollbackForException(before.getId());
        } catch (Exception e) {
        }

        Account after = repo.findById(id).orElseThrow();
        assertEquals(init_balance,after.getBalance());
    }

    @Test
    void RuntimeNoRollback()
    {
        int init_balance=1000;
        Account before = new Account(init_balance);
        repo.save(before);
        Long id = before.getId();
        try
        {
            service.RuntimeNoRollback(before.getId());
        } catch (Exception e) {
        }

        Account after = repo.findById(id).orElseThrow();
        assertNotEquals(init_balance,after.getBalance());
    }

    @Test
    void ExceptionWithNoForRollBack()
    {
        int init_balance=1000;
        Account before = new Account(init_balance);
        repo.save(before);
        Long id = before.getId();
        try
        {
            service.ExceptionWithNoForRollBack(before.getId());
        } catch (Exception e) {
        }

        Account after = repo.findById(id).orElseThrow();
        assertNotEquals(init_balance,after.getBalance());
    }

}