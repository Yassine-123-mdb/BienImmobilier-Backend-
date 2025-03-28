package com.pfe.BienImmobilier.entities;

import com.pfe.BienImmobilier.entities.BienImmobilier;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idImage;

    private String name;
    private String type;

    @Column(name = "IMAGE", length = 4048576)
    @Lob
    private byte[] image; // Changez en byte[] pour stocker l'image en binaire

    @ManyToOne
    @JoinColumn(name = "bien_id")
    @JsonIgnore
    private BienImmobilier bienImmobilier;

    @ManyToOne
    @JoinColumn(name = "client_id")
    @JsonIgnore
    private Utilisateur utilisateur;

}
