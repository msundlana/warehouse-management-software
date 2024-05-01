package com.github.msundlana.warehousemanagementapi.services;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

public abstract class BaseService<TEntity, TDto> {

    private static final ModelMapper mapper = new ModelMapper();

    private final Class<TEntity> entityClass;
    private final Class<TDto> dtoClass;
    

    public BaseService(Class<TEntity> entityClass, Class<TDto> dtoClass) {
        this.entityClass = entityClass;
        this.dtoClass = dtoClass;
    }

    public TEntity convertToEntity(TDto dto) {
        return mapper.map(dto, this.entityClass);
    }

    public TEntity convertToEntity(TDto dto, TEntity entity) {
        return mapper.map(dto, this.entityClass);
    }

    public TDto convertToDto(TEntity entity) {
        return mapper.map(entity, this.dtoClass);
    }

    public List<TDto> convertToDto(List<TEntity> list) {
        return list.stream().map(this::convertToDto).toList();
    }

    public List<TEntity> convertToEntity(List<TDto> list) {
        return list.stream().map(this::convertToEntity).toList();
    }

    public static <TIn, TOut> TOut map(Class<TOut> outClass, TIn data) {
        return mapper.map(data, outClass);
    }

    public static <TIn, TOut> List<TOut> map(Class<TOut> outClass, List<TIn> list) {
        return list.stream().map(item -> map(outClass, item)).toList();
    }

}