package com.knguyendev.api.controllers;

import com.knguyendev.api.domain.dto.ItemColor.ItemColorCreateDTO;
import com.knguyendev.api.domain.dto.ItemColor.ItemColorDTO;
import com.knguyendev.api.services.ItemColorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;


@RestController
@RequestMapping("/api/v1/itemColors")
public class ItemColorController {

    private final ItemColorService itemColorService;
    public ItemColorController(ItemColorService itemColorService) {
        this.itemColorService = itemColorService;
    }

    @GetMapping(path="/{id}")
    public ResponseEntity<ItemColorDTO> getItemColorById(@PathVariable("id") Long id) {
        return new ResponseEntity<>(itemColorService.findById(id), HttpStatus.OK);
    }

    @PutMapping(path="/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<ItemColorDTO> updateItemColorById(
            @PathVariable("id") Long id,
            @Valid @RequestBody ItemColorCreateDTO itemColorCreateDTO
    ) {
        itemColorCreateDTO.normalizeData();
        return new ResponseEntity<>(itemColorService.updateById(id, itemColorCreateDTO), HttpStatus.OK);
    }

    @DeleteMapping(path="/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<ItemColorDTO> deleteItemColorById(@PathVariable("id") Long id) {
        return new ResponseEntity<>(itemColorService.deleteById(id), HttpStatus.OK);
    }

    @GetMapping(path="")
    public ResponseEntity<List<ItemColorDTO>> getItemColorList() {
        return new ResponseEntity<>(itemColorService.findAll(), HttpStatus.OK);
    }

    @PostMapping(path="")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<ItemColorDTO> createItemColor(
            @Valid @RequestBody ItemColorCreateDTO itemColorCreateDTO
    ) {
        itemColorCreateDTO.normalizeData();
        return new ResponseEntity<>(itemColorService.create(itemColorCreateDTO), HttpStatus.OK);
    }

}
