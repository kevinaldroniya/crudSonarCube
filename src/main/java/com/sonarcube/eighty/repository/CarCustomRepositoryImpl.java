package com.sonarcube.eighty.repository;

import com.sonarcube.eighty.dto.CarFilterParams;
import com.sonarcube.eighty.model.Car;
import com.sonarcube.eighty.model.CarMake;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class CarCustomRepositoryImpl implements CarCustomRepository{

    private final EntityManager entity;

    @Override
    @Transactional
    public Page<Car> findCarWithCustomQueryV2(CarMake carMake, CarFilterParams carFilterParams, Pageable pageable) {
        StringBuilder sql = createQuery(carMake, carFilterParams);
        TypedQuery<Car> query = entity.createQuery(sql.toString(), Car.class);
        setQueryParam(carMake, carFilterParams, query);
        // Set pagination parameters
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<Car> cars = query.getResultList();

        // Get the total count of results
        String countSql = sql.toString().replace("SELECT c FROM Car c", "SELECT COUNT(c) FROM Car c");
        TypedQuery<Long> countQuery = entity.createQuery(countSql, Long.class);
        setQueryParam(carMake, carFilterParams, countQuery);
        long totalCount = countQuery.getSingleResult();
        return new PageImpl<>(cars, pageable, totalCount);
    }

    private StringBuilder createQuery(CarMake carMake, CarFilterParams carFilterParams) {
        StringBuilder sql = new StringBuilder("SELECT c FROM Car c WHERE 1=1");
        if (Objects.nonNull(carMake)) {
            sql.append(" AND c.carMake = :make_id");
        }
        if (!carFilterParams.getModel().isEmpty() && !carFilterParams.getModel().isBlank()) {
            sql.append(" AND c.model LIKE :model");
        }
        if (carFilterParams.getYear() > 0) {
            sql.append(" AND c.year = :year");
        }
        if (carFilterParams.getStatus().describeConstable().isPresent()) {
            sql.append(" AND c.status = :status");
        }
        if (!carFilterParams.getSortBy().isEmpty() && !carFilterParams.getSortDirection().isEmpty()) {
            sql.append(" ORDER BY c.").append(carFilterParams.getSortBy()).append(" ").append(carFilterParams.getSortDirection());
        }
        return sql;
    }

    private void setQueryParam(CarMake carMake, CarFilterParams carFilterParams, TypedQuery<?> query) {
        if (Objects.nonNull(carMake)) {
            query.setParameter("make_id", carMake);
        }
        if (!carFilterParams.getModel().isEmpty() && !carFilterParams.getModel().isBlank()) {
            query.setParameter("model", "%" + carFilterParams.getModel() + "%");
        }
        if (carFilterParams.getYear() > 0) {
            query.setParameter("year", carFilterParams.getYear());
        }
        if (carFilterParams.getStatus().describeConstable().isPresent()) {
            query.setParameter("status", carFilterParams.getStatus());
        }
    }
}
