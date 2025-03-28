package com.pfe.BienImmobilier.services.inter;

import com.pfe.BienImmobilier.entities.Reservation;
import java.util.List;

public interface ReservationService {
    Reservation reserverBien(Reservation reservation);
    List<Reservation> listerReservations();
    Reservation getReservationById(Long id);
    void annulerReservation(Long id);
}
