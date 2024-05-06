package com.handballleague.services;

import java.util.List;

public interface HandBallService<T> {
    /**
     * Creates an entity.
     * @param entity The entity to be created.
     * @return The created entity.
     */
    T create(T entity);

    /**
     * Deletes an entity based on its identifier.
     * @param id The identifier of the entity to be deleted.
     * @return true if the entity was successfully deleted, false otherwise.
     */
    boolean delete(Long id);

    /**
     * Updates an existing entity.
     * @param id The identifier of the entity to be updated.
     * @param entity The updated entity.
     * @return The updated entity.
     */
    T update(Long id, T entity);

    /**
     * Retrieves an entity by its identifier.
     * @param id The identifier of the entity.
     * @return The entity, or null if not found.
     */
    T getById(Long id);

    /**
     * Retrieves all entities.
     * @return A list of all entities.
     */
    List<T> getAll();

    /**
     * Checks if an entity already exists in the database.
     * @param entity The entity to check.
     * @return true if the entity exists, false otherwise.
     */
    boolean checkIfEntityExistsInDb(T entity);

    /**
     * Checks if an entity with the specified identifier exists in the database.
     * @param entityID The identifier of the entity to check.
     * @return true if the entity exists, false otherwise.
     */
    boolean checkIfEntityExistsInDb(Long entityID);

}
