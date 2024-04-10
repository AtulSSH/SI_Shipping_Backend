package com.dimentrix.report.service;


import com.dimentrix.report.model.User;
import com.dimentrix.report.repository.IUserSearchRepo;
import com.dimentrix.report.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class UserService implements  IUserService{

    @Autowired
    PasswordEncoder passwordEncoder;


    @Autowired
    UserRepository UserRepository;

    @Autowired
    IUserSearchRepo iUserSearchRepo;

    @Override
    public List<User> findAll() {
        System.out.println(" Inside user Service ");
        return iUserSearchRepo.findAll();
    }

    @Override
    public User getUserByID(long id) {
        return iUserSearchRepo.getUserById(id);
    }

    @Override
    @Transactional
    public Map<String,String> suspendUserByID(long id) {
        User user2 = iUserSearchRepo.getUserById(id);
        Map<String,String> map = new HashMap<>();
        if (user2 != null) {
            map.put("status","true");
            iUserSearchRepo.delete(user2);
            map.put("message","User deleted successfully");
            return map;
        }
        map.put("status","true");
        map.put("message","User Not Found");
        return map;

    }

    @Override
    public List<User> getAllUsers() {
        return iUserSearchRepo.findAll();
    }

    @Override
    @Transactional
    public Map<String,String> updateUser(User user) {
        Map<String,String> message = new HashMap();
        message.put("status","true");
        User user2 = iUserSearchRepo.getUserById(user.getId());
        if(user.getEmail() != null && !user.getEmail().equalsIgnoreCase(user2.getEmail())){
            User doesExists = iUserSearchRepo.getUserByEmail(user.getEmail());
            if(doesExists != null){
                message.put("status","false");
                message.put("message","This email already exists");
                return message;
            }
        }

        if(user2 != null){
            user2.setFirstName(user.getFirstName());
            user2.setLastName(user.getLastName());
            user2.setEmail(user.getEmail());
            user2.setUsername(user.getUsername());
            user2.setPrivileges(user.getPrivileges());
            iUserSearchRepo.save(user2);
            message.put("message","User update successfully");
            return  message;
        }
        message.put("status","false");
        message.put("message","User not found");
        return message;
    }


    @Override
    @Transactional
    public String updatePassword(User user) {

        User user2 = UserRepository.findByEmail(user.getEmail());

        if(user2 != null){
            user2.setPassword(passwordEncoder.encode(user.getPassword()));
            iUserSearchRepo.save(user2);
            return  "Password update successfully";
        }

        return "User not found";
    }


}
