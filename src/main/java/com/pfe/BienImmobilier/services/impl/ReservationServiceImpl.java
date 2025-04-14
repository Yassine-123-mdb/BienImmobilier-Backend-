package com.pfe.BienImmobilier.services.impl;

import com.pfe.BienImmobilier.entities.BienImmobilier;
import com.pfe.BienImmobilier.entities.EStatutReservation;
import com.pfe.BienImmobilier.entities.Reservation;
import com.pfe.BienImmobilier.entities.Utilisateur;
import com.pfe.BienImmobilier.mapper.ReservationMapper;
import com.pfe.BienImmobilier.model.IndisponibiliteDTO;
import com.pfe.BienImmobilier.model.ReservationDTO;
import com.pfe.BienImmobilier.repository.BienImmobilierRepository;
import com.pfe.BienImmobilier.repository.ReservationRepository;
import com.pfe.BienImmobilier.repository.UserRepository;
import com.pfe.BienImmobilier.services.inter.EmailService;
import com.pfe.BienImmobilier.services.inter.ReservationService;
import com.pfe.BienImmobilier.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final BienImmobilierRepository bienRepository;
    private final UserRepository utilisateurRepository;
    private final JwtUtil jwtService; // Ton service JWT
    private final HttpServletRequest request;
    private final ReservationMapper reservationMapper;
    private final BienImmobilierRepository bienImmobilierRepository;
    private final EmailService emailService;


        @Override
        public ReservationDTO creerReservation(Reservation reservation, Long bienId) {
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new RuntimeException("Token manquant ou invalide.");
            }
            String token = authHeader.substring(7); // Remove "Bearer "

            // 🔐 Extraire l'email depuis le token
            String email = jwtService.extractEmail(token);
            Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            BienImmobilier bien = bienImmobilierRepository.findById(bienId)
                    .orElseThrow(() -> new RuntimeException("Bien non trouvé"));

            reservation.setUtilisateur(utilisateur);
            reservation.setBienImmobilier(bien);
            reservation.setPropietaire(bien.getProprietaire());
            reservation.setStatut(EStatutReservation.EN_ATTENTE);
            reservation.setDateReservation(LocalDateTime.now());

            List<Reservation> reservationsExistantes = reservationRepository
                    .findByBienImmobilierIdAndStatut(bienId, EStatutReservation.CONFIRMEE);

            for (Reservation existante : reservationsExistantes) {
                boolean chevauchement = reservation.getDateDebut().isBefore(existante.getDateFin()) &&
                        reservation.getDateFin().isAfter(existante.getDateDebut());
                if (chevauchement) {
                    throw new RuntimeException("Le bien est déjà réservé sur cette période.");
                }
            }
            Reservation saved = reservationRepository.save(reservation);
            String sujet = "Nouvelle réservation reçue";
            String message = "Bonjour " + bien.getProprietaire().getNom() + ",<br><br>" +
                    "Vous avez reçu une nouvelle réservation pour votre bien situé à : <strong>" + bien.getAdresse() + "</strong>.<br><br>" +
                    "Client : <strong>" + utilisateur.getNom() + " " + utilisateur.getPrenom() + "</strong><br>" +
                    "Email du client : <strong>" + utilisateur.getEmail() + "</strong><br><br>" +
                    "Veuillez vous connecter pour confirmer ou refuser cette réservation.<br><br>" +
                    "Cordialement,<br>L'équipe de Gestion Immobilière";

            emailService.envoyerEmail(bien.getProprietaire().getEmail(), sujet, message);
            return reservationMapper.toDTO(saved);
        }
    @Override
    public List<IndisponibiliteDTO> getIndisponibilitesParBien(Long bienId) {
        List<Reservation> reservations = reservationRepository.findByBienImmobilierIdAndStatut(bienId, EStatutReservation.CONFIRMEE);

        return reservations.stream()
                .map(res -> new IndisponibiliteDTO(res.getDateDebut(), res.getDateFin()))
                .collect(Collectors.toList());
    }

    @Override
    public ReservationDTO confirmerReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

        reservation.setStatut(EStatutReservation.CONFIRMEE);
        reservation.setConfirmeParProprietaire(true);
        reservationRepository.save(reservation);

        // Annuler les autres réservations en attente pour le même bien
        List<Reservation> autresReservations = reservationRepository
                .findByBienImmobilierIdAndStatutAndIdNot(
                        reservation.getBienImmobilier().getId(),
                        EStatutReservation.EN_ATTENTE,
                        reservation.getId()
                );

        for (Reservation r : autresReservations) {
            r.setStatut(EStatutReservation.ANNULEE);
        }

        reservationRepository.saveAll(autresReservations);
        return reservationMapper.toDTO(reservation);
    }

    @Override
    public void annulerReservationParClient(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));
        reservation.setAnnuleParClient(true);
        reservation.setStatut(EStatutReservation.ANNULEE);
        reservationRepository.save(reservation);
    }

    @Override
    public List<ReservationDTO> getReservationsParUtilisateur() {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token manquant ou invalide.");
        }
        String token = authHeader.substring(7); // Remove "Bearer "

        // 🔐 Extraire l'email depuis le token
        String email = jwtService.extractEmail(token);
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        return reservationRepository.findByUtilisateurId(utilisateur.getId()).stream()
                .map(reservationMapper::toDTO)
                .toList();
    }
    @Override
    public List<ReservationDTO> getReservationsParProprietaire() {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token manquant ou invalide.");
        }
        String token = authHeader.substring(7); // Remove "Bearer "

        // 🔐 Extraire l'email depuis le token
        String email = jwtService.extractEmail(token);
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        return reservationRepository.findByPropietaireId(utilisateur.getId()).stream()
                .map(reservationMapper::toDTO)
                .toList();
    }

    @Override
    public List<ReservationDTO> getReservationsParBien(Long bienId) {
        return reservationRepository.findByBienImmobilierId(bienId).stream()
                .map(reservationMapper::toDTO)
                .toList();
    }
}
