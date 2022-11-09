package com.example.rankomat.service;

import com.example.rankomat.error.*;
import com.example.rankomat.model.user.command.*;
import com.example.rankomat.model.user.*;
import com.example.rankomat.report.*;
import com.example.rankomat.report.ReportDto.*;
import com.example.rankomat.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Service;

import javax.transaction.*;
import java.math.*;
import java.time.*;
import java.util.*;

import static java.util.Optional.ofNullable;


@Service
@RequiredArgsConstructor
public class UserService {

    @Value("${processing.users.threshold}")
    private BigInteger threshold;

    private final UserRepository userRepository;

    public RecordEntity saveUser(RecordEntity entity) {
        entity.setCreatedDate(LocalDate.now());
        return userRepository.save(entity);
    }

    public List<RecordEntity> findUsers() {
        return userRepository.findAll();
    }

    public RecordEntity findUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User with id: %s not found!", id)));
    }

    public void deleteUser(Integer id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new ResourceNotFoundException(String.format("User with id: %s not found!", id));
        }
    }

    public RecordEntity editUser(Integer id, EditUserEntityCommand command) {
        return userRepository.findById(id)
                .map(userToEdit -> {
                    userToEdit.setFirstName(command.getFirstName());
                    userToEdit.setTelephoneNumber(command.getTelephoneNumber());
                    userToEdit.setMoneyAmount(command.getMoneyAmount());
                    userRepository.save(userToEdit);
                    return userToEdit;
                }).orElseThrow(() -> new ResourceNotFoundException(String.format("User with id: %s not found!", id)));
    }

    @Transactional
    public RecordEntity editUserPartially(Integer id, EditUserEntityCommand command) {
        return userRepository.findById(id)
                .map(userToEdit -> {
                    ofNullable(command.getFirstName()).ifPresent(userToEdit::setFirstName);
                    ofNullable(command.getTelephoneNumber()).ifPresent(userToEdit::setTelephoneNumber);
                    ofNullable(command.getMoneyAmount()).ifPresent(userToEdit::setMoneyAmount);
                    //userRepository.save(userToEdit);
                    return userToEdit;
                }).orElseThrow(() -> new ResourceNotFoundException(String.format("User with id: %s not found!", id)));
    }

    @Transactional
    public RecordEntity processTheRecord(Integer id) {
        return userRepository.findById(id)
                .map(recordToProcess -> {
                    if (!recordToProcess.isProcessed()) {
                        recordToProcess.setProcessed(true);
                        recordToProcess.setProcessingDate(LocalDate.now());
                        return recordToProcess;
                    }
                    throw new RecordIsAlreadyProcessed(String.format("User with id: %s is already processed!", id));
                }).orElseThrow(() -> new ResourceNotFoundException(String.format("User with id: %s is not found!", id)));
    }

    @Transactional
    public Optional<RecordEntity> findTheRecordToProcess() {
        List<RecordEntity> allRecords = userRepository.findAll();
        LocalDate threeDaysAgo = LocalDate.now().minusDays(3);
        RecordEntity lowerPriorityRecord = null;

        for (RecordEntity recordEntity : allRecords) {
            if (!recordEntity.isProcessed()) {
                boolean isDateOk = true;
                for (RecordEntity otherEntity : allRecords) {
                    if (recordEntity.getTelephoneNumber().equals(otherEntity.getTelephoneNumber())) {
                        boolean notNull = otherEntity.getProcessingDate() != null;
                        if (notNull && otherEntity.getProcessingDate().isAfter(threeDaysAgo)) {
                            isDateOk = false;
                            break;
                        }
                    }
                }
                if (isDateOk) {
                    int result = recordEntity.getMoneyAmount().compareTo(threshold);
                    if (result >= 0) {
                        recordEntity.setProcessed(true);
                        recordEntity.setProcessingDate(LocalDate.now());
                        //userRepository.save(recordEntity);
                        return Optional.of(recordEntity);
                    }
                    if (lowerPriorityRecord == null) {
                        lowerPriorityRecord = recordEntity;
                    }
                }
            }

        }

        return Optional.ofNullable(lowerPriorityRecord).map(recordEntity -> {
            recordEntity.setProcessed(true);
            recordEntity.setProcessingDate(LocalDate.now());
            return recordEntity;
        });
    }

    public ReportDto generateReport(List<RecordEntity> userList) {

        ReportDto reportDto = new ReportDto();

        for (RecordEntity recordEntity : userList) {
            if (!reportDto.containsKey(recordEntity.getTelephoneNumber())) {
                reportDto.put(recordEntity.getTelephoneNumber(), new HashMap<>());
            }
            Map<String, DateSumDto> innerMap = reportDto.get(recordEntity.getTelephoneNumber());
            if (!innerMap.containsKey(recordEntity.getFirstName())) {
                innerMap.put(recordEntity.getFirstName(), new DateSumDto());
            }
            DateSumDto dateSumDto = innerMap.get(recordEntity.getFirstName());
            dateSumDto.setSum(dateSumDto.getSum().add(recordEntity.getMoneyAmount()));
            if (recordEntity.getCreatedDate().isAfter(dateSumDto.getLatestRecordDate())) {
                dateSumDto.setLatestRecordDate(recordEntity.getCreatedDate());
            }
        }
        return reportDto;
    }

}
