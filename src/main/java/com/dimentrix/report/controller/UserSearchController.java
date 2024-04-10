package com.dimentrix.report.controller;

import com.dimentrix.report.model.User;
import com.dimentrix.report.payload.Search;
import com.dimentrix.report.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController()
@RequestMapping("/api/user/")
public class UserSearchController {

    @Autowired
    IUserService userService;

    @CrossOrigin
    @PostMapping("/search/{id}/{text}")
    public List<User> searchUser(@PathVariable("id") int id,@PathVariable("text") String text,@RequestBody Search request){

        System.out.println("Inside user Search  "+id);
        System.out.println("Inside user Search  "+text);
        System.out.println("Request   "+request);

        return userService.findAll();

    }
}
