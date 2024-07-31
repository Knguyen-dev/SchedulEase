package com.knguyendev.api.mappers.impl;


import com.knguyendev.api.domain.dto.ItemColor.ItemColorDTO;
import com.knguyendev.api.domain.entities.ItemColorEntity;
import com.knguyendev.api.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ItemColorMapperImpl implements Mapper<ItemColorEntity, ItemColorDTO> {

    // ModelMapper bean should be created in our config package
    private final ModelMapper modelMapper;
    public ItemColorMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }


    /**
     * @param itemColorEntity An entity object.
     * @return A DTO version of ItemColor
     */
    @Override
    public ItemColorDTO mapTo(ItemColorEntity itemColorEntity) {
        return modelMapper.map(itemColorEntity, ItemColorDTO.class);
    }

    /**
     * @param itemColorDTO A DTO object.
     * @return A domain object version of ItemColor
     */
    @Override
    public ItemColorEntity mapFrom(ItemColorDTO itemColorDTO) {
        return modelMapper.map(itemColorDTO, ItemColorEntity.class);
    }
}
