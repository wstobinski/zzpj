package com.handballleague.services;

import java.util.List;

public interface HandBallService<T> {
    T create(T entity);

    boolean delete(Long id);

    T update(Long id, T entity);

    T getById(Long id);

    List<T> getAll();

    boolean checkIfEntityExistsInDb(T entity);

    boolean checkIfEntityExistsInDb(Long entityID);
}
