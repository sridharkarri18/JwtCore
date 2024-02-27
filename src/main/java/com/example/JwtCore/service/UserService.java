package com.example.JwtCore.service;

import com.example.JwtCore.entity.Roles;
import com.example.JwtCore.entity.Users;
import com.example.JwtCore.exceptions.UserDefinedException;
import com.example.JwtCore.model.requests.UserRequest;
import com.example.JwtCore.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Users> fetchAll() {
        return userRepository.findAll();
    }

    public Users newUser(UserRequest userRequest) throws UserDefinedException {
        Users user = new Users();
        Users existingUser = userRepository.findByEmail(userRequest.getEmail());

        if (existingUser == null) {
            user.setEmail(userRequest.getEmail());
            user.setName(userRequest.getName());
            user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
            if (userRequest.getEmail().toLowerCase().contains("@img.com")) {
                user.setRole(Roles.ADMIN);
            } else {
                user.setRole(Roles.USER);
            }
        } else {
            throw new UserDefinedException("Email already exists");
        }

        return userRepository.save(user);
    }

    public Users updateUser(UserRequest userRequest) throws UserDefinedException {
        Users user = userRepository.findById(getDetails())
                .orElseThrow(() -> new UserDefinedException("User not found with id: " + getDetails()));

        if (isValidString(userRequest.getName())) {
            user.setName(userRequest.getName());
        }
        if (isValidString(userRequest.getPassword())) {
            user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        }
        if (isValidString(userRequest.getEmail())) {
            user.setEmail(userRequest.getEmail());
        }

        return userRepository.save(user);
    }


    private boolean isValidString(String str) {
        return str != null && !str.trim().isEmpty();
    }


    public String softDelete(int userid) throws UserDefinedException {
        Users user = userRepository.findById(userid)
                .orElseThrow(() -> new UserDefinedException("User not found with id: " + userid));
        user.setStatus(1);
        return "User removed Successfully";
    }

    public String hardDelete(int userid) throws UserDefinedException {
        Users user = userRepository.findById(userid)
                .orElseThrow(() -> new UserDefinedException("User not found with id: " + userid));
        userRepository.deleteById(userid);
        return "Deleted Successfully";
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = userRepository.findByEmail(username);
        return user;
    }


    public int getDetails() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        int id = userRepository.findByEmail(name).getId();
        return id;
    }



}
