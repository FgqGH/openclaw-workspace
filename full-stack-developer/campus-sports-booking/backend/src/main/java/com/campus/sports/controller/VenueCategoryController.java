package com.campus.sports.controller;

import com.campus.sports.common.result.Result;
import com.campus.sports.entity.system.venue.VenueCategory;
import com.campus.sports.service.VenueCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "场地分类管理")
@RestController
@RequestMapping("/api/venue/category")
public class VenueCategoryController {

    private final VenueCategoryService categoryService;

    public VenueCategoryController(VenueCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(summary = "获取所有分类")
    @GetMapping("/list")
    public Result<List<VenueCategory>> getAll() {
        return categoryService.getAll();
    }

    @Operation(summary = "获取分类详情")
    @GetMapping("/{id}")
    public Result<VenueCategory> getById(@PathVariable Long id) {
        return categoryService.getById(id);
    }

    @Operation(summary = "创建分类")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> create(@RequestBody VenueCategory category) {
        return categoryService.create(category);
    }

    @Operation(summary = "更新分类")
    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> update(@RequestBody VenueCategory category) {
        return categoryService.update(category);
    }

    @Operation(summary = "删除分类")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        return categoryService.delete(id);
    }
}
