package com.sonarcube.eighty.repository;

import com.sonarcube.eighty.model.Car;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class CarCustomRepositoryImpl implements CarCustomRepository{

    @Autowired
    private EntityManager entity;

    @Override
    public Page<Car> findCarWithCustomQueryV2(String make, String model, Integer year, Boolean isElectric, Pageable pageable) {
        StringBuilder sql = new StringBuilder("SELECT * FROM car WHERE 1=1");
        if (make != null){
            sql.append(" AND make LIKE :make");
        }
        if (model != null) {
            sql.append(" AND model LIKE :model");
        }
        if (year != null) {
            sql.append(" AND year = :year");
        }
        if (isElectric != null) {
            sql.append(" AND is_electric = :isElectric");
        }

        Query query = entity.createNamedQuery(sql.toString(), Car.class);

        if (make != null) {
            query.setParameter("make", "%" + make + "%");
        }
        if (model != null) {
            query.setParameter("model", "%" + model + "%");
        }
        if (year != null) {
            query.setParameter("year", year);
        }
        if (isElectric != null) {
            query.setParameter("isElectric", isElectric);
        }

        // Set pagination parameters
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<Car> cars = query.getResultList();

        // Get the total count of results
        Query countQuery = entity.createNativeQuery("SELECT COUNT(*) FROM (" + sql.toString() + ") AS countQuery");
        long totalCount = ((Number) countQuery.getSingleResult()).longValue();

        return new PageImpl<>(cars, pageable, totalCount);
    }
}
