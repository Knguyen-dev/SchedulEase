package com.knguyendev.api.services.impl;

import com.knguyendev.api.domain.dto.ItemColor.ItemColorCreateDTO;
import com.knguyendev.api.domain.dto.ItemColor.ItemColorDTO;
import com.knguyendev.api.domain.entities.ItemColorEntity;
import com.knguyendev.api.exception.ServiceException;
import com.knguyendev.api.mappers.ItemColorMapper;
import com.knguyendev.api.repositories.ItemColorRepository;
import com.knguyendev.api.services.ItemColorService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
public class ItemColorServiceImpl implements ItemColorService {
    private final ItemColorRepository itemColorRepository;
    private final ItemColorMapper itemColorMapper;
    public ItemColorServiceImpl(ItemColorRepository itemColorRepository, ItemColorMapper itemColorMapper) {
        this.itemColorRepository = itemColorRepository;
        this.itemColorMapper = itemColorMapper;
    }

    @Override
    public ItemColorDTO create(ItemColorCreateDTO itemColorCreateDTO) throws ServiceException {
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

        ItemColorEntity newItemColor = itemColorMapper.toEntity(itemColorCreateDTO);
        return itemColorMapper.toDTO(itemColorRepository.save(newItemColor));
    }

    @Override
    public List<ItemColorDTO> findAll() {
        List<ItemColorEntity> itemColors = StreamSupport
                .stream(itemColorRepository.findAll().spliterator(), false)
                .toList();
        return itemColors.stream().map(itemColorMapper::toDTO).toList();
    }


    @Override
    public ItemColorDTO findById(Long id) throws ServiceException {
        Optional<ItemColorEntity> result = itemColorRepository.findById(id);
        if (result.isEmpty()) {
            throw new ServiceException("ItemColor not found!", HttpStatus.NOT_FOUND);
        }
        return itemColorMapper.toDTO(result.get());
    }


    @Override
    public ItemColorDTO updateById(Long id, ItemColorCreateDTO itemColorCreateDTO) throws ServiceException {

        // Check that the id of the ItemColor being updated exists
        Optional<ItemColorEntity> result = itemColorRepository.findById(id);
        if (result.isEmpty()) {
            throw new ServiceException("Item color being updated not found!", HttpStatus.NOT_FOUND);
        }

        ItemColorEntity existingItemColor = result.get();

        boolean isNameChanged = !itemColorCreateDTO.getName().equals(existingItemColor.getName());
        boolean isHexCodeChanged = !itemColorCreateDTO.getHexCode().equals(existingItemColor.getHexCode());

        // If both the name and hexCode haven't changed, then we can return the existing DTO
        if (!isNameChanged && !isHexCodeChanged) {
            return itemColorMapper.toDTO(existingItemColor);
        }

        // At this point, either the name or hexCode changed, or even both.
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

        // Apply any potential changes and save it to the database
        existingItemColor.setName(itemColorCreateDTO.getName());
        existingItemColor.setHexCode(itemColorCreateDTO.getHexCode());
        return itemColorMapper.toDTO(itemColorRepository.save(existingItemColor));
    }

    @Override
    public ItemColorDTO deleteById(Long id) throws ServiceException{
        Optional<ItemColorEntity> result = itemColorRepository.findById(id);
        if (result.isEmpty()) {
            throw new ServiceException("ItemColor with id '" + id + "' wasn't found!", HttpStatus.NOT_FOUND);
        }
        itemColorRepository.deleteById(id);
        return itemColorMapper.toDTO(result.get());
    }
}
