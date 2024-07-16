package com.springsecurity.tweet.controllers;

import com.springsecurity.tweet.dtos.UserDto;
import com.springsecurity.tweet.dtos.UserNameDto;
import com.springsecurity.tweet.models.UserModel;
import com.springsecurity.tweet.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@ResponseBody
@RequestMapping("/users")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<Void> createUser(@RequestBody UserDto dto) {
        userService.save(dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_admin')")
    public ResponseEntity<List<UserModel>> ListUsers(){

        return ResponseEntity.ok(userService.findAll());
    }

    @DeleteMapping("/username")
    @PreAuthorize("hasAuthority('SCOPE_admin')")
    public ResponseEntity<String> deleteUser(@RequestBody UserNameDto dto ){
        userService.delete(dto.username());
        return ResponseEntity.status(HttpStatus.OK).body("excluido com sucesso :"+dto.username());
    }
}





























