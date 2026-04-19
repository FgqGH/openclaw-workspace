package com.campus.sports.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.campus.sports.common.result.Result;
import com.campus.sports.entity.system.venue.Venue;
import com.campus.sports.mapper.VenueMapper;
import com.campus.sports.service.VenueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class VenueServiceImpl implements VenueService {

    private final VenueMapper venueMapper;

    public VenueServiceImpl(VenueMapper venueMapper) {
        this.venueMapper = venueMapper;
    }

    @Override
    public Result<List<Venue>> getAll() {
        List<Venue> list = venueMapper.selectList(
            new LambdaQueryWrapper<Venue>()
                .eq(Venue::getStatus, 1)
                .orderByAsc(Venue::getCategoryId)
        );
        return Result.success(list);
    }

    @Override
    public Result<List<Venue>> getByCategory(Long categoryId) {
        List<Venue> list = venueMapper.selectList(
            new LambdaQueryWrapper<Venue>()
                .eq(Venue::getCategoryId, categoryId)
                .eq(Venue::getStatus, 1)
        );
        return Result.success(list);
    }

    @Override
    public Result<Venue> getById(Long id) {
        Venue venue = venueMapper.selectById(id);
        if (venue == null) {
            return Result.error("场地不存在");
        }
        return Result.success(venue);
    }

    @Override
    public Result<Void> create(Venue venue) {
        venueMapper.insert(venue);
        log.info("创建场地: {}", venue.getName());
        return Result.success("创建成功", null);
    }

    @Override
    public Result<Void> update(Venue venue) {
        if (venue.getId() == null) {
            return Result.error("ID不能为空");
        }
        Venue existing = venueMapper.selectById(venue.getId());
        if (existing == null) {
            return Result.error("场地不存在");
        }
        venueMapper.updateById(venue);
        log.info("更新场地: {}", venue.getId());
        return Result.success("更新成功", null);
    }

    @Override
    public Result<Void> delete(Long id) {
        venueMapper.update(null,
            new LambdaUpdateWrapper<Venue>()
                .eq(Venue::getId, id)
                .set(Venue::getStatus, 2)
        );
        log.info("删除场地: {}", id);
        return Result.success("删除成功", null);
    }
}
