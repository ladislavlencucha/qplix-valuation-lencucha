package com.qplix.valuation;

import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {

    public static void main(String[] args) {
        if (args.length != 1) {
            log.error("Usage: java -jar qplix-valuation-lencucha.jar <date>");
            return;
        }

        LocalDate date = LocalDate.parse(args[0]);
        log.info("Valuation as of {}", date);

        QuotesLoader quotesLoader = new QuotesLoader();
        quotesLoader.load("/Quotes.csv");
    }

}