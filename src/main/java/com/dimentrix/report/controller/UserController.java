package com.dimentrix.report.controller;

import com.dimentrix.report.model.Role;
import com.dimentrix.report.model.RoleName;
import com.dimentrix.report.model.User;
import com.dimentrix.report.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.dimentrix.report.service.IUserService;


import java.net.URI;
import java.util.*;

@RestController
@CrossOrigin
@RequestMapping("/api/user/")
public class UserController {

    @Autowired
    IUserService UserService;

    @Autowired
    UserRepository UserRepository;

    @Autowired
    PasswordEncoder passwordEncoder;


    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody User newUser) {
        System.out.println(newUser.getPrivileges());
        if (UserRepository.existsByUsername(newUser.getUsername())) {
            return new ResponseEntity(new ApiResponse(false, "Username is already taken!"),
                    HttpStatus.OK);
        }

        if (UserRepository.existsByEmail(newUser.getEmail())) {
            return new ResponseEntity(new ApiResponse(false, "Email Address already in use!"),
                    HttpStatus.OK);
        }

        Set<Role> roles = new HashSet<>();
        Role r  = new Role();
        r.setId(2L);
        r.setName(RoleName.ROLE_USER);

        roles.add(r);
        newUser.setRoles(roles);


        // Creating user's account

        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));

        newUser.setPrivileges(newUser.getPrivileges());

        User result = UserRepository.save(newUser);



        URI location = URI.create(String.format("/users/%s", result.getFirstName()));

        return ResponseEntity.created(location).body(new ApiResponse(true, "User registered successfully"));
    }


    @PostMapping("/getUserByID")
    public User getUserByID(@RequestBody User data){
        long id = data.getId();
        return UserService.getUserByID(id);
    }

    @PostMapping("/suspendUserByID")
    public Map<String,String> suspendUserByID(@RequestBody User data){
        long id = data.getId();
        return  UserService.suspendUserByID(id);
    }



    @GetMapping("/getAllUsers")
    public List<User> getAllUsers(){
        return UserService.getAllUsers();
    }


    @PostMapping("/updateUser")
    public Map<String,String> updateUser(@RequestBody User user){
       return UserService.updateUser(user);
    }

    @PostMapping("/updatePassword")
    public String updatePassword(@RequestBody User user){
        return  UserService.updatePassword(user);
    }

    @GetMapping("/logout")
        public ResponseEntity<?> userLogout(){
        if(SecurityContextHolder.getContext().getAuthentication().isAuthenticated()){
            SecurityContextHolder.getContext().getAuthentication().setAuthenticated(false);
            SecurityContextHolder.clearContext();
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "User Successfully Logged Out"));
        }
        SecurityContextHolder.getContext();
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(false, "User Not Logged In"));
        }
}
