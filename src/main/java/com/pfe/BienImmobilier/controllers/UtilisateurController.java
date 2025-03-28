package com.pfe.BienImmobilier.controllers;

import com.pfe.BienImmobilier.entities.Utilisateur;
import com.pfe.BienImmobilier.mapper.UtilisateurMapper;
import com.pfe.BienImmobilier.model.UtilisateurDTO;
import com.pfe.BienImmobilier.model.UtilisateurRequest;
import com.pfe.BienImmobilier.services.inter.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;

@RestController
@RequestMapping("/utilisateurs")
public class UtilisateurController {
    @Autowired
    private UtilisateurService utilisateurService;

    @Autowired
    private UtilisateurMapper utilisateurMapper;

    @PostMapping("/register")
    public ResponseEntity<UtilisateurDTO> register(@RequestBody UtilisateurRequest request) {
        Utilisateur savedUser = utilisateurService.enregistrerUtilisateur(
                Utilisateur.builder().nom(request.getNom())
                        .prenom(request.getPrenom())
                        .email(request.getEmail())
                        .motDePasse(request.getMotDePasse())
                        .telephone(request.getTelephone())
                        .build(),
                request.getRoles()
        );

        return ResponseEntity.ok(utilisateurMapper.toDTO(savedUser));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UtilisateurRequest request) {
        Optional<Utilisateur> utilisateurOpt = utilisateurService.trouverParEmail(request.getEmail());

        if (utilisateurOpt.isPresent()) {
            Utilisateur utilisateur = utilisateurOpt.get();

            if (utilisateurService.verifierMotDePasse(request.getMotDePasse(), utilisateur.getMotDePasse())) {
                return ResponseEntity.ok("Connexion réussie !");
            } else {
                return ResponseEntity.status(401).body("Mot de passe incorrect.");
            }
        }

        return ResponseEntity.status(404).body("Utilisateur non trouvé.");
    }

    @GetMapping("/{email}")
    public ResponseEntity<UtilisateurDTO> getByEmail(@PathVariable String email) {
        return utilisateurService.trouverParEmail(email)
                .map(utilisateurMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
