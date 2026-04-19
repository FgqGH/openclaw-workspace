package com.campus.sports.service;

import com.campus.sports.common.result.Result;
import com.campus.sports.entity.system.venue.Venue;

import java.util.List;

public interface VenueService {
    
    Result<List<Venue>> getAll();
    
    Result<List<Venue>> getByCategory(Long categoryId);
    
    Result<Venue> getById(Long id);
    
    Result<Void> create(Venue venue);
    
    Result<Void> update(Venue venue);
    
    Result<Void> delete(Long id);
}
