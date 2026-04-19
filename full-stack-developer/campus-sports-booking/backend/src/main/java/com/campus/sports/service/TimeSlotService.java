package com.campus.sports.service;

import com.campus.sports.common.result.Result;
import com.campus.sports.entity.system.timeslot.TimeSlot;

import java.time.LocalDate;
import java.util.List;

public interface TimeSlotService {
    
    Result<List<TimeSlot>> getByVenueAndDate(Long venueId, LocalDate date);
    
    Result<List<TimeSlot>> getAvailableSlots(Long venueId, LocalDate date);
    
    Result<Void> create(TimeSlot timeSlot);
    
    Result<Void> batchCreate(List<TimeSlot> timeSlots);
    
    Result<Void> update(TimeSlot timeSlot);
    
    Result<Void> delete(Long id);
}
