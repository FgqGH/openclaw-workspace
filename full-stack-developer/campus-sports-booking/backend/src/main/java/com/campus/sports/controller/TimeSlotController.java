package com.campus.sports.controller;

import com.campus.sports.common.result.Result;
import com.campus.sports.entity.system.timeslot.TimeSlot;
import com.campus.sports.service.TimeSlotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "时段管理")
@RestController
@RequestMapping("/api/venue/timeslot")
public class TimeSlotController {

    private final TimeSlotService timeSlotService;

    public TimeSlotController(TimeSlotService timeSlotService) {
        this.timeSlotService = timeSlotService;
    }

    @Operation(summary = "获取场地某日所有时段")
    @GetMapping("/venue/{venueId}/date/{date}")
    public Result<List<TimeSlot>> getByVenueAndDate(
            @PathVariable Long venueId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return timeSlotService.getByVenueAndDate(venueId, date);
    }

    @Operation(summary = "获取场地某日可预约时段")
    @GetMapping("/venue/{venueId}/date/{date}/available")
    public Result<List<TimeSlot>> getAvailableSlots(
            @PathVariable Long venueId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return timeSlotService.getAvailableSlots(venueId, date);
    }

    @Operation(summary = "创建时段")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> create(@RequestBody TimeSlot timeSlot) {
        return timeSlotService.create(timeSlot);
    }

    @Operation(summary = "批量创建时段")
    @PostMapping("/batch")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> batchCreate(@RequestBody List<TimeSlot> timeSlots) {
        return timeSlotService.batchCreate(timeSlots);
    }

    @Operation(summary = "更新时段")
    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> update(@RequestBody TimeSlot timeSlot) {
        return timeSlotService.update(timeSlot);
    }

    @Operation(summary = "删除时段")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        return timeSlotService.delete(id);
    }
}
