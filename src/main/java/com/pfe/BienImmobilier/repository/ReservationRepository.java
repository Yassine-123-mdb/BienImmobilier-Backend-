package com.pfe.BienImmobilier.repository;

import com.pfe.BienImmobilier.entities.EStatutReservation;
import com.pfe.BienImmobilier.entities.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUtilisateurId(Long utilisateurId);
    List<Reservation> findByBienImmobilierId(Long bienId);
    List<Reservation> findByPropietaireId(Long utilisateurId);
    List<Reservation> findByBienImmobilierIdAndStatut(Long bienId, EStatutReservation statut);

    @Query("SELECT r FROM Reservation r WHERE r.bienImmobilier.id = :bienId AND r.statut = 'CONFIRMEE'")
    List<Reservation> findIndisponibilitesByBien(@Param("bienId") Long bienId);
    List<Reservation> findByBienImmobilierIdAndStatutAndIdNot(Long bienId, EStatutReservation statut, Long id);
    @Query("SELECT COALESCE(SUM(r.totalPrice), 0) FROM Reservation r WHERE MONTH(r.dateReservation) = :month AND YEAR(r.dateReservation) = :year")
    double sumRevenueByMonth(@Param("month") int month, @Param("year") int year);

    @Query("SELECT COALESCE(SUM(r.totalPrice), 0) FROM Reservation r WHERE YEAR(r.dateReservation) = :year")
    double sumRevenueByYear(@Param("year") int year);

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.statut = :statut")
    long countByStatut(@Param("statut") EStatutReservation  statut);
}


