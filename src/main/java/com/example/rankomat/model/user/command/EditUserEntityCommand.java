package com.example.rankomat.model.user.command;

import lombok.*;

import java.math.*;

@Getter
@Setter
@Builder
public class EditUserEntityCommand {

    private String firstName;

    private String telephoneNumber;

    private BigInteger moneyAmount;

}
