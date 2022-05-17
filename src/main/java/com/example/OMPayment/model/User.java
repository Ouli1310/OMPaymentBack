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
    private Long msisdn;
    @Column(name = "userPassword")
    private String password;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "user")
    private Balance balance;
    @Column(name = "userProfil")
    private Long profil;
    @OneToMany
    private Set<Transaction> userTransactions = new HashSet<>();

    public User(String firstName, String lastName, String email, String msisdn, String encode, Long profil) {
    }
}
