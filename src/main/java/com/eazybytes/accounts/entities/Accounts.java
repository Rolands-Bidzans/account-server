package com.eazybytes.accounts.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @ToString @AllArgsConstructor @NoArgsConstructor
public class Accounts extends  BaseEntity {

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
}
