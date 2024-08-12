package com.knguyendev.api.mappers.impl;


import com.knguyendev.api.domain.dto.ItemColor.ItemColorCreateDTO;
import com.knguyendev.api.domain.dto.ItemColor.ItemColorDTO;
import com.knguyendev.api.domain.entities.ItemColorEntity;
import com.knguyendev.api.mappers.ItemColorMapper;
import org.springframework.stereotype.Component;

@Component
public class ItemColorMapperImpl implements ItemColorMapper {
    @Override
    public ItemColorEntity toEntity(ItemColorCreateDTO dto) {
        return ItemColorEntity.builder()
                .name(dto.getName())
                .hexCode(dto.getHexCode())
                .build();
    }

    @Override
    public ItemColorDTO toDTO(ItemColorEntity entity) {
        return ItemColorDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .hexCode(entity.getHexCode())
                .build();
    }
}
