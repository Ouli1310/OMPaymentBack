package com.example.OMPayment.serviceImpl;

import com.example.OMPayment.dto.UserDTO;
import com.example.OMPayment.model.User;
import com.example.OMPayment.payload.request.SignupRequest;
import com.example.OMPayment.repository.ProfilRepository;
import com.example.OMPayment.repository.UserRepository;
import com.example.OMPayment.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ProfilRepository profilRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).get();
    }

    @Override
    public List<User> getUserByProfil(Long id) {
        return userRepository.findUserByProfil(id);
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void createUser(SignupRequest signupRequest) {
       /** User newUser = new User();
        newUser.setFirstName(signupRequest.getFirstName());
        newUser.setLastName(signupRequest.getLastName());
        newUser.setEmail(signupRequest.getEmail());
        newUser.setMsisdn(signupRequest.getMsisdn());
        newUser.setProfil(profilRepository.getById(signupRequest.getProfil()));
        try {
            userRepository.save(newUser);
        }catch (Exception e) {
            return;
        } */


    }

    @Override
    public User updateUser(Long id, UserDTO userDto) {
        User currentUser = userRepository.findById(id).orElse(null);
        currentUser.setFirstName(userDto.getFirstName());
        currentUser.setLastName(userDto.getLastName());
        currentUser.setEmail(userDto.getEmail());
        currentUser.setMsisdn(userDto.getMsisdn());
        currentUser.setProfil(userDto.getProfil());
        User updatedUser = userRepository.save(currentUser);

        return updatedUser;
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }


}
