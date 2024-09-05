package com.sonarcube.eighty.repository;

import com.sonarcube.eighty.model.Car;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarCustomRepositoryImpl implements CarCustomRepository{

    @Autowired
    private EntityManager entity;

    @Override
    public Page<Car> findCarWithCustomQueryV2(Long makeId, String model, Integer year, Pageable pageable) {
        StringBuilder sql = new StringBuilder("SELECT * FROM car WHERE 1=1");
        if (makeId != null && makeId > 0) {
            sql.append(" AND make_id = :make_id");
        }
        if (!model.isEmpty() && !model.isBlank()) {
            sql.append(" AND model LIKE :model");
        }
        if (year != null) {
            sql.append(" AND year = :year");
        }

        Query query = entity.createNativeQuery(sql.toString(), Car.class);

        if (makeId != null && makeId > 0) {
            query.setParameter("make_id", makeId);
        }
        if (!model.isEmpty() && !model.isBlank()) {
            query.setParameter("model", "%" + model + "%");
        }
        if (year != null) {
            query.setParameter("year", year);
        }

        // Set pagination parameters
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<Car> cars = query.getResultList();

        // Get the total count of results
        Query countQuery = entity.createNativeQuery("SELECT COUNT(*) FROM (" + sql.toString() + ") AS countQuery");
        if (makeId != null && makeId > 0) {
            countQuery.setParameter("make_id", makeId);
        }
        if (!model.isEmpty() && !model.isBlank()) {
            countQuery.setParameter("model", "%" + model + "%");
        }
        if (year != null) {
            countQuery.setParameter("year", year);
        }
        long totalCount = ((Number) countQuery.getSingleResult()).longValue();

        return new PageImpl<>(cars, pageable, totalCount);
    }
}
