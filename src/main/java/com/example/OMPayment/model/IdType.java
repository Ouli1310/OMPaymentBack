package com.example.OMPayment.model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "TP_idType")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class IdType implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idTypeId")
    private Long id;
    @Column(name = "idTypeName")
    private String name;

}
