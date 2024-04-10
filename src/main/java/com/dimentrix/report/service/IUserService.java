package com.dimentrix.report.service;


import com.dimentrix.report.model.User;

import java.util.List;
import java.util.Map;

public interface IUserService {

    public List<User> findAll();

    User getUserByID(long id);

    public List<User> getAllUsers();

    Map<String,String> updateUser(User user);

    String updatePassword(User user);


    Map<String,String> suspendUserByID(long id);
}
