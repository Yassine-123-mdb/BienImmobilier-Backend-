package com.pfe.BienImmobilier.services.impl;

import com.pfe.BienImmobilier.model.BienImmobilierDTO;
import com.pfe.BienImmobilier.entities.BienImmobilier;
import com.pfe.BienImmobilier.mapper.BienImmobilierMapper;
import com.pfe.BienImmobilier.repository.BienImmobilierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BienImmobilierServiceImpl {

    private final BienImmobilierRepository bienImmobilierRepository;
    private final BienImmobilierMapper bienImmobilierMapper;


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

        System.out.println("Catégorie récupérée: " + (bien.getCategorie() != null ? bien.getCategorie().getNom() : "NULL"));

        return bienImmobilierMapper.toDTO(bien);
    }


    public BienImmobilierDTO createBien(BienImmobilier bien) {
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

        // Mise à jour des relations et attributs spécifiques
        bien.setCategorie(bienDetails.getCategorie());
        bien.setProprietaire(bienDetails.getProprietaire());
        bien.setCommune(bienDetails.getCommune());
        bien.setGouvernorat(bienDetails.getGouvernorat());

        BienImmobilier updatedBien = bienImmobilierRepository.save(bien);
        return bienImmobilierMapper.toDTO(updatedBien);
    }

    public void deleteBien(Long id) {
        BienImmobilier bien = bienImmobilierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bien non trouvé"));
        bienImmobilierRepository.delete(bien);
    }
}
