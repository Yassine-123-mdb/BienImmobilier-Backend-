package com.pfe.BienImmobilier.services.impl;

import com.pfe.BienImmobilier.entities.Reservation;
import com.pfe.BienImmobilier.repository.ReservationRepository;
import com.pfe.BienImmobilier.services.inter.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Override
    public Reservation reserverBien(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    @Override
    public List<Reservation> listerReservations() {
        return reservationRepository.findAll();
    }

    @Override
    public Reservation getReservationById(Long id) {
        return reservationRepository.findById(id).orElse(null);
    }

    @Override
    public void annulerReservation(Long id) {
        reservationRepository.deleteById(id);
    }
}
