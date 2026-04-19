package com.campus.sports.controller;

import com.campus.sports.common.result.Result;
import com.campus.sports.entity.system.venue.Venue;
import com.campus.sports.service.VenueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "场地管理")
@RestController
@RequestMapping("/api/venue")
public class VenueController {

    private final VenueService venueService;

    public VenueController(VenueService venueService) {
        this.venueService = venueService;
    }

    @Operation(summary = "获取所有场地")
    @GetMapping("/list")
    public Result<List<Venue>> getAll() {
        return venueService.getAll();
    }

    @Operation(summary = "按分类获取场地")
    @GetMapping("/list/category/{categoryId}")
    public Result<List<Venue>> getByCategory(@PathVariable Long categoryId) {
        return venueService.getByCategory(categoryId);
    }

    @Operation(summary = "获取场地详情")
    @GetMapping("/{id}")
    public Result<Venue> getById(@PathVariable Long id) {
        return venueService.getById(id);
    }

    @Operation(summary = "创建场地")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> create(@RequestBody Venue venue) {
        return venueService.create(venue);
    }

    @Operation(summary = "更新场地")
    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> update(@RequestBody Venue venue) {
        return venueService.update(venue);
    }

    @Operation(summary = "删除场地")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        return venueService.delete(id);
    }
}
