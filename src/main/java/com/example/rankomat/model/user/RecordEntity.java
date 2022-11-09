package com.example.rankomat.model.user;

import lombok.*;
import javax.persistence.*;
import javax.validation.constraints.*;
import java.math.*;
import java.time.*;
import java.util.*;

@Entity
@Table(name = "users")
@Getter
@Setter
public class RecordEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @NotEmpty
    @Size(min = 2, message = "FIRSTNAME TOO SHORT")
    @Pattern(regexp="^[^0-9]*$", message = "NAME CONTAINS INVALID CHARACTERS")
    private String firstName;

    @NotEmpty
    @Size(min = 9, message = "TELEPHONENUMBER TOO SHORT")
    //na razie zwykly pattern na ciag 9 cyfr
    @Pattern(regexp="^\\d{9}$", message = "TELEPHONENUMBER CONTAINS INVALID CHARACTERS")
    private String telephoneNumber;

    //zamiast patternu inicjalizacja Waluty, tylko jak to zrobić dla userEntity, tak zeby nie było to w klasie?
    private BigInteger moneyAmount;

    private LocalDate createdDate;

    private LocalDate processingDate;

    boolean processed = false;



}
