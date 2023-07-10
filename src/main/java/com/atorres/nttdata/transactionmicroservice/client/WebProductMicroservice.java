package com.atorres.nttdata.transactionmicroservice.client;

import com.atorres.nttdata.transactionmicroservice.model.RequestUpdateAccount;
import com.atorres.nttdata.transactionmicroservice.model.dao.AccountDao;
import com.atorres.nttdata.transactionmicroservice.model.dao.ClientDao;
import com.atorres.nttdata.transactionmicroservice.model.dao.CreditDao;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class WebProductMicroservice {
    WebClient client= WebClient.create("http://localhost:8081/api");

    public Flux<AccountDao> getAllAccountClient(String id){
        return client.get()
                .uri("/account/client/{id}",id)
                .retrieve()
                .bodyToFlux(AccountDao.class);
    }

    public Mono<AccountDao> updateAccount(RequestUpdateAccount request){
        return client.put()
                .uri("/account/update")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(AccountDao.class);
    }


    public Flux<CreditDao> getAllCreditClient(String id){
        return client.get()
                .uri("/credit/client/{id}",id)
                .retrieve()
                .bodyToFlux(CreditDao.class);
    }


}
