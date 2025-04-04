package com.qplix.valuation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;

/**
 * Valuation runner.
 */
@Slf4j
public class ValuationRunner {

    private final QuotesLoader quotesLoader;
    private final InvestmentsLoader investmentsLoader;
    private final TransactionsLoader transactionsLoader;

    public ValuationRunner() {
        this(new QuotesLoader(), new InvestmentsLoader(), new TransactionsLoader());
    }

    ValuationRunner(
            QuotesLoader quotesLoader, InvestmentsLoader investmentsLoader, TransactionsLoader transactionsLoader) {
        this.quotesLoader = quotesLoader;
        this.investmentsLoader = investmentsLoader;
        this.transactionsLoader = transactionsLoader;
    }

    public void load() {
        quotesLoader.load("/Quotes.csv");
        investmentsLoader.load("/Investments.csv");
        transactionsLoader.load("/Transactions.csv");
    }

    public BigDecimal valuate(String investorId, LocalDate date) {
        log.info("Valuation of {} as of {}", investorId, date);

        InvestmentsLoader.InvestorTree investorTree = investmentsLoader.getInvestorTree(investorId);
        if (investorTree == null) {
            log.warn("Investor {} not found", investorId);
            return BigDecimal.ZERO;
        }

        BigDecimal result = investmentValue(date, investorTree);

        log.info("Valuation of {} as of {} is {}", investorId, date, result);
        return result;
    }

    private BigDecimal investmentValue(LocalDate date, InvestmentsLoader.InvestorTree investmentTree) {
        BigDecimal result = BigDecimal.ZERO;
        for (InvestmentsLoader.InvestmentTree underlying : investmentTree.getUnderlyings()) {
            BigDecimal underlyingValue = switch (underlying.getInvestmentType()) {
                case Stock -> calculateStockValue(underlying, date);
                case RealEstate -> calculateRealEstate(underlying, date);
                case Fonds -> calculateFund(underlying, date);
            };

            if (underlyingValue != null) { // null means no value available
                result = result.add(underlyingValue);
            }
        }
        return result;
    }

    private BigDecimal calculateFund(InvestmentsLoader.InvestmentTree investment, LocalDate date) {
        // Retrieve basic data and if we have no data available, we early terminate
        InvestmentsLoader.InvestorTree fundTree = investmentsLoader.getInvestorTree(investment.getAssetId());
        if (fundTree == null) {
            log.warn("Fund {} has no investments", investment.getAssetId());
            return BigDecimal.ZERO;
        }
        BigDecimal value = transactionsLoader.getValue(investment.getInvestmentId(), date);
        if (value == null || value.compareTo(BigDecimal.ZERO) == 0) {
            log.warn("Investor has no investment in {}", investment.getAssetId());
            return null;
        }

        // Calculate the value of the fund
        BigDecimal fundValue = investmentValue(date, fundTree);
        if (fundValue == null) {
            log.warn("Investor's investment into fund {} has no value", investment.getAssetId());
            return null;
        }

        // Fund value is its value multiplied by total investments (divide by 100, because 100=100%)
        return value.multiply(fundValue).divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
    }

    private BigDecimal calculateRealEstate(InvestmentsLoader.InvestmentTree investment, LocalDate date) {
        // the transaction contains absolute value of the real estate, there is no adjustment defined anywhere
        return transactionsLoader.getValue(investment.getInvestmentId(), date);
    }

    private BigDecimal calculateStockValue(InvestmentsLoader.InvestmentTree investment, LocalDate date) {
        BigDecimal value = transactionsLoader.getValue(investment.getInvestmentId(), date);
        BigDecimal quote = quotesLoader.getQuote(investment.getAssetId(), date);

        if (value == null || value.compareTo(BigDecimal.ZERO) == 0) {
            log.warn("Investor has no investment in {}", investment.getAssetId());
            return null;
        }
        if (quote == null) {
            log.warn("Investor has investment without any quote in {}", investment.getAssetId());
            return null;
        }


        // number of shares multiplied by their value
        return quote.multiply(value);
    }
}
