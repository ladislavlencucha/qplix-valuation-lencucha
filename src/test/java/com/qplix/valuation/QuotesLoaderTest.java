package com.qplix.valuation;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class QuotesLoaderTest {

    @Test
    void test() {
        QuotesLoader quotesLoader = new QuotesLoader();
        quotesLoader.load("/QuotesLoaderTest/Quotes.csv");

        assertEquals(6, quotesLoader.size());
        assertNull(quotesLoader.getQuote("ISIN0", LocalDate.of(2016, 1, 4)), "There is no value for 2016-01-03 or before");
        assertEquals(BigDecimal.valueOf(17148.94), quotesLoader.getQuote("ISIN0", LocalDate.of(2016, 1, 5)), "There is value for 2016-01-04");
        assertEquals(BigDecimal.valueOf(17148.94), quotesLoader.getQuote("ISIN0", LocalDate.of(2018, 6, 21)), "There is value for 2016-01-04");
        assertEquals(BigDecimal.valueOf(17829.73), quotesLoader.getQuote("ISIN0", LocalDate.of(2018, 6, 22)), "There is value for 2018-06-21");
        assertEquals(BigDecimal.valueOf(17829.73), quotesLoader.getQuote("ISIN0", LocalDate.of(2020, 8, 20)), "There is value for 2018-06-21");
        assertEquals(BigDecimal.valueOf(27739.73), quotesLoader.getQuote("ISIN0", LocalDate.of(2020, 8, 21)), "There is value for 2020-08-20");
        assertEquals(BigDecimal.valueOf(27739.73), quotesLoader.getQuote("ISIN0", LocalDate.of(2025, 3, 25)), "There is value for 2020-08-20");

        assertNull(quotesLoader.getQuote("ISIN1", LocalDate.of(2016, 1, 1)), "There is no value for 2016-01-01 or before");
        assertEquals(BigDecimal.valueOf(295.28), quotesLoader.getQuote("ISIN1", LocalDate.of(2016, 1, 2)), "There is value for 2016-01-01");
        assertEquals(BigDecimal.valueOf(295.28), quotesLoader.getQuote("ISIN1", LocalDate.of(2016, 6, 7)), "There is value for 2016-01-01");
        assertEquals(BigDecimal.valueOf(299.462), quotesLoader.getQuote("ISIN1", LocalDate.of(2016, 6, 8)), "There is value for 2016-06-7");
        assertEquals(BigDecimal.valueOf(300.841), quotesLoader.getQuote("ISIN1", LocalDate.of(2016, 6, 9)), "There is value for 2016-06-8");
    }
}