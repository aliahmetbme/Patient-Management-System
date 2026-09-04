package com.aliahmet.pms.testorder;

import org.springframework.data.jpa.repository.JpaRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TestOrderRepository extends JpaRepository<TestOrder, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT testOrder
            FROM TestOrder testOrder
            WHERE testOrder.id = :id
            """)
    Optional<TestOrder> findByIdForUpdate(
            @Param("id") Long id
    );

}
