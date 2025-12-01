package org.example.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderUpdateDto {
    @NotNull
    private String status;
}
