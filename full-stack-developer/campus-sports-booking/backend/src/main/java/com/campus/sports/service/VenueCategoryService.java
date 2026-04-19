package com.campus.sports.service;

import com.campus.sports.common.result.Result;
import com.campus.sports.entity.system.venue.VenueCategory;

import java.util.List;

public interface VenueCategoryService {
    
    Result<List<VenueCategory>> getAll();
    
    Result<VenueCategory> getById(Long id);
    
    Result<Void> create(VenueCategory category);
    
    Result<Void> update(VenueCategory category);
    
    Result<Void> delete(Long id);
}
