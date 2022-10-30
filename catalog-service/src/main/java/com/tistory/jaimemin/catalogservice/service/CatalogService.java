package com.tistory.jaimemin.catalogservice.service;

import com.tistory.jaimemin.catalogservice.entity.CatalogEntity;

public interface CatalogService {

    Iterable<CatalogEntity> getAllCatalogs();
}
