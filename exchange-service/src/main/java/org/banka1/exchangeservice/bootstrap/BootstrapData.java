package org.banka1.exchangeservice.bootstrap;

import com.opencsv.bean.CsvToBeanBuilder;
import org.banka1.exchangeservice.domains.dtos.exchange.ExchangeCSV;
import org.banka1.exchangeservice.services.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import org.banka1.exchangeservice.domains.dtos.currency.CurrencyCsvBean;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import java.util.ArrayList;


@Component
@AllArgsConstructor
//@Profile("local")
@Profile("!test_it")
public class BootstrapData implements CommandLineRunner {

    private final ExchangeService exchangeService;
    private final CurrencyService currencyService;
    private final ForexService forexService;
    private final StockService stockService;
    private final OptionService optionService;

    @Override
    public void run(String... args) throws Exception {

        // CURRENCY DATA
        List<CurrencyCsvBean> currencyCsvBeanList = getCurrencies();
        currencyService.persistCurrencies(currencyCsvBeanList);
        System.out.println("Currency Data Loaded!");


        // EXCHANGE DATA
        List<ExchangeCSV> exchangeCSVList = getExchangeData();
        exchangeService.persistExchanges(exchangeCSVList);
        System.out.println("Exchange Data loaded");

        //LISTING
        forexService.loadForex();
        System.out.println("Forexes loaded");
        stockService.loadStocks();
        System.out.println("Stocks loaded");

        //OPTIONS
        optionService.loadOptions();
        System.out.println("Options loaded");
    }

    public List<CurrencyCsvBean> getCurrencies() {
        FileReader fileReader;
        try {
            fileReader = new FileReader(ResourceUtils.getFile("exchange-service/csv-files/currencies.csv"));
        } catch (Exception e) {
            try {
                fileReader = new FileReader(ResourceUtils.getFile("classpath:csv/currencies.csv"));
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        }

        return new CsvToBeanBuilder<CurrencyCsvBean>(fileReader)
                .withType(CurrencyCsvBean.class)
                .withSkipLines(1)
                .build()
                .parse();
    }

    public List<ExchangeCSV> getExchangeData() {
        FileReader fileReader;
        try {
            fileReader = new FileReader(ResourceUtils.getFile("exchange-service/csv-files/exchange.csv"));
        } catch (Exception e) {
            try {
                fileReader = new FileReader(ResourceUtils.getFile("classpath:csv/exchange.csv"));
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        }

        return new CsvToBeanBuilder<ExchangeCSV>(fileReader)
                .withType(ExchangeCSV.class)
                .withSkipLines(1)
                .build()
                .parse();

    }
}
