package org.example.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


// USER ROLE DATABASE CONFIG
@Entity
@Data // GIVES BUILDER
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "roles")
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) //AUTO INCREMENT
    @Column(name = "role_id")
    private Long roleId;

    private String name;

}
