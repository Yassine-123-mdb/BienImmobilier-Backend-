package com.pfe.BienImmobilier.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_DEFAULT) // Masquer les attributs null
public class BienImmobilierDTO {
    private long id;
    private String titre;
    private String description;
    private String adresse;
    private double prix;
    private Boolean disponible; // Utilisation de Boolean
    private String typeTransaction;
    private Date dateAjout;
    private double surface;
    private String localisation;

    // Attributs spécifiques
    private Integer nombresChambres;
    private Integer nombresPieces;
    private Integer nombresSalledebain;
    private Boolean jardin; // Utilisation de Boolean
    private Boolean garage; // Utilisation de Boolean
    private Boolean climatiseur; // Utilisation de Boolean
    private Boolean piscine; // Utilisation de Boolean
    private Boolean balcon; // Utilisation de Boolean
    private Boolean vueSurMer; // Utilisation de Boolean
    private Integer nombresEtages;
    private double superficie;
    private Boolean isVerifieAdmin; // Utilisation de Boolean
    private Boolean constructible; // Utilisation de Boolean

    // Relations
    private String categorie;
    private String commune;
    private String gouvernorat;
    private String proprietaire;
    private List<String> avis;
    private Set<Long> utilisateursFavoris;

    // Added images field to DTO
    private List<String> images; // URLs or image paths of the images
}
