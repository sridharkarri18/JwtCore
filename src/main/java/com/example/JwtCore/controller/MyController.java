package com.example.JwtCore.controller;

import com.example.JwtCore.entity.Users;
import com.example.JwtCore.exceptions.UserDefinedException;
import com.example.JwtCore.model.requests.UserRequest;
import com.example.JwtCore.model.responses.UserResponsedto;
import com.example.JwtCore.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/User")
public class MyController {

    @Autowired
    private UserService userService;

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<UserResponsedto> fetchAllUsers() {
        return userService.fetchAll();
    }

    @PostMapping("/add")
    public UserResponsedto createUser(@Valid @RequestBody UserRequest userRequest) throws UserDefinedException {
        return userService.newUser(userRequest);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public UserResponsedto updateUser(@RequestBody UserRequest userRequest) throws UserDefinedException {
        return userService.updateUser(userRequest);
    }

    @DeleteMapping("/remove")
    @PreAuthorize("hasAuthority('USER')")
    public String remove() throws UserDefinedException {
        return userService.softDelete();
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String deleteUser(@RequestParam("userid") int userid) throws UserDefinedException {
        return userService.hardDelete(userid);
    }


}
