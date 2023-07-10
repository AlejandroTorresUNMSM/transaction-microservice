package com.atorres.nttdata.transactionmicroservice.repository;

import com.atorres.nttdata.transactionmicroservice.model.dao.TransactionDao;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransaccionRepository extends ReactiveMongoRepository<TransactionDao,String> {
}
