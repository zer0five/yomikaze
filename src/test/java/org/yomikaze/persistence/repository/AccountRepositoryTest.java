package org.yomikaze.persistence.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.yomikaze.persistence.entity.Account;
import org.yomikaze.persistence.entity.Profile;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Optional;



@SpringBootTest
public class AccountRepositoryTest {

    @Autowired
    AccountRepository accountRepository;

    static boolean isInitialized = false;

    @BeforeEach
    public void setUp() {
        if (isInitialized) return;
        Account account = new Account();
        account.setUsername("hunterx");
        account.setEmail("hunterx@gmail.com");
        account.setPassword("123456");
        accountRepository.save(account);
        isInitialized = true;
    }

    @Test
    void testfindByUsername(){
        Optional<Account> accounts = accountRepository.findByUsername("hunterx");
        assertEquals("hunterx",accounts.get().getUsername());
    }

    @Test
    void testExistsByUsername(){
        assertTrue(accountRepository.existsByUsername("hunterx"));
        assertFalse(accountRepository.existsByUsername("hunterx1"));
    }

    @Test
    void testFindByEmail(){
        Optional<Account> accountOptional = accountRepository.findByEmail("hunterx@gmail.com");
        assertTrue(accountOptional.isPresent());
        Account account = accountOptional.get();
        assertEquals("hunterx@gmail.com", account.getEmail());
    }

    @Test
    void testExistsByEmail(){
        assertTrue(accountRepository.existsByEmail("hunterx@gmail.com"));
        assertFalse(accountRepository.existsByUsername("hunterx1@gmail.com"));
    }

    @Test
    void testProfileCreated(){
        Optional<Account> accountOptional = accountRepository.findByUsername("hunterx");
        assertTrue(accountOptional.isPresent());
        Account account = accountOptional.get();

        Profile profile = account.getProfile();
        assertEquals(account.getId(), profile.getId());
        assertNotNull(profile.getDisplayName());
        assertFalse(profile.getDisplayName().isEmpty());
    }
}
