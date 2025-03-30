package com.qplix.valuation;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;

/**
 * Valuation runner.
 */
@Slf4j
public class ValuationRunner {

    private final QuotesLoader quotesLoader = new QuotesLoader();
    private final InvestmentsLoader investmentsLoader = new InvestmentsLoader();

    public void load() {
        quotesLoader.load("/Quotes.csv");
        investmentsLoader.load("/Investments.csv");
    }

    public BigDecimal valuate(String investorId, LocalDate date) {
        log.info("Valuation of {} as of {}", investorId, date);

        BigDecimal result = null;

        log.info("Valuation of {} as of {} is {}", investorId, date, result);
        return result;
    }
}
