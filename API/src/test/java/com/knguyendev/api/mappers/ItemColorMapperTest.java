package com.knguyendev.api.mappers;

import com.knguyendev.api.TestUtil;
import com.knguyendev.api.domain.dto.ItemColor.ItemColorCreateDTO;
import com.knguyendev.api.domain.dto.ItemColor.ItemColorDTO;
import com.knguyendev.api.domain.entities.ItemColorEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class ItemColorMapperTest {
    private final ItemColorMapper itemColorMapper;

    @Autowired
    public ItemColorMapperTest(ItemColorMapper itemColorMapper) {
        this.itemColorMapper = itemColorMapper;
    }

    @Test
    void testCreateDTOMapsToEntity() {
        ItemColorCreateDTO createDTO = TestUtil.createItemColorCreateDTOA();
        ItemColorEntity itemColor = itemColorMapper.toEntity(createDTO);
        assertThat(itemColor.getId()).isNull();
        assertThat(itemColor.getName()).isEqualTo(createDTO.getName());
        assertThat(itemColor.getHexCode()).isEqualTo(createDTO.getHexCode());
    }

    @Test
    void testEntityMapsToDTO() {
        ItemColorEntity itemColorA = TestUtil.createSavedItemColorA();
        ItemColorDTO itemColorDTO = itemColorMapper.toDTO(itemColorA);
        assertThat(itemColorDTO.getId()).isEqualTo(itemColorA.getId());
        assertThat(itemColorDTO.getName()).isEqualTo(itemColorA.getName());
        assertThat(itemColorDTO.getHexCode()).isEqualTo(itemColorA.getHexCode());
    }
}