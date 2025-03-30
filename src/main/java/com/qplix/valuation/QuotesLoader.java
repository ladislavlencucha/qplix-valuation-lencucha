package com.qplix.valuation;

import java.io.Reader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 * Quotes loader from a file
 */
@Slf4j
public class QuotesLoader {

    /**
     * Quote values indexed by ISIN
     */
    private Map<String, NavigableMap<LocalDate, BigDecimal>> quotes = Collections.emptyMap();

    /**
     * Load quotes from CSV file for given date.
     *
     * @param fileName name of the file to load
     */
    public void load(String fileName) {
        log.info("Loading quotes...");

        Map<String, NavigableMap<LocalDate, BigDecimal>> newQuotes = new HashMap<>();

        try (Reader reader = ResourceReaderFactory.resourceFileReader(fileName);
             CSVParser csvParser = ResourceReaderFactory.csvParser(reader)) {
            for (CSVRecord csvRecord : csvParser) {
                String isin = csvRecord.get("ISIN");
                validateStringValue(isin, "Missing ISIN for quote");

                String dateString = csvRecord.get("Date");
                validateStringValue(dateString, "Missing date for quote");
                LocalDate date = LocalDate.parse(dateString);

                String pricePerShareString = csvRecord.get("PricePerShare");
                validateStringValue(pricePerShareString, "Missing price per share for quote");
                BigDecimal pricePerShare = new BigDecimal(pricePerShareString);

                addQuote(isin, date, pricePerShare, newQuotes);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to load quotes from file " + fileName, e);
        }

        quotes = newQuotes;

        log.info("Loaded {} quotes", newQuotes.size());
    }

    /**
     * Get value for given ISIN and date.
     *
     * @param isin isin for which to get value
     * @param date date for which to get value
     * @return value for given ISIN and date or null if not present
     */
    public BigDecimal getQuote(String isin, LocalDate date) {
        NavigableMap<LocalDate, BigDecimal> quotesForIsin = quotes.get(isin);
        if (quotesForIsin == null) {
            log.warn("No quotes for ISIN {}", isin);
            return null;
        }

        // this will return value strictly before given 'date'
        Map.Entry<LocalDate, BigDecimal> entry = quotesForIsin.lowerEntry(date);
        if (entry == null) {
            log.warn("No quotes for ISIN {} before {}", isin, date);
            return null;
        }

        return entry.getValue();
    }

    /**
     * Return number of records in cache.
     *
     * @return number of records in cache
     */
    public int size() {
        return quotes.values()
                .stream()
                .map(Map::size)
                .mapToInt(i -> i)
                .sum();
    }

    private void addQuote(String isin, LocalDate date, BigDecimal pricePerShare,
                          Map<String, NavigableMap<LocalDate, BigDecimal>> newQuotes) {
        newQuotes.computeIfAbsent(isin, k -> new TreeMap<>())
                .put(date, pricePerShare);
    }

    private static void validateStringValue(String stringValue, String exceptionMessage) {
        if (stringValue == null || stringValue.isBlank()) {
            throw new IllegalArgumentException(exceptionMessage);
        }
    }
}
