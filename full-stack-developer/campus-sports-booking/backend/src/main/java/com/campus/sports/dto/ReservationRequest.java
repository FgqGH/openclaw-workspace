package com.campus.sports.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReservationRequest {

    @NotNull(message = "时段ID不能为空")
    private Long timeSlotId;
}
