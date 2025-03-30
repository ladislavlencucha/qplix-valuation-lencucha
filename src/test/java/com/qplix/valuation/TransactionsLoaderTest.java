package com.qplix.valuation;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class TransactionsLoaderTest {

    @Test
    void test() {
        TransactionsLoader transactionsLoader = new TransactionsLoader();
        transactionsLoader.load("/TransactionsLoaderTest/Transactions.csv");

        assertEquals(4, transactionsLoader.size(), "There should be 4 transactions");
        assertEquals(BigDecimal.ZERO, transactionsLoader.getValue("Investment1", LocalDate.of(2016, 1 ,3)), "Investment1 has no transactions before 2016-01-03");
        assertEquals(BigDecimal.valueOf(10.2), transactionsLoader.getValue("Investment1", LocalDate.of(2016, 1 ,4)), "Investment1 has transactions on 2016-01-03");
        assertEquals(BigDecimal.valueOf(25.3), transactionsLoader.getValue("Investment1", LocalDate.of(2018, 1 ,13)), "Investment1 has transactions on 2018-01-12");
        assertEquals(BigDecimal.valueOf(37.4), transactionsLoader.getValue("Investment1", LocalDate.of(2020, 1 ,16)), "Investment1 has transactions on 2020-01-15");
        assertEquals(BigDecimal.valueOf(13.1), transactionsLoader.getValue("Investment2", LocalDate.of(2016, 1 ,16)), "Investment2 has transactions on 2016-01-15");
    }
}