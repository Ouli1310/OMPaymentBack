package com.example.OMPayment.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@Data
@Entity
@Table(name = "TD_Token")
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement
public class TokenOM implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tokenId")
    private Long id;
    @Column(name = "tokenString", columnDefinition = "TEXT")
    private String  token;
}
