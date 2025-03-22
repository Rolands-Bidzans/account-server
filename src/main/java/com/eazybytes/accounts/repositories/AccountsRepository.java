package com.eazybytes.accounts.repositories;

import com.eazybytes.accounts.entities.Accounts;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountsRepository extends JpaRepository<Accounts, Long> {

    Optional<Accounts> findByEmail(String email);

    @Transactional
    @Modifying
    void deleteByAccountNumber(String accountNumber);

}
