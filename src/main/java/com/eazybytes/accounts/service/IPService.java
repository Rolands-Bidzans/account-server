package com.eazybytes.accounts.service;

import com.eazybytes.accounts.dto.AccountsDto;

public interface IPService {

    void createAccount(AccountsDto accountDto);
    AccountsDto fetchAccount(String email);
    boolean updateAccount(AccountsDto accountDto);
    boolean deleteAccount(String email);


}
