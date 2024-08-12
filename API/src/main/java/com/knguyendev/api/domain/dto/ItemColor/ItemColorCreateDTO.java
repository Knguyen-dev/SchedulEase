package com.knguyendev.api.domain.dto.ItemColor;

import com.knguyendev.api.domain.dto.ItemColor.constraints.HexCodeConstraint;
import com.knguyendev.api.domain.dto.ItemColor.constraints.NameConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * DTO used for creating new item colors (POST) and doing full updates
 * on existing item colors (PUT)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemColorCreateDTO {

    @NameConstraint
    private String name;

    @HexCodeConstraint
    private String hexCode;

    // Trim the data, and lowercase the values. Lowercase them since both values are unique, and this ensures uniqueness
    public void normalizeData() {
        name = name.trim().toLowerCase();
        hexCode = hexCode.toLowerCase(); // doesn't allow spaces so it doesn't need to be trimmed
    }
}
