package org.banka1.exchangeservice.domains.dtos.exchange;

import com.opencsv.bean.CsvBindByPosition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeCSV {
    /**
     * Execute install lifecycle to be able to use @CsvBindByPosition
     */
    @CsvBindByPosition(position = 0)
    private String exchangeName;

    @CsvBindByPosition(position = 1)
    private String exchangeAcronym;

    @CsvBindByPosition(position = 2)
    private String exchangeMicCode;

    @CsvBindByPosition(position = 3)
    private String country;

    @CsvBindByPosition(position = 4)
    private String currency;

    @CsvBindByPosition(position = 5)
    private String timeZone;

    @CsvBindByPosition(position = 6)
    private String openTime;

    @CsvBindByPosition(position = 7)
    private String closeTime;
}
