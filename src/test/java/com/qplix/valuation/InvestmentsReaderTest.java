package com.qplix.valuation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class InvestmentsReaderTest {

    @Test
    void test() {
        InvestmentsReader investmentsReader = new InvestmentsReader();
        investmentsReader.load("/InvestmentsReaderTest/Investments.csv");

        InvestmentsReader.InvestorTree investor1 = investmentsReader.getInvestmentTree("Investor1");
        assertEquals(4, investor1.getUnderlyings().size(), "Investor1 should have 2 investments");
        assertThat(investor1.getUnderlyings())
                .as("Investment into ISIN0 expected")
                .anyMatch(i -> i.getAssetId().equals("ISIN0")
                        && i.getInvestmentId().equals("I3")
                        && i.getUnderlyings().isEmpty());
        assertThat(investor1.getUnderlyings())
                .as("Investment into Bratislava expected")
                .anyMatch(i -> i.getAssetId().equals("Bratislava")
                        && i.getInvestmentId().equals("I4")
                        && i.getUnderlyings().isEmpty());
        assertThat(investor1.getUnderlyings())
                .as("Investment into Fonds1 expected")
                .anyMatch(i -> i.getAssetId().equals("Fonds1")
                        && i.getInvestmentId().equals("I1")
                        && i.getUnderlyings().size() == 2);
        assertThat(investor1.getUnderlyings())
                .as("Investment into Fonds2 expected")
                .anyMatch(i -> i.getAssetId().equals("Fonds2")
                        && i.getInvestmentId().equals("I2")
                        && i.getUnderlyings().size() == 2);

        InvestmentsReader.InvestorTree fonds1 = investmentsReader.getInvestmentTree("Fonds1");
        assertThat(fonds1.getUnderlyings())
                .as("Investment into Fonds2 expected")
                .anyMatch(i -> i.getAssetId().equals("Fonds2")
                        && i.getInvestmentId().equals("F1")
                        && i.getUnderlyings().size() == 2);
        assertThat(fonds1.getUnderlyings())
                .as("Investment into Fonds3 expected")
                .anyMatch(i -> i.getAssetId().equals("Fonds3")
                        && i.getInvestmentId().equals("F2")
                        && i.getUnderlyings().isEmpty());

        InvestmentsReader.InvestorTree fonds2 = investmentsReader.getInvestmentTree("Fonds2");
        assertThat(fonds2.getUnderlyings())
                .as("Investment into Fonds3 expected")
                .anyMatch(i -> i.getAssetId().equals("Fonds3")
                        && i.getInvestmentId().equals("F3")
                        && i.getUnderlyings().isEmpty());
        assertThat(fonds2.getUnderlyings())
                .as("Investment into Fonds4 expected")
                .anyMatch(i -> i.getAssetId().equals("Fonds4")
                        && i.getInvestmentId().equals("F4")
                        && i.getUnderlyings().size() == 1);

        InvestmentsReader.InvestorTree fonds3 = investmentsReader.getInvestmentTree("Fonds3");
        assertThat(fonds3.getUnderlyings())
                .as("No investments expected")
                .isEmpty();

        InvestmentsReader.InvestorTree fonds4 = investmentsReader.getInvestmentTree("Fonds4");
        assertThat(fonds4.getUnderlyings())
                .as("Investment into Fonds2 expected")
                .anyMatch(i -> i.getAssetId().equals("Fonds2")
                        && i.getInvestmentId().equals("F5")
                        && i.getUnderlyings().size() == 2);

        InvestmentsReader.InvestorTree fonds5 = investmentsReader.getInvestmentTree("Fonds5");
        assertThat(fonds5.getUnderlyings())
                .as("No investments expected")
                .isEmpty();
    }
}