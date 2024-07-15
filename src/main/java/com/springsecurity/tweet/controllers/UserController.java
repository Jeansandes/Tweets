package com.springsecurity.tweet.controllers;

import com.springsecurity.tweet.dtos.UserDto;
import com.springsecurity.tweet.models.UserModel;
import com.springsecurity.tweet.services.UserService;
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
    //@PreAuthorize("hasAuthority('SCOPE_admin')")
    public ResponseEntity<Void> deleteUser(@RequestBody String username ){
        userService.delete(username);
        return ResponseEntity.ok().build();
    }
}





























