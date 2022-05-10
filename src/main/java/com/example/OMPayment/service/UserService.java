package com.example.OMPayment.service;

import com.example.OMPayment.dto.UserDTO;
import com.example.OMPayment.model.User;
import com.example.OMPayment.payload.request.SignupRequest;

import java.util.List;

public interface UserService {

    List<User> getAllUsers();
    User getUserById(Long id);
    List<User> getUserByProfil(Long id);
    User getUserByEmail(String email);
    void createUser(SignupRequest signupRequest);
    User updateUser(Long id, UserDTO userDto);
    void deleteUser(Long id);
}
