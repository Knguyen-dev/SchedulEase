package com.knguyendev.api.services;

import com.knguyendev.api.domain.dto.ItemColor.ItemColorCreateDTO;
import com.knguyendev.api.domain.entities.ItemColorEntity;

import java.util.List;
import java.util.Optional;

public interface ItemColorService {

    /**
     * Creates a brand new ItemColor
     * @param itemColorCreateDTO A raw data object that should contain all the information the service layer needs to create a new ItemColor.
     * @return An entity representing the newly created ItemColor
     */
    ItemColorEntity create(ItemColorCreateDTO itemColorCreateDTO);

    /**
     * Attempts to find an existing ItemColor based on its ID value.
     * @param id ID of the ItemColor that you want to do an existence check on.
     * @return Returns the ItemColorEntity if found, else the 'Optional' will be empty.
     */
    Optional<ItemColorEntity> find(Long id);

    /**
     * Updates an existing ItemColor
     *
     * @param id ID value of the ItemColor we are updating
     * @param itemColorCreateDTO The raw data object that contains all we need to update an itemColor
     *
     * @return An entity object representing the newly updated ItemColor
     */
     ItemColorEntity update(Long id, ItemColorCreateDTO itemColorCreateDTO);

    void delete(Long id);

    // NOTE: There won't be a lot of colors, at most 25. So this doesn't need pagination.
    List<ItemColorEntity> findAll();
}
