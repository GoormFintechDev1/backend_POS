package com.example.pos.dto.pg;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DateRangeRequestDTO {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
