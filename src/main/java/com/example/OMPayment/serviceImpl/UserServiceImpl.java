package com.example.OMPayment.serviceImpl;

import com.example.OMPayment.dto.UserDTO;
import com.example.OMPayment.model.User;
import com.example.OMPayment.payload.request.SignupRequest;
import com.example.OMPayment.repository.CashInActorRepository;
import com.example.OMPayment.repository.PaymentActorRepository;
import com.example.OMPayment.repository.ProfilRepository;
import com.example.OMPayment.repository.UserRepository;
import com.example.OMPayment.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ProfilRepository profilRepository;
    private final PaymentActorRepository paymentActorRepository;
    private final CashInActorRepository cashInActorRepository;

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
    public User getUserByFirstName(String name) {
        return userRepository.findByFirstName(name);
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
    public User updateUser(Long id, SignupRequest user) {

        User currentUser = userRepository.findById(id).orElse(null);
        currentUser.setFirstName(user.getFirstName());
        currentUser.setLastName(user.getLastName());
        currentUser.setEmail(user.getEmail());
        currentUser.setMsisdn(user.getMsisdn());
        currentUser.setProfil(user.getProfil());
        currentUser.setEntite(user.getEntite());
        User updatedUser = userRepository.save(currentUser);

        return updatedUser;
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public Boolean passwordExist(String email) {
        User user = userRepository.findByEmail(email);
        if (user.getPassword().isEmpty()) {
            return false;
        }
        return true;
    }

    @Override
    public List<User> getUserByMsisdn(String msisdn) {
        return userRepository.findByMsisdn(msisdn);
    }

    @Override
    public Long getUserPinCode(String msisdn) {
        return paymentActorRepository.findByMsisdn(msisdn).getPinCode();
    }

    @Override
    public List<User> getUserByEntite(Long entite) {
        return userRepository.findByEntite(entite);
    }

    @Override
    public User getUserByEmailAndMsisdn(String email, String msisdn) {
        return userRepository.findByEmailAndMsisdn(email, msisdn);
    }

    @Override
    public void blockUser(String email) {
        User user = userRepository.findByEmail(email);
        user.setStatus(false);
        userRepository.save(user);

    }

    @Override
    public void unblockUser(String email) {
        User user = userRepository.findByEmail(email);
        user.setStatus(true);
        userRepository.save(user);
    }


}
