package com.pfe.BienImmobilier.controllers;

import com.pfe.BienImmobilier.model.BienImmobilierDTO;
import com.pfe.BienImmobilier.entities.BienImmobilier;
import com.pfe.BienImmobilier.services.impl.BienImmobilierServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4300")
@RestController
@RequestMapping("/api/biens")
@RequiredArgsConstructor
public class BienImmobilierController {

    private final BienImmobilierServiceImpl bienImmobilierService;

    @GetMapping("/top-offers")
    public ResponseEntity<List<BienImmobilierDTO>> getTopOffers() {
        return ResponseEntity.ok(bienImmobilierService.getTopOffers());
    }

    @GetMapping("/today-added")
    public ResponseEntity<List<BienImmobilierDTO>> getTodayAdded() {
        return ResponseEntity.ok(bienImmobilierService.getTodayAdded());
    }

    @GetMapping("/categorie/{categorie}")
    public ResponseEntity<List<BienImmobilierDTO>> getByCategorie(@PathVariable String categorie) {
        return ResponseEntity.ok(bienImmobilierService.getByCategorie(categorie));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BienImmobilierDTO> getBienById(@PathVariable Long id) {
        return ResponseEntity.ok(bienImmobilierService.getBienById(id));
    }

    @PostMapping
    public ResponseEntity<BienImmobilierDTO> createBien(@RequestBody BienImmobilier bien) {
        return ResponseEntity.ok(bienImmobilierService.createBien(bien));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BienImmobilierDTO> updateBien(@PathVariable Long id, @RequestBody BienImmobilier bienDetails) {
        return ResponseEntity.ok(bienImmobilierService.updateBien(id, bienDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBien(@PathVariable Long id) {
        bienImmobilierService.deleteBien(id);
        return ResponseEntity.noContent().build();
    }
}