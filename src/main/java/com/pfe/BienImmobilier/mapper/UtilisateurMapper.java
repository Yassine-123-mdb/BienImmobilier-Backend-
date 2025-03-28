package com.pfe.BienImmobilier.mapper;

import com.pfe.BienImmobilier.entities.Role;
import com.pfe.BienImmobilier.entities.Utilisateur;
import com.pfe.BienImmobilier.model.UtilisateurDTO;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UtilisateurMapper {
    public UtilisateurDTO toDTO(Utilisateur utilisateur) {
        return new UtilisateurDTO(
                utilisateur.getId(),
                utilisateur.getNom(),
                utilisateur.getEmail(),
                utilisateur.getRoles().stream().map(Role::getRoleType).collect(Collectors.toSet())
        );
    }
}
