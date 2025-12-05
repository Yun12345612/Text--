package org.example.springboot_hd.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Examination {

    private Long id;
    private String name;
    private BigDecimal price;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;

}
