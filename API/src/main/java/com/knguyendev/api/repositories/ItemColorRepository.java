package com.knguyendev.api.repositories;

import com.knguyendev.api.domain.entities.ItemColorEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ItemColorRepository extends CrudRepository<ItemColorEntity, Long> {

    // Attempt to find one ItemColorEntity where the name or hex code matches.
    Optional<ItemColorEntity> findByNameOrHexCode(String name, String hexCode);

}
