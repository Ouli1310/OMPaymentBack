package com.example.OMPayment.model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Data
@Entity
@Table(name = "TP_profil")
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class Profil implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profilId")
    private Long id;
    @Column(name = "profilName")
    private String name;
    //@OneToMany(mappedBy = "profil")
    //private List<User> users;


}
