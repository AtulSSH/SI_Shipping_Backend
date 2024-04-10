package com.dimentrix.report.repository;

import com.dimentrix.report.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface IUserSearchRepo  extends JpaRepository<User, Long> {

     List<User> findAll();
     List<User> findAllById(long id);
     User getUserById(Long userId);
     User getUserByEmail(String email);
}
