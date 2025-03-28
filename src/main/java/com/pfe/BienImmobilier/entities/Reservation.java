package com.pfe.BienImmobilier.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "reservations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date dateReservation;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;  // Associe la réservation à un utilisateur

    @ManyToOne
    @JoinColumn(name = "bien_immobilier_id")
    private BienImmobilier bienImmobilier;  // Associe la réservation à un bien immobilier
}
