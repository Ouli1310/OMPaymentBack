package com.example.OMPayment.model;

import com.example.OMPayment.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Data
@Entity
@Table(name = "TD_user")
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId")
    private Long id;
    @Column(name = "userFirstname")
    private String firstName;
    @Column(name = "userLastname")
    private String lastName;
    @Column(name = "userEmail")
    private String email;
    @Column(name = "userMsisdn")
    private String msisdn;
    @Column(name = "userCode")
    private String code;
    @Column(name = "userPassword")
    private String password;
    @Column(name = "userTokenOM", columnDefinition = "TEXT")
    private String tokenOM;
    @Column(name = "userResetPasswordToken", columnDefinition = "TEXT")
    private String requestPasswordToken;
    @Column(name = "userPublicKey")
    private String publicKey;
    @Column(name = "userProfil")
    private Long profil;
    @Column(name = "userEntite")
    private Long entite;
    @Column(name = "userStatus")
    private Boolean status;
    @OneToOne
    private Balance balance;


}
