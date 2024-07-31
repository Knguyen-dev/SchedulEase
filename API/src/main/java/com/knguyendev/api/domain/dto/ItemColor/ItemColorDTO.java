package com.knguyendev.api.domain.dto.ItemColor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemColorDTO {
    private Long id;
    private String name;
    private String hexCode;
}
