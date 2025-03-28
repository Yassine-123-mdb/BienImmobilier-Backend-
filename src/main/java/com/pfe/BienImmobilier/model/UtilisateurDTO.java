package com.pfe.BienImmobilier.model;

import com.pfe.BienImmobilier.entities.RoleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UtilisateurDTO {
    private Long id;
    private String nom;
    private String email;
    private Set<RoleType> roles;
}

