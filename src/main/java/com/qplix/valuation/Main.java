package com.qplix.valuation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {

    public static void main(String[] args) throws IOException {
        ValuationRunner runner = new ValuationRunner();
        runner.load();

        System.out.printf("Type 'investorId;date' or press enter to exit%n");

        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
        String line = consoleReader.readLine();
        while (line != null && !line.isEmpty()) {
            String[] input = line.split(";");
            LocalDate date = LocalDate.parse(args[0]);
            String investorId = input[1];
            BigDecimal result = runner.valuate(investorId, date);

            System.out.printf("Valuation of %s as of %s is %s%n", investorId, date, result);

            line = consoleReader.readLine();
        }
    }
}