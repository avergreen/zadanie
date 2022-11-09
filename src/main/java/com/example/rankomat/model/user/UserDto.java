package com.example.rankomat.model.user;

import lombok.*;

import java.math.*;

@Getter
@Setter
public class UserDto {

    private String firstName;

    private String telephoneNumber;

    private BigInteger moneyAmount;

//    @Override
//    public String toString() {
//        return "UserDto{" +
//                "firstName='" + firstName + '\'' +
//                ", telephoneNumber='" + telephoneNumber + '\'' +
//                '}';
//    }
}
