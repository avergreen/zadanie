package com.example.rankomat.controller;

import com.example.rankomat.error.*;
import com.example.rankomat.model.user.command.*;
import com.example.rankomat.model.user.*;
import com.example.rankomat.report.*;
import com.example.rankomat.service.*;
import lombok.*;
import org.modelmapper.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.*;

import static org.springframework.http.HttpStatus.OK;

@RestController
///user na /record
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers() {
        return new ResponseEntity<>(userService.findUsers()
                .stream()
                .map(user -> modelMapper.map(user, UserDto.class)).collect(Collectors.toList()), OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> findUserById(@PathVariable("id") Integer id) {
        RecordEntity entity = userService.findUserById(id);
        return new ResponseEntity<>(modelMapper.map(entity, UserDto.class), OK);
    }

    @PostMapping
    public ResponseEntity<UserDto> saveUser(@RequestBody CreateUserEntityCommand command) {
        RecordEntity userToAdd = modelMapper.map(command, RecordEntity.class);
        RecordEntity createdUser = userService.saveUser(userToAdd);
        return new ResponseEntity<UserDto>(modelMapper.map(createdUser, UserDto.class), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteUser(@PathVariable("id") Integer id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> editUser(@PathVariable("id") Integer id,
                                            @RequestBody EditUserEntityCommand command) {
        RecordEntity editedEntity = userService.editUser(id, command);
        return new ResponseEntity<>(modelMapper.map(editedEntity, UserDto.class), OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDto> editUserPartially(@PathVariable("id") Integer id,
                                                     @RequestBody EditUserEntityCommand command) {
        RecordEntity editedEntity = userService.editUserPartially(id, command);
        return new ResponseEntity<>(modelMapper.map(editedEntity, UserDto.class), OK);
    }

    @PostMapping("/process/{id}")
    public ResponseEntity<UserDto> processUserWithId(@PathVariable("id") Integer id) {
        RecordEntity processedEntity = userService.processTheRecord(id);
        return new ResponseEntity<>(modelMapper.map(processedEntity, UserDto.class), OK);
    }

    @GetMapping("/report")
    public ResponseEntity<ReportDto> getReportAboutUsers() {
        return new ResponseEntity<>(userService.generateReport(userService.findUsers()), OK);
    }

    @GetMapping("/process")
    public ResponseEntity<UserDto> getNextRecordToProcess() {
        Optional<RecordEntity> recordEntity = userService.findTheRecordToProcess();
        if (recordEntity.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(modelMapper.map(recordEntity.get(), UserDto.class), OK);
    }

}
