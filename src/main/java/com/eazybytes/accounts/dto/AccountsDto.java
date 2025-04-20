package com.eazybytes.accounts.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AccountsDto {

    private String accountNumber;

    @NotEmpty(message = "Name can not be a null or empty")
    @Pattern(regexp="^[a-zA-Z ]{5,}$", message="Name must be longer than 4 letters and contain only letters and spaces")
    private String name;

    @NotEmpty(message = "Email can not be a null or empty")
    @Email(message = "Invalid email format")
    private String email;

    @NotEmpty(message = "MobileNumber can not be a null or empty")
    @Pattern(regexp="(^$|[0-9]{8})",message = "Mobile number must be 8 digits")
    private String mobileNumber;


}
