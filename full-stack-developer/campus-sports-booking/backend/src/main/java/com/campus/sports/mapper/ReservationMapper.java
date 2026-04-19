package com.campus.sports.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.sports.entity.system.reservation.Reservation;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReservationMapper extends BaseMapper<Reservation> {
}
