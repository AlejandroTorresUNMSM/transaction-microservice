package com.atorres.nttdata.transactionmicroservice.repository;

import com.atorres.nttdata.transactionmicroservice.model.dao.TransactionDao;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Repository
public interface TransaccionRepository extends ReactiveMongoRepository<TransactionDao, String> {
	@Query("{ 'fecha': { $gte: ?0, $lt: ?1 } }")
	Flux<TransactionDao> findAllByCurrentMonth(Date startOfMonth, Date startOfNextMonth);

	default Flux<TransactionDao> findAllByCurrentMonth() {
		LocalDate now = LocalDate.now();
		LocalDate startOfMonth = now.withDayOfMonth(1);
		LocalDate startOfNextMonth = now.plusMonths(1).withDayOfMonth(1);

		Date startDate = Date.from(startOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());
		Date endDate = Date.from(startOfNextMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());

		return findAllByCurrentMonth(startDate, endDate);
	}
}
