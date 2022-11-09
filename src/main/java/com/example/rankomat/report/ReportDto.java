package com.example.rankomat.report;

import lombok.*;

import java.math.*;
import java.time.*;
import java.util.*;

public class ReportDto extends HashMap<String, Map <String, ReportDto.DateSumDto>> {
    @Getter
    @Setter
    public static class DateSumDto {
        private BigInteger sum = BigInteger.ZERO;
        private LocalDate latestRecordDate = LocalDate.MIN;
    }
}
