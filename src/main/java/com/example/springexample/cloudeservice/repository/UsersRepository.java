package com.example.springexample.cloudeservice.repository;

import com.example.springexample.cloudeservice.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<Users,Integer> {
    Users findById(int userId);
    Users findByLogin(String login);
}
