package com.knguyendev.api.mappers;

import com.knguyendev.api.domain.dto.ItemColor.ItemColorCreateDTO;
import com.knguyendev.api.domain.dto.ItemColor.ItemColorDTO;
import com.knguyendev.api.domain.entities.ItemColorEntity;

public interface ItemColorMapper {

    ItemColorEntity toEntity(ItemColorCreateDTO dto);

    ItemColorDTO toDTO(ItemColorEntity entity);
}
