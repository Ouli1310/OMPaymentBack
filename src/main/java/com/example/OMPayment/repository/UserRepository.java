package com.example.OMPayment.repository;

import com.example.OMPayment.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);
    User findByMsisdn(String msisdn);
    User findByFirstName(String firstname);
    User findByLastName(String lastname);
    User findByFirstNameAndLastName(String firstname, String lastname);
    List<User> findUserByProfil(Long id);
    Boolean existsByEmail(String email);
}
