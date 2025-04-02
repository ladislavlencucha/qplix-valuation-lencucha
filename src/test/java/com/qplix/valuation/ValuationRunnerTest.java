package com.qplix.valuation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ValuationRunnerTest {

    private ValuationRunner valuationRunner;
    private QuotesLoader quotesLoader;
    private InvestmentsLoader investmentsLoader;
    private TransactionsLoader transactionsLoader;

    @BeforeEach
    void setUp() {
        quotesLoader = mock(QuotesLoader.class);
        investmentsLoader = mock(InvestmentsLoader.class);
        transactionsLoader = mock(TransactionsLoader.class);
        valuationRunner = new ValuationRunner(quotesLoader, investmentsLoader, transactionsLoader);
    }

    @Test
    void valuateInvestorWithNoInvestments() {
        when(investmentsLoader.getInvestorTree("INV123")).thenReturn(null);

        BigDecimal result = valuationRunner.valuate("INV123", LocalDate.of(2023, 1, 1));

        Assertions.assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void valuateInvestorWithStockInvestment() {
        InvestmentsLoader.InvestorTree investorTree = mock(InvestmentsLoader.InvestorTree.class);
        InvestmentsLoader.InvestmentTree investmentTree = mock(InvestmentsLoader.InvestmentTree.class);

        when(investmentsLoader.getInvestorTree("INV123")).thenReturn(investorTree);
        when(investorTree.getUnderlyings()).thenReturn(List.of(investmentTree));
        when(investmentTree.getInvestmentType()).thenReturn(InvestmentsLoader.InvestmentType.Stock);
        when(investmentTree.getAssetId()).thenReturn("STOCK123");
        when(investmentTree.getInvestmentId()).thenReturn("INVEST123");
        when(transactionsLoader.getValue("INVEST123", LocalDate.of(2023, 1, 1))).thenReturn(new BigDecimal("10"));
        when(quotesLoader.getQuote("STOCK123", LocalDate.of(2023, 1, 1))).thenReturn(new BigDecimal("100"));

        BigDecimal result = valuationRunner.valuate("INV123", LocalDate.of(2023, 1, 1));

        assertEquals(new BigDecimal("1000"), result);
    }

    @Test
    void valuateInvestorWithStockInvestmentWithoutQuote() {
        InvestmentsLoader.InvestorTree investorTree = mock(InvestmentsLoader.InvestorTree.class);
        InvestmentsLoader.InvestmentTree investmentTree = mock(InvestmentsLoader.InvestmentTree.class);

        when(investmentsLoader.getInvestorTree("INV123")).thenReturn(investorTree);
        when(investorTree.getUnderlyings()).thenReturn(List.of(investmentTree));
        when(investmentTree.getInvestmentType()).thenReturn(InvestmentsLoader.InvestmentType.Stock);
        when(investmentTree.getAssetId()).thenReturn("STOCK123");
        when(investmentTree.getInvestmentId()).thenReturn("INVEST123");
        when(transactionsLoader.getValue("INVEST123", LocalDate.of(2023, 1, 1))).thenReturn(new BigDecimal("10"));
        when(quotesLoader.getQuote("STOCK123", LocalDate.of(2023, 1, 1))).thenReturn(nullable(BigDecimal.class));

        BigDecimal result = valuationRunner.valuate("INV123", LocalDate.of(2023, 1, 1));

        Assertions.assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void valuateInvestorWithStockInvestmentWithoutAmount() {
        InvestmentsLoader.InvestorTree investorTree = mock(InvestmentsLoader.InvestorTree.class);
        InvestmentsLoader.InvestmentTree investmentTree = mock(InvestmentsLoader.InvestmentTree.class);

        when(investmentsLoader.getInvestorTree("INV123")).thenReturn(investorTree);
        when(investorTree.getUnderlyings()).thenReturn(List.of(investmentTree));
        when(investmentTree.getInvestmentType()).thenReturn(InvestmentsLoader.InvestmentType.Stock);
        when(investmentTree.getAssetId()).thenReturn("STOCK123");
        when(investmentTree.getInvestmentId()).thenReturn("INVEST123");
        when(quotesLoader.getQuote(eq("STOCK123"), eq(LocalDate.of(2023, 1, 1)))).thenReturn(new BigDecimal("100"));
        when(transactionsLoader.getValue(eq("INVEST123"), eq(LocalDate.of(2023, 1, 1)))).thenReturn(nullable(BigDecimal.class));

        BigDecimal result = valuationRunner.valuate("INV123", LocalDate.of(2023, 1, 1));

        Assertions.assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void valuateInvestorWithRealEstateInvestment() {
        InvestmentsLoader.InvestorTree investorTree = mock(InvestmentsLoader.InvestorTree.class);
        InvestmentsLoader.InvestmentTree investmentTree = mock(InvestmentsLoader.InvestmentTree.class);

        when(investmentsLoader.getInvestorTree("INV123")).thenReturn(investorTree);
        when(investorTree.getUnderlyings()).thenReturn(List.of(investmentTree));
        when(investmentTree.getInvestmentType()).thenReturn(InvestmentsLoader.InvestmentType.RealEstate);
        when(investmentTree.getInvestmentId()).thenReturn("INVEST123");
        when(transactionsLoader.getValue("INVEST123", LocalDate.of(2023, 1, 1))).thenReturn(new BigDecimal("500000"));

        BigDecimal result = valuationRunner.valuate("INV123", LocalDate.of(2023, 1, 1));

        assertEquals(new BigDecimal("500000"), result);
    }

    @Test
    void valuateInvestorWithRealEstateInvestmentWithoutValue() {
        InvestmentsLoader.InvestorTree investorTree = mock(InvestmentsLoader.InvestorTree.class);
        InvestmentsLoader.InvestmentTree investmentTree = mock(InvestmentsLoader.InvestmentTree.class);

        when(investmentsLoader.getInvestorTree("INV123")).thenReturn(investorTree);
        when(investorTree.getUnderlyings()).thenReturn(List.of(investmentTree));
        when(investmentTree.getInvestmentType()).thenReturn(InvestmentsLoader.InvestmentType.RealEstate);
        when(investmentTree.getInvestmentId()).thenReturn("INVEST123");
        when(transactionsLoader.getValue("INVEST123", LocalDate.of(2023, 1, 1))).thenReturn(nullable(BigDecimal.class));

        BigDecimal result = valuationRunner.valuate("INV123", LocalDate.of(2023, 1, 1));

        Assertions.assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void valuateInvestorWithFundInvestmentHaving1Stock() {

        // we have Investor INV123 which invests into Fund FUND123 via INVEST123
        InvestmentsLoader.InvestorTree investorTree = mock(InvestmentsLoader.InvestorTree.class);
        InvestmentsLoader.InvestmentTree investorInvestmentTree = mock(InvestmentsLoader.InvestmentTree.class);
        when(investmentsLoader.getInvestorTree("INV123")).thenReturn(investorTree);
        when(investorTree.getUnderlyings()).thenReturn(List.of(investorInvestmentTree));
        when(investorInvestmentTree.getInvestmentType()).thenReturn(InvestmentsLoader.InvestmentType.Fonds);
        when(investorInvestmentTree.getAssetId()).thenReturn("FUND123");
        when(investorInvestmentTree.getInvestmentId()).thenReturn("INVEST123");
        when(transactionsLoader.getValue("INVEST123", LocalDate.of(2023, 1, 1))).thenReturn(new BigDecimal("0.5"));

        // we have fund FUND123 which invests into ISIN0 via INVEST456
        InvestmentsLoader.InvestorTree fundTree = mock(InvestmentsLoader.InvestorTree.class);
        InvestmentsLoader.InvestmentTree fundInvestmentTree = mock(InvestmentsLoader.InvestmentTree.class);
        when(investmentsLoader.getInvestorTree("FUND123")).thenReturn(fundTree);
        when(fundTree.getUnderlyings()).thenReturn(List.of(fundInvestmentTree));
        when(fundInvestmentTree.getInvestmentType()).thenReturn(InvestmentsLoader.InvestmentType.Stock);
        when(fundInvestmentTree.getAssetId()).thenReturn("ISIN0");
        when(fundInvestmentTree.getInvestmentId()).thenReturn("INVEST456");
        when(transactionsLoader.getValue("INVEST456", LocalDate.of(2023, 1, 1)))
                .thenReturn(new BigDecimal("100"));
        when(quotesLoader.getQuote("ISIN0", LocalDate.of(2023, 1, 1)))
                .thenReturn(new BigDecimal("1000"));

        BigDecimal result = valuationRunner.valuate("INV123", LocalDate.of(2023, 1, 1));

        assertEquals(new BigDecimal("500.0"), result,
                "We have 0.5% in a Fund that consists of 100 Shares each worth 1000 of some ccy");
    }

    @Test
    void valuateInvestorWithFundInvestmentHaving1StockWithoutQuote() {

        // we have Investor INV123 which invests into Fund FUND123 via INVEST123
        InvestmentsLoader.InvestorTree investorTree = mock(InvestmentsLoader.InvestorTree.class);
        InvestmentsLoader.InvestmentTree investorInvestmentTree = mock(InvestmentsLoader.InvestmentTree.class);
        when(investmentsLoader.getInvestorTree("INV123")).thenReturn(investorTree);
        when(investorTree.getUnderlyings()).thenReturn(List.of(investorInvestmentTree));
        when(investorInvestmentTree.getInvestmentType()).thenReturn(InvestmentsLoader.InvestmentType.Fonds);
        when(investorInvestmentTree.getAssetId()).thenReturn("FUND123");
        when(investorInvestmentTree.getInvestmentId()).thenReturn("INVEST123");
        when(transactionsLoader.getValue("INVEST123", LocalDate.of(2023, 1, 1))).thenReturn(new BigDecimal("0.5"));

        // we have fund FUND123 which invests into ISIN0 via INVEST456
        InvestmentsLoader.InvestorTree fundTree = mock(InvestmentsLoader.InvestorTree.class);
        InvestmentsLoader.InvestmentTree fundInvestmentTree = mock(InvestmentsLoader.InvestmentTree.class);
        when(investmentsLoader.getInvestorTree("FUND123")).thenReturn(fundTree);
        when(fundTree.getUnderlyings()).thenReturn(List.of(fundInvestmentTree));
        when(fundInvestmentTree.getInvestmentType()).thenReturn(InvestmentsLoader.InvestmentType.Stock);
        when(fundInvestmentTree.getAssetId()).thenReturn("ISIN0");
        when(fundInvestmentTree.getInvestmentId()).thenReturn("INVEST456");
        when(transactionsLoader.getValue("INVEST456", LocalDate.of(2023, 1, 1)))
                .thenReturn(new BigDecimal("100"));
        when(quotesLoader.getQuote("ISIN0", LocalDate.of(2023, 1, 1)))
                .thenReturn(nullable(BigDecimal.class));

        BigDecimal result = valuationRunner.valuate("INV123", LocalDate.of(2023, 1, 1));

        Assertions.assertThat(result)
                .as("We have no quote for stock, which leads to no value of a fund, which leads to 0 value of holdings")
                .isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void valuateInvestorWithFundInvestmentHavingAnotherFundWith1Stock() {

        // we have Investor INV123 which invests into Fund FUND123 via INVEST123
        InvestmentsLoader.InvestorTree investorTree = mock(InvestmentsLoader.InvestorTree.class);
        InvestmentsLoader.InvestmentTree investorInvestmentTree = mock(InvestmentsLoader.InvestmentTree.class);
        when(investmentsLoader.getInvestorTree("INV123")).thenReturn(investorTree);
        when(investorTree.getUnderlyings()).thenReturn(List.of(investorInvestmentTree));
        when(investorInvestmentTree.getInvestmentType()).thenReturn(InvestmentsLoader.InvestmentType.Fonds);
        when(investorInvestmentTree.getAssetId()).thenReturn("FUND123");
        when(investorInvestmentTree.getInvestmentId()).thenReturn("INVEST123");
        when(transactionsLoader.getValue("INVEST123", LocalDate.of(2023, 1, 1)))
                .thenReturn(new BigDecimal("0.5"));

        // we have fund FUND123 which invests into FUND456 via INVEST456
        InvestmentsLoader.InvestorTree fundTree = mock(InvestmentsLoader.InvestorTree.class);
        InvestmentsLoader.InvestmentTree fundInvestmentTree = mock(InvestmentsLoader.InvestmentTree.class);
        when(investmentsLoader.getInvestorTree("FUND123")).thenReturn(fundTree);
        when(fundTree.getUnderlyings()).thenReturn(List.of(fundInvestmentTree));
        when(fundInvestmentTree.getInvestmentType()).thenReturn(InvestmentsLoader.InvestmentType.Fonds);
        when(fundInvestmentTree.getAssetId()).thenReturn("FUND456");
        when(fundInvestmentTree.getInvestmentId()).thenReturn("INVEST456");
        when(transactionsLoader.getValue("INVEST456", LocalDate.of(2023, 1, 1)))
                .thenReturn(new BigDecimal("0.2"));

        // we have fund FUND456 which invests into ISIN0 via INVEST789
        InvestmentsLoader.InvestorTree nestedFundTree = mock(InvestmentsLoader.InvestorTree.class);
        InvestmentsLoader.InvestmentTree nestedFundInvestmentTree = mock(InvestmentsLoader.InvestmentTree.class);
        when(investmentsLoader.getInvestorTree("FUND456")).thenReturn(nestedFundTree);
        when(nestedFundTree.getUnderlyings()).thenReturn(List.of(nestedFundInvestmentTree));
        when(nestedFundInvestmentTree.getInvestmentType()).thenReturn(InvestmentsLoader.InvestmentType.Stock);
        when(nestedFundInvestmentTree.getAssetId()).thenReturn("ISIN0");
        when(nestedFundInvestmentTree.getInvestmentId()).thenReturn("INVEST789");
        when(transactionsLoader.getValue("INVEST789", LocalDate.of(2023, 1, 1)))
                .thenReturn(new BigDecimal("100"));
        when(quotesLoader.getQuote("ISIN0", LocalDate.of(2023, 1, 1)))
                .thenReturn(new BigDecimal("1000"));

        BigDecimal result = valuationRunner.valuate("INV123", LocalDate.of(2023, 1, 1));

        assertEquals(new BigDecimal("1.00"), result,
                "We have 0.5% in a Fund that consists of 0.2% Share in another Fund having 100 Shares each worth 1000 of some ccy");
    }

    @Test
    void valuateInvestorWithFundInvestmentHavingAnotherFundWith1StockWithoutQuote() {

        // we have Investor INV123 which invests into Fund FUND123 via INVEST123
        InvestmentsLoader.InvestorTree investorTree = mock(InvestmentsLoader.InvestorTree.class);
        InvestmentsLoader.InvestmentTree investorInvestmentTree = mock(InvestmentsLoader.InvestmentTree.class);
        when(investmentsLoader.getInvestorTree("INV123")).thenReturn(investorTree);
        when(investorTree.getUnderlyings()).thenReturn(List.of(investorInvestmentTree));
        when(investorInvestmentTree.getInvestmentType()).thenReturn(InvestmentsLoader.InvestmentType.Fonds);
        when(investorInvestmentTree.getAssetId()).thenReturn("FUND123");
        when(investorInvestmentTree.getInvestmentId()).thenReturn("INVEST123");
        when(transactionsLoader.getValue("INVEST123", LocalDate.of(2023, 1, 1)))
                .thenReturn(new BigDecimal("0.5"));

        // we have fund FUND123 which invests into FUND456 via INVEST456
        InvestmentsLoader.InvestorTree fundTree = mock(InvestmentsLoader.InvestorTree.class);
        InvestmentsLoader.InvestmentTree fundInvestmentTree = mock(InvestmentsLoader.InvestmentTree.class);
        when(investmentsLoader.getInvestorTree("FUND123")).thenReturn(fundTree);
        when(fundTree.getUnderlyings()).thenReturn(List.of(fundInvestmentTree));
        when(fundInvestmentTree.getInvestmentType()).thenReturn(InvestmentsLoader.InvestmentType.Fonds);
        when(fundInvestmentTree.getAssetId()).thenReturn("FUND456");
        when(fundInvestmentTree.getInvestmentId()).thenReturn("INVEST456");
        when(transactionsLoader.getValue("INVEST456", LocalDate.of(2023, 1, 1)))
                .thenReturn(new BigDecimal("0.2"));

        // we have fund FUND456 which invests into ISIN0 via INVEST789
        InvestmentsLoader.InvestorTree nestedFundTree = mock(InvestmentsLoader.InvestorTree.class);
        InvestmentsLoader.InvestmentTree nestedFundInvestmentTree = mock(InvestmentsLoader.InvestmentTree.class);
        when(investmentsLoader.getInvestorTree("FUND456")).thenReturn(nestedFundTree);
        when(nestedFundTree.getUnderlyings()).thenReturn(List.of(nestedFundInvestmentTree));
        when(nestedFundInvestmentTree.getInvestmentType()).thenReturn(InvestmentsLoader.InvestmentType.Stock);
        when(nestedFundInvestmentTree.getAssetId()).thenReturn("ISIN0");
        when(nestedFundInvestmentTree.getInvestmentId()).thenReturn("INVEST789");
        when(transactionsLoader.getValue("INVEST789", LocalDate.of(2023, 1, 1)))
                .thenReturn(new BigDecimal("100"));
        when(quotesLoader.getQuote("ISIN0", LocalDate.of(2023, 1, 1)))
                .thenReturn(nullable(BigDecimal.class));

        BigDecimal result = valuationRunner.valuate("INV123", LocalDate.of(2023, 1, 1));

        Assertions.assertThat(result)
                .as("The quote is missing, which leads to both funds having null value, which leads to ZERO valuation")
                .isEqualByComparingTo(BigDecimal.ZERO);
    }
}