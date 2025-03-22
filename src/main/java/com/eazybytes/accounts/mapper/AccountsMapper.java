package com.eazybytes.accounts.mapper;

import com.eazybytes.accounts.dto.AccountsDto;
import com.eazybytes.accounts.entities.Accounts;

public class AccountsMapper {

    public static AccountsDto mapToAccountsDto(Accounts accounts, AccountsDto accountsDto) {
        accountsDto.setAccountNumber(accounts.getAccountNumber());
        accountsDto.setEmail(accounts.getEmail());
        accountsDto.setName(accounts.getName());
        accountsDto.setMobileNumber(accounts.getMobileNumber());
        return accountsDto;
    }

    public static Accounts mapToAccounts(AccountsDto accountsDto, Accounts accounts) {
        accounts.setAccountNumber(accountsDto.getAccountNumber());
        accounts.setEmail(accountsDto.getEmail());
        accounts.setName(accountsDto.getName());
        accounts.setMobileNumber(accountsDto.getMobileNumber());
        return accounts;
    }

}
