package com.tistory.jaimemin.catalogservice.repository;

import com.tistory.jaimemin.catalogservice.entity.CatalogEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface CatalogRepository extends CrudRepository<CatalogEntity, Long> {

    CatalogEntity findByProductId(String productId);
}
