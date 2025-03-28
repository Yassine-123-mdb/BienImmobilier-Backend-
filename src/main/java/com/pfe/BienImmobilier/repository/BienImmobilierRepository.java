package com.pfe.BienImmobilier.repository;

import com.pfe.BienImmobilier.entities.BienImmobilier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BienImmobilierRepository extends JpaRepository<BienImmobilier, Long> {



    @Query("SELECT b FROM BienImmobilier b " +
            "WHERE b.disponible = true " +
            "AND b.nombresChambres >= 3 " +  // Exiger un minimum de chambres
            "AND (b.piscine = true OR b.jardin = true) " + // Ou bien ayant piscine ou jardin
            "ORDER BY b.dateAjout DESC, b.nombresChambres DESC, b.prix ASC Limit 5")
    List<BienImmobilier> findTopOffers();


    @Query("SELECT b FROM BienImmobilier b ORDER BY b.dateAjout DESC LIMIT 5")
    List<BienImmobilier> findTodayAdded();

    @Query("SELECT b FROM BienImmobilier b WHERE b.categorie.nom = :categorie ORDER BY b.dateAjout DESC LIMIT 5")
    List<BienImmobilier> findByCategorie(@Param("categorie") String categorie);
}