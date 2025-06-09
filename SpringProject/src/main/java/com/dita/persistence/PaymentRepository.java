package com.dita.persistence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.dita.domain.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    @Query("""
        SELECT 
            FUNCTION('DATE_FORMAT', p.paidAt, '%Y-%m') AS month,
            SUM(p.amount) AS total
        FROM Payment p
        WHERE p.payStatus = '완료'
        GROUP BY month
        ORDER BY month
    """)
    List<Object[]> getMonthlyRevenueList();
}
