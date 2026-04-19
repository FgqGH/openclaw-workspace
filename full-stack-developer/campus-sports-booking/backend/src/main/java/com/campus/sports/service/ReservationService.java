package com.campus.sports.service;

import com.campus.sports.common.result.Result;
import com.campus.sports.entity.system.reservation.Reservation;

import java.util.List;

public interface ReservationService {
    
    Result<Reservation> create(Long userId, Long timeSlotId);
    
    Result<Void> cancel(Long reservationId, Long userId);
    
    Result<List<Reservation>> getMyReservations(Long userId);
    
    Result<List<Reservation>> getAllReservations();
    
    Result<Void> adminCancel(Long reservationId);
}
