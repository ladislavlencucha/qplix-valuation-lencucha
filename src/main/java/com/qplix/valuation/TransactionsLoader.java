package com.qplix.valuation;

import java.io.Reader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 * Transactions loader from a file.
 * <p>
 * This is one of many possible implementations of a loader. We could also always read from file or precalculate the values.
 */
@Slf4j
public class TransactionsLoader {

    /**
     * Quote values indexed by ISIN
     */
    private Map<String, NavigableMap<LocalDate, List<BigDecimal>>> transactions = Collections.emptyMap();

    /**
     * Load quotes from CSV file for given date.
     *
     * @param fileName name of the file to load
     */
    public void load(String fileName) {
        log.info("Loading transactions...");

        Map<String, NavigableMap<LocalDate, List<BigDecimal>>> newTransactions = new HashMap<>();

        try (Reader reader = ResourceReaderFactory.resourceFileReader(fileName);
             CSVParser csvParser = ResourceReaderFactory.csvParser(reader)) {
            for (CSVRecord csvRecord : csvParser) {
                String investmentId = csvRecord.get("InvestmentId");
                validateStringValue(investmentId, "Missing Investment ID for Transaction");

                String dateString = csvRecord.get("Date");
                validateStringValue(dateString, "Missing date for Transaction");
                LocalDate date = LocalDate.parse(dateString);

                String valueString = csvRecord.get("Value");
                validateStringValue(valueString, "Missing value for Transaction");
                BigDecimal value = new BigDecimal(valueString);

                addTransaction(investmentId, date, value, newTransactions);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to load quotes from file " + fileName, e);
        }

        transactions = newTransactions;

        log.info("Loaded {} transactions", newTransactions.size());
    }

    /**
     * Get summary value for given investment and date.
     *
     * @param investmentId investment for which to get value
     * @param date         date for which to get value
     * @return summary value for given investment and date or null if not present
     */
    public BigDecimal getValue(String investmentId, LocalDate date) {
        NavigableMap<LocalDate, List<BigDecimal>> transactionsForInvestment = transactions.get(investmentId);
        if (transactionsForInvestment == null) {
            log.warn("No transactions for investment {}", investmentId);
            return BigDecimal.ZERO;
        }

        // this will return value strictly before given 'date'
        return transactionsForInvestment.headMap(date, false)
                .values()
                .stream()
                .flatMap(List::stream)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Return number of records in cache.
     *
     * @return number of records in cache
     */
    public int size() {
        return transactions.values()
                .stream()
                .map(Map::size)
                .mapToInt(i -> i)
                .sum();
    }

    private void addTransaction(String investmentId, LocalDate date, BigDecimal value,
                                Map<String, NavigableMap<LocalDate, List<BigDecimal>>> newTransactions) {
        newTransactions.computeIfAbsent(investmentId, k -> new TreeMap<>())
                .computeIfAbsent(date, k -> new ArrayList<>())
                .add(value);
    }

    private static void validateStringValue(String stringValue, String exceptionMessage) {
        if (stringValue == null || stringValue.isBlank()) {
            throw new IllegalArgumentException(exceptionMessage);
        }
    }
}
