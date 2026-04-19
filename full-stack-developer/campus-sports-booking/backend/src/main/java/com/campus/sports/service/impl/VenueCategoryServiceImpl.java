package com.campus.sports.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.campus.sports.common.exception.BusinessException;
import com.campus.sports.common.result.Result;
import com.campus.sports.entity.system.venue.VenueCategory;
import com.campus.sports.mapper.VenueCategoryMapper;
import com.campus.sports.service.VenueCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class VenueCategoryServiceImpl implements VenueCategoryService {

    private final VenueCategoryMapper categoryMapper;

    public VenueCategoryServiceImpl(VenueCategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @Override
    public Result<List<VenueCategory>> getAll() {
        List<VenueCategory> list = categoryMapper.selectList(
            new LambdaQueryWrapper<VenueCategory>()
                .eq(VenueCategory::getStatus, 1)
                .orderByAsc(VenueCategory::getSortOrder)
        );
        return Result.success(list);
    }

    @Override
    public Result<VenueCategory> getById(Long id) {
        VenueCategory category = categoryMapper.selectById(id);
        if (category == null) {
            return Result.error("分类不存在");
        }
        return Result.success(category);
    }

    @Override
    public Result<Void> create(VenueCategory category) {
        categoryMapper.insert(category);
        log.info("创建场地分类: {}", category.getName());
        return Result.success("创建成功", null);
    }

    @Override
    public Result<Void> update(VenueCategory category) {
        if (category.getId() == null) {
            return Result.error("ID不能为空");
        }
        VenueCategory existing = categoryMapper.selectById(category.getId());
        if (existing == null) {
            return Result.error("分类不存在");
        }
        categoryMapper.updateById(category);
        log.info("更新场地分类: {}", category.getId());
        return Result.success("更新成功", null);
    }

    @Override
    public Result<Void> delete(Long id) {
        // 软删除
        categoryMapper.update(null,
            new LambdaUpdateWrapper<VenueCategory>()
                .eq(VenueCategory::getId, id)
                .set(VenueCategory::getStatus, 0)
        );
        log.info("删除场地分类: {}", id);
        return Result.success("删除成功", null);
    }
}
