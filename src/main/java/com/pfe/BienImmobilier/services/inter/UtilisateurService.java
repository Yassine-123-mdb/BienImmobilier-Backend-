package com.pfe.BienImmobilier.services.inter;

import com.pfe.BienImmobilier.entities.RoleType;
import com.pfe.BienImmobilier.entities.Utilisateur;

import java.util.Optional;
import java.util.Set;

public interface UtilisateurService {
    Utilisateur enregistrerUtilisateur(Utilisateur utilisateur, Set<RoleType> roleTypes);
    Optional<Utilisateur> trouverParEmail(String email);
    boolean verifierMotDePasse(String motDePasseSaisi, String motDePasseHache) ;

    }

