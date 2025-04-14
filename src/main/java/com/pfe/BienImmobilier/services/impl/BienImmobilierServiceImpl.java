package com.pfe.BienImmobilier.services.impl;

import com.pfe.BienImmobilier.entities.*;
import com.pfe.BienImmobilier.mapper.BienImmobilierMapper;
import com.pfe.BienImmobilier.model.BienImmobilierDTO;
import com.pfe.BienImmobilier.model.BienImmobilierFilterDTO;
import com.pfe.BienImmobilier.repository.BienImmobilierRepository;
import com.pfe.BienImmobilier.repository.UserRepository;
import com.pfe.BienImmobilier.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BienImmobilierServiceImpl {

    private final BienImmobilierRepository bienImmobilierRepository;
    private final BienImmobilierMapper bienImmobilierMapper;
    private final CommuneService communeService; // Injection du service Commune
    private final JwtUtil jwtUtils;
    private final HttpServletRequest request;
    private final UserRepository utilisateurRepository;

    public Page<BienImmobilierDTO> searchBiens(BienImmobilierFilterDTO filter, Pageable pageable) {
        TypeTransaction typeTransaction = null;
        if (filter.getTypeTransaction() != null && !filter.getTypeTransaction().isEmpty()) {
            try {
                typeTransaction = TypeTransaction.valueOf(filter.getTypeTransaction().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Type de transaction invalide : " + filter.getTypeTransaction());
            }
        }

        Page<BienImmobilier> biens = bienImmobilierRepository.searchBiens(
                typeTransaction,
                filter.getCategorie(),
                filter.getLocalisation(),
                filter.getKeyword(),
                filter.getPrixMax(),
                filter.getSurfaceMin(),
                filter.getNombresPieces(),
                filter.getNombresChambres(),
                filter.getNombresSalledebain(),
                filter.getNombresEtages(),
                filter.getCommune(),
                filter.getGouvernorat(),
                pageable

        );

        System.out.println("Filtrage avec Commune ID: " + filter.getCommune());
        System.out.println("Filtrage avec Gouvernorat ID: " + filter.getGouvernorat());
        System.out.println("bien: " + biens);
        return biens.map(bienImmobilierMapper::toDTO);
    }

    public List<BienImmobilierDTO> getTopOffers() {
        return bienImmobilierRepository.findTopOffers().stream()
                .map(bienImmobilierMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<BienImmobilierDTO> getTodayAdded() {
        return bienImmobilierRepository.findTodayAdded().stream()
                .map(bienImmobilierMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<BienImmobilierDTO> getByCategorie(String categorie) {
        return bienImmobilierRepository.findByCategorie(categorie).stream()
                .map(bienImmobilierMapper::toDTO)
                .collect(Collectors.toList());
    }

    public BienImmobilierDTO getBienById(Long id) {
        BienImmobilier bien = bienImmobilierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bien non trouvé"));

        return bienImmobilierMapper.toDTO(bien);
    }
    public List<BienImmobilierDTO> getBiensDuProprietaireConnecte() {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token JWT manquant ou invalide.");
        }

        String token = authHeader.substring(7);
        String email = jwtUtils.extractEmail(token);

        Utilisateur proprietaire = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé."));

        List<BienImmobilier> biens = bienImmobilierRepository.findByProprietaire(proprietaire);
        return biens.stream().map(bienImmobilierMapper::toDTO).collect(Collectors.toList());
    }


    public BienImmobilierDTO createBien(BienImmobilier bien) {
        // Vérifier et récupérer la commune
        Commune commune = bien.getCommune();
        if (commune == null || commune.getId() == null) {
            throw new RuntimeException("La commune est requise pour créer un bien.");
        }
        commune = communeService.getCommuneById(commune.getId());
        Gouvernorat gouvernorat = commune.getGouvernorat();

        // 🔐 Extraire le token JWT depuis l'en-tête
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token manquant ou invalide.");
        }
        String token = authHeader.substring(7); // Remove "Bearer "

        // 🔐 Extraire l'email depuis le token
        String email = jwtUtils.extractEmail(token);

        // 🔐 Trouver l'utilisateur
        Utilisateur proprietaire = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Associer l'utilisateur en tant que propriétaire
        bien.setProprietaire(proprietaire);
        bien.setCommune(commune);
        bien.setGouvernorat(gouvernorat);

        BienImmobilier savedBien = bienImmobilierRepository.save(bien);
        return bienImmobilierMapper.toDTO(savedBien);
    }

    public BienImmobilierDTO updateBien(Long id, BienImmobilier bienDetails) {
        BienImmobilier bien = bienImmobilierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bien non trouvé"));

        bien.setTitre(bienDetails.getTitre());
        bien.setDescription(bienDetails.getDescription());
        bien.setAdresse(bienDetails.getAdresse());
        bien.setPrix(bienDetails.getPrix());
        bien.setDisponible(bienDetails.isDisponible());
        bien.setTypeTransaction(bienDetails.getTypeTransaction());
        bien.setDateAjout(bienDetails.getDateAjout());
        bien.setSurface(bienDetails.getSurface());
        bien.setLocalisation(bienDetails.getLocalisation());

        // Mise à jour de la commune et gouvernorat
        if (bienDetails.getCommune() != null && bienDetails.getCommune().getId() != null) {
            Commune commune = communeService.getCommuneById(bienDetails.getCommune().getId());
            bien.setCommune(commune);
            bien.setGouvernorat(commune.getGouvernorat());
        }

        BienImmobilier updatedBien = bienImmobilierRepository.save(bien);
        return bienImmobilierMapper.toDTO(updatedBien);
    }

    public void deleteBien(Long id) {
        BienImmobilier bien = bienImmobilierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bien non trouvé"));
        bienImmobilierRepository.delete(bien);
    }
}
