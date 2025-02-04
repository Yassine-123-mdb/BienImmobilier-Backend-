package com.pfe.BienImmobilier.services.impl;

import com.pfe.BienImmobilier.entities.*;
import com.pfe.BienImmobilier.mapper.BienImmobilierMapper;
import com.pfe.BienImmobilier.model.BienImmobilierDTO;
import com.pfe.BienImmobilier.model.BienImmobilierFilterDTO;
import com.pfe.BienImmobilier.repository.BienImmobilierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BienImmobilierServiceImpl {

    private final BienImmobilierRepository bienImmobilierRepository;
    private final BienImmobilierMapper bienImmobilierMapper;
    private final CommuneService communeService; // Injection du service Commune

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

    public BienImmobilierDTO createBien(BienImmobilier bien) {
        // Récupérer la commune avec son ID
        Commune commune = bien.getCommune();
        if (commune == null || commune.getId() == null) {
            throw new RuntimeException("La commune est requise pour créer un bien.");
        }

        // Vérifier que la commune existe bien
        commune = communeService.getCommuneById(commune.getId());

        // Récupérer le gouvernorat associé à la commune
        Gouvernorat gouvernorat = commune.getGouvernorat();

        // Assigner les relations
        bien.setCommune(commune);
        bien.setGouvernorat(gouvernorat);

        // Sauvegarde du bien
        BienImmobilier savedBien = bienImmobilierRepository.save(bien);

        return bienImmobilierMapper.toDTO(savedBien);
    }

    public BienImmobilierDTO updateBien(Long id, BienImmobilier bienDetails) {
        BienImmobilier bien = bienImmobilierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bien non trouvé"));

        // Mise à jour des champs
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
