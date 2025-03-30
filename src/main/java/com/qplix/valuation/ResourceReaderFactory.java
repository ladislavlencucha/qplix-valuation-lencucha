package com.qplix.valuation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Objects;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

/**
 * Factory for reading resource files (e.g. CSV).
 */
public class ResourceReaderFactory {

    private static final CSVFormat CSV_FORMAT = CSVFormat.DEFAULT.builder()
            .setHeader()
            .setSkipHeaderRecord(true)
            .setDelimiter(';')
            .build();

    public static BufferedReader resourceFileReader(String name) {
        return new BufferedReader(
                new InputStreamReader(
                        Objects.requireNonNull(
                                ResourceReaderFactory.class.getResourceAsStream(name))));
    }

    public static CSVParser csvParser(Reader reader) throws IOException {
        return new CSVParser(reader, CSV_FORMAT);
    }

}
