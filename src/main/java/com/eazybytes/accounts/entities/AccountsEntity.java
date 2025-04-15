package com.eazybytes.accounts.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @ToString @AllArgsConstructor @NoArgsConstructor
public class AccountsEntity extends  BaseEntity {

    @Id
    @Column(name="account_number")
//    @GeneratedValue(strategy = GenerationType.UUID)
    private String accountNumber;

    @Column(name="name")
    private String name;

    @Column(name="email")
    private String email;

    @Column(name="mobile_number")
    private String mobileNumber;

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
}
