package com.app.common.infrastructure.interfaces;

import com.app.common.infrastructure.request.FilterRequest;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 *
 * @author InuHa
 */
public interface IDAOinterface<T, I> {

    int insert(T model) throws SQLException;

    int update(T model) throws SQLException;

    int delete(I id) throws SQLException;

    boolean has(I id) throws SQLException;

    Optional<T> getById(I id) throws SQLException;

    List<T> selectAll() throws SQLException;

    List<T> selectPage(FilterRequest request) throws SQLException;

    int count(FilterRequest request) throws SQLException;

}
