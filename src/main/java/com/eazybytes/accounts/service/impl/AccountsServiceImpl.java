package com.eazybytes.accounts.service.impl;

import com.eazybytes.accounts.dto.AccountsDto;
import com.eazybytes.accounts.entities.Accounts;
import com.eazybytes.accounts.exception.AccountAlreadyExistsException;
import com.eazybytes.accounts.exception.ResourceNotFoundException;
import com.eazybytes.accounts.mapper.AccountsMapper;
import com.eazybytes.accounts.repositories.AccountsRepository;
import com.eazybytes.accounts.service.IAccountsService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AccountsServiceImpl  implements IAccountsService {

    private AccountsRepository accountsRepository;

    @Override
    public void createAccount(@Valid AccountsDto accountDto) {
        Accounts account = AccountsMapper.mapToAccounts(accountDto, new Accounts());
        Optional<Accounts > optionalAccount = accountsRepository.findByEmail(accountDto.getEmail());
        if(optionalAccount.isPresent()) {
            throw new AccountAlreadyExistsException("Account already registered with given Email "
                    + accountDto.getEmail());
        }
        accountsRepository.save(account);
    }

    @Override
    public AccountsDto fetchAccount(String email) {
        Accounts accounts = accountsRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("Account", "Email", email)
        );
        AccountsDto accountDto = AccountsMapper.mapToAccountsDto(accounts, new AccountsDto());
        return accountDto;
    }

    @Override
    public boolean updateAccount(AccountsDto accountsDto) {
        boolean isUpdated = false;
        if(accountsDto !=null ){
            Accounts accounts = accountsRepository.findByEmail(accountsDto.getEmail()).orElseThrow(
                    () -> new ResourceNotFoundException("Account", "Email", accountsDto.getEmail().toString())
            );

            String accountNumber = accounts.getAccountNumber();
            AccountsMapper.mapToAccounts(accountsDto, accounts);

            accounts.setAccountNumber(accountNumber);
            accounts = accountsRepository.save(accounts);

            isUpdated = true;
        }
        return  isUpdated;
    }


    @Override
    public boolean deleteAccount(String email) {
        Accounts account = accountsRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("Account", "email", email)
        );

        accountsRepository.deleteByAccountNumber(account.getAccountNumber());
        return true;
    }
}
