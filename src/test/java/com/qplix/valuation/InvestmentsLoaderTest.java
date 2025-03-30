package com.qplix.valuation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class InvestmentsLoaderTest {

    @Test
    void test() {
        InvestmentsLoader investmentsLoader = new InvestmentsLoader();
        investmentsLoader.load("/InvestmentsLoaderTest/Investments.csv");

        InvestmentsLoader.InvestorTree investor1 = investmentsLoader.getInvestorTree("Investor1");
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

        InvestmentsLoader.InvestorTree fonds1 = investmentsLoader.getInvestorTree("Fonds1");
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

        InvestmentsLoader.InvestorTree fonds2 = investmentsLoader.getInvestorTree("Fonds2");
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

        InvestmentsLoader.InvestorTree fonds3 = investmentsLoader.getInvestorTree("Fonds3");
        assertThat(fonds3.getUnderlyings())
                .as("No investments expected")
                .isEmpty();

        InvestmentsLoader.InvestorTree fonds4 = investmentsLoader.getInvestorTree("Fonds4");
        assertThat(fonds4.getUnderlyings())
                .as("Investment into Fonds2 expected")
                .anyMatch(i -> i.getAssetId().equals("Fonds2")
                        && i.getInvestmentId().equals("F5")
                        && i.getUnderlyings().size() == 2);

        InvestmentsLoader.InvestorTree fonds5 = investmentsLoader.getInvestorTree("Fonds5");
        assertThat(fonds5.getUnderlyings())
                .as("No investments expected")
                .isEmpty();
    }
}