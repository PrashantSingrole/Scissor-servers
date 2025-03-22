package com.salon.service.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.salon.service.model.PgTransaction;

@Repository
public interface PgTransactionDao extends JpaRepository<PgTransaction, Integer> {

	List<PgTransaction> findByUserIdOrderByIdDesc(Integer userId);

	List<PgTransaction> findByOrderIdOrderByIdDesc(String orderId);

	PgTransaction findByTypeAndOrderId(String type, String orderId);

	PgTransaction findByBookingIdAndType(String bookingId, String type);

}
