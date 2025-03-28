package com.pfe.BienImmobilier.services.impl;

import com.pfe.BienImmobilier.entities.Role;
import com.pfe.BienImmobilier.entities.RoleType;
import com.pfe.BienImmobilier.entities.Utilisateur;
import com.pfe.BienImmobilier.repository.RoleRepository;
import com.pfe.BienImmobilier.repository.UtilisateurRepository;
import com.pfe.BienImmobilier.security.PasswordHashUtil;
import com.pfe.BienImmobilier.services.inter.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UtilisateurServiceImpl implements UtilisateurService {
    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public Utilisateur enregistrerUtilisateur(Utilisateur utilisateur, Set<RoleType> roleTypes) {
        utilisateur.setMotDePasse(PasswordHashUtil.hashPassword(utilisateur.getMotDePasse()));

        // Assigner les rôles à l'utilisateur
        Set<Role> roles = roleTypes.stream()
                .map(roleType -> roleRepository.findByRoleType(roleType)
                        .orElseGet(() -> roleRepository.save(new Role(null, roleType))))
                .collect(Collectors.toSet());

        utilisateur.setRoles(roles);
        return utilisateurRepository.save(utilisateur);
    }

    @Override
    public boolean verifierMotDePasse(String motDePasseSaisi, String motDePasseHache) {
        return PasswordHashUtil.hashPassword(motDePasseSaisi).equals(motDePasseHache);
    }

    @Override
    public Optional<Utilisateur> trouverParEmail(String email) {
        return utilisateurRepository.findByEmail(email);
    }
}
