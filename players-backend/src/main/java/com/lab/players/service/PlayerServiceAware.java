package com.lab.players.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

/**
 * Service to provide CRUD operations for entities.
 *
 * @param <ENTITY> entity type.
 * @param <ID>     entity id type.
 */
public interface PlayerServiceAware<ENTITY, ID> {

    List<ENTITY> saveAll(List<ENTITY> entities);

    List<ENTITY> findAll();

    Optional<ENTITY> findById(ID id);

    Page<ENTITY> findAll(@NonNull Pageable pageable);

}
