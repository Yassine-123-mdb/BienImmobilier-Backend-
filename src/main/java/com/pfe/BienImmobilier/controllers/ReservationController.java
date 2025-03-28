package com.pfe.BienImmobilier.controllers;

import com.pfe.BienImmobilier.entities.Reservation;
import com.pfe.BienImmobilier.services.inter.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @PostMapping("/ajouter")
    public Reservation reserverBien(@RequestBody Reservation reservation) {
        return reservationService.reserverBien(reservation);
    }

    @GetMapping("/liste")
    public List<Reservation> listerReservations() {
        return reservationService.listerReservations();
    }

    @GetMapping("/{id}")
    public Reservation getReservationById(@PathVariable Long id) {
        return reservationService.getReservationById(id);
    }

    @DeleteMapping("/annuler/{id}")
    public void annulerReservation(@PathVariable Long id) {
        reservationService.annulerReservation(id);
    }
}
