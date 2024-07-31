package com.knguyendev.api.services.impl;

import com.knguyendev.api.domain.dto.ItemColor.ItemColorCreateDTO;
import com.knguyendev.api.domain.entities.ItemColorEntity;
import com.knguyendev.api.exception.ServiceException;
import com.knguyendev.api.repositories.ItemColorRepository;
import com.knguyendev.api.services.ItemColorService;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ItemColorServiceImpl implements ItemColorService {
    private final ItemColorRepository itemColorRepository;
    public ItemColorServiceImpl(ItemColorRepository itemColorRepository) {
        this.itemColorRepository = itemColorRepository;
    }

    @Override
    public ItemColorEntity create(ItemColorCreateDTO itemColorCreateDTO) {
        Optional<ItemColorEntity> result = itemColorRepository.findByNameOrHexCode(
                itemColorCreateDTO.getName(),
                itemColorCreateDTO.getHexCode()
        );

        // if existingItemColor is defined, return error message telling the user
        if (result.isPresent()) {
            ItemColorEntity existingItemColor = result.get();
            if (existingItemColor.getName().equals(itemColorCreateDTO.getName())) {
                throw new ServiceException("ItemColor with the same name already exists!", HttpStatus.BAD_REQUEST);
            } else {
                throw new ServiceException("ItemColor with the same hexCode already exists!", HttpStatus.BAD_REQUEST);
            }
        }

        // Create domain object
        ItemColorEntity newItemColor = ItemColorEntity.builder()
                .name(itemColorCreateDTO.getName())
                .hexCode(itemColorCreateDTO.getHexCode())
                .build();

        // Save and return ItemColor domain object
        return itemColorRepository.save(newItemColor);
    }

    @Override
    public Optional<ItemColorEntity> find(Long id) {
        return itemColorRepository.findById(id);
    }

    @Override
    public ItemColorEntity update(Long id, ItemColorCreateDTO itemColorCreateDTO) {

        // Check that the id of the ItemColor being updated exists
        Optional<ItemColorEntity> result = itemColorRepository.findById(id);
        if (result.isEmpty()) {
            throw new ServiceException("Item color being updated not found!", HttpStatus.NOT_FOUND);
        }

        ItemColorEntity existingItemColor = result.get();

        // If the name or hexCode has changed, we'll perform a check to ensure the new values are unique
        boolean isNameChanged = !itemColorCreateDTO.getName().equals(existingItemColor.getName());
        boolean isHexCodeChanged = !itemColorCreateDTO.getHexCode().equals(existingItemColor.getHexCode());
        if (isNameChanged || isHexCodeChanged) {
            Optional<ItemColorEntity> conflictingItemColor = itemColorRepository.findByNameOrHexCode(
                    itemColorCreateDTO.getName(),
                    itemColorCreateDTO.getHexCode()
            );

            // If there exists an ItemColor, other than the one being updated, that matches the same name or hexCode, then we know the new values aren't unique!
            // As a result, throw a ServiceException with an appropriate message
            if (conflictingItemColor.isPresent() && !conflictingItemColor.get().getId().equals(id)) {
                ItemColorEntity conflicting = conflictingItemColor.get();
                String errorMessage = conflicting.getName().equals(itemColorCreateDTO.getName()) ?
                        "ItemColor with the same name already exists!" :
                        "ItemColor with the same hexCode already exists!";
                throw new ServiceException(errorMessage, HttpStatus.BAD_REQUEST);
            }
        }

        // At this point, we know the values from our DTO are valid, so update the fields that have changed and apply updates to database
        if (isNameChanged) {
            existingItemColor.setName(itemColorCreateDTO.getName());
        }
        if (isHexCodeChanged) {
            existingItemColor.setHexCode(itemColorCreateDTO.getHexCode());
        }
        return itemColorRepository.save(existingItemColor);
    }

    @Override
    public void delete(Long id) {
        itemColorRepository.deleteById(id);
    }

    @Override
    public List<ItemColorEntity> findAll() {
        return StreamSupport
                .stream(itemColorRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }
}
