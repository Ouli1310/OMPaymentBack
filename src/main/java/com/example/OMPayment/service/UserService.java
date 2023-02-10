package com.example.OMPayment.service;

import com.example.OMPayment.dto.UserDTO;
import com.example.OMPayment.model.User;
import com.example.OMPayment.payload.request.SignupRequest;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface UserService {

    List<User> getAllUsers();
    User getUserById(Long id);
    List<User> getUserByProfil(Long id);
    User getUserByEmail(String email);
    User getUserByFirstName(String name);
    void createUser(SignupRequest signupRequest);
    User updateUser(Long id, SignupRequest signupRequest);
    void deleteUser(Long id);
    Boolean passwordExist(String email);
    List<User> getUserByMsisdn(String msisdn);
    Long getUserPinCode(String msisdn);
    List<User> getUserByEntite(Long entite);
    User getUserByEmailAndMsisdn(String email, String msisdn);
    void blockUser(String email);
    void unblockUser(String email);

}
