package org.example.repository;

import org.example.model.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @Query(value = """
            SELECT oi FROM OrderItem oi
            JOIN FETCH oi.order o
            WHERE o.id = :orderId AND o.user.id = :userId""")
    Page<OrderItem> findByOrderIdAndUserId(@Param("orderId") Long orderId,
                                           @Param("userId") Long userId,
                                           Pageable pageable);

    @Query(value = """
            SELECT oi FROM OrderItem oi
            JOIN FETCH oi.order o
            WHERE oi.id = :orderItemId AND o.id = :orderId AND o.user.id = :userId""")
    Optional<OrderItem> findByIdAndOrderIdAndUserId(@Param("orderItemId") Long orderItemId,
                                                    @Param("orderId") Long orderId,
                                                    @Param("userId") Long userId);
}
