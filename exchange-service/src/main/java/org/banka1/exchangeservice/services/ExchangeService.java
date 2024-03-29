package org.banka1.exchangeservice.services;

import org.banka1.exchangeservice.domains.dtos.currency.CurrencyCsvBean;
import org.banka1.exchangeservice.domains.dtos.exchange.ExchangeCSV;
import org.banka1.exchangeservice.domains.entities.Currency;
import org.banka1.exchangeservice.domains.entities.Exchange;
import org.banka1.exchangeservice.domains.exceptions.NotFoundExceptions;
import org.banka1.exchangeservice.repositories.ExchangeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class ExchangeService {


    private ExchangeRepository exchangeRepository;

    public ExchangeService(ExchangeRepository exchangeRepository) {
        this.exchangeRepository = exchangeRepository;
    }


    public void persistExchanges(List<ExchangeCSV> exchangeCSVList) {
        List<Exchange> exchanges = new ArrayList<>();

        for(ExchangeCSV csv : exchangeCSVList) {
            Exchange exchange = Exchange.builder()
                    .excName(csv.getExchangeName())
                    .excAcronym(csv.getExchangeAcronym())
                    .excMicCode(csv.getExchangeMicCode())
                    .excCountry(csv.getCountry())
                    .excCurrency(csv.getCurrency())
                    .excTimeZone(csv.getTimeZone())
                    .excOpenTime(csv.getOpenTime())
                    .excCloseTime(csv.getCloseTime())
                    .build();

            exchanges.add(exchange);
        }
        exchangeRepository.saveAll(exchanges);
    }

    public List<Exchange> getExchanges(Integer page, Integer size){
        return (exchangeRepository.findAll(PageRequest.of(page, size))).getContent();
    }

   public Exchange findById(Long id){
        Optional<Exchange> exchange =  exchangeRepository.findById(id);
        if(exchange.isEmpty()){
           throw new NotFoundExceptions("Exchange not found");
        }
       return exchange.get();
   }

    public List<Exchange> findByNameLike(String name){
        List<Exchange> exchangeList = exchangeRepository.findByExcNameLike(name);
        if(exchangeList.isEmpty()){
            throw new NotFoundExceptions("Exchanges not found");
        }
        return exchangeList;
    }

    public Exchange findByAcronym(String acronym){
        Exchange exchange = exchangeRepository.findByExcAcronym(acronym);
        if(exchange == null){
            throw new NotFoundExceptions("Exchanges not found");
        }
        return exchange;
    }
    public Exchange findByIdMicCode(String micCode){
        Optional<Exchange> exchange = exchangeRepository.findByExcMicCode(micCode);
        if(exchange.isEmpty()){
            throw new NotFoundExceptions("Exchanges not found");
        }
        return exchange.get();
    }
}
