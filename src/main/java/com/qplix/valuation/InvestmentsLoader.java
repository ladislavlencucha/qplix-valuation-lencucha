package com.qplix.valuation;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

@Slf4j
public class InvestmentsLoader {

    /**
     * Represents raw investments read from the file indexed by investorId.
     */
    private Map<String, List<Investment>> investmentsByInvestorId = Collections.emptyMap();
    /**
     * Represents investments tree by given investor (cached for repetitive calls to optimize Funds tree hierarchy retrieval).
     */
    private Map<String, InvestorTree> fundInvestmentTreesByFondsInvestor = new HashMap<>();

    /**
     * Load all investments into memory for easy repetitive access.
     *
     * @param fileName file name to load
     */
    public void load(String fileName) {
        log.info("Loading Investments");

        // load investments
        List<Investment> investments = loadInvestments(fileName);

        // index them for faster repetitive access by investor
        investmentsByInvestorId = indexInvestments(investments);

        // clear the cached investment trees
        fundInvestmentTreesByFondsInvestor = new HashMap<>();

        log.info("Loaded {} Investments for {} investors", investments.size(), investmentsByInvestorId.size());
    }

    public InvestorTree getInvestmentTree(String investorId) {
        // filter investments by investorId and index by investmentId
        InvestorTree result = new InvestorTree(investorId, new ArrayList<>());
        filterInvestments(investorId, result, fundInvestmentTreesByFondsInvestor);

        return result;
    }

    private void filterInvestments(
            String investorId, InvestorTree target, Map<String, InvestorTree> fundInvestmentTreesByFondsInvestor) {
        List<Investment> requiredInvestorInvestments = investmentsByInvestorId.get(investorId);
        if (requiredInvestorInvestments == null) {
            return; // no investments for this investor exist
        }

        for (Investment ii : requiredInvestorInvestments) {
            if (ii.investmentType() == InvestmentType.Fonds) {
                addFund(target, fundInvestmentTreesByFondsInvestor, ii);
            } else {
                addNonFund(target, ii);
            }
        }
    }

    private void addFund(InvestorTree target, Map<String, InvestorTree> fundInvestmentTreesByFondsInvestor, Investment ii) {
        // find existing investments of given investor
        InvestorTree childTarget;
        boolean isNew;
        InvestorTree childTargetTemp = fundInvestmentTreesByFondsInvestor.get(ii.getAssetId());
        if (childTargetTemp == null) {
            isNew = true;
            childTarget = new InvestorTree(ii.getAssetId(), new ArrayList<>());
            fundInvestmentTreesByFondsInvestor.put(ii.getAssetId(), childTarget);
        } else {
            isNew = false;
            childTarget = childTargetTemp;
        }

        // add the investment to the result and ensure the investors are properly cycled via shared collections
        target.underlyings.add(
                new InvestmentTree(
                        ii.investmentId, ii.investmentType, ii.getAssetId(), childTarget.underlyings));

        // now recursively go through all fonds investments and add their investments if we have not yet done it
        if (isNew) {
            filterInvestments(ii.fondsInvestor(), childTarget, fundInvestmentTreesByFondsInvestor);
        }
    }

    private void addNonFund(InvestorTree target, Investment ii) {
        // stocks and real estates have no underlyings
        target.underlyings.add(
                new InvestmentTree(
                        ii.investmentId, ii.investmentType(), ii.getAssetId(), Collections.emptyList()));
    }

    private static Map<String, List<Investment>> indexInvestments(List<Investment> investments) {
        // index investments by investorId and investmentId
        Map<String, List<Investment>> indexedInvestorInvestments = new HashMap<>();
        for (Investment ii : investments) {
            indexedInvestorInvestments.computeIfAbsent(
                            ii.investorId,
                            k -> new ArrayList<>())
                    .add(ii);
        }

        return indexedInvestorInvestments;
    }

    private List<Investment> loadInvestments(String fileName) {
        List<Investment> investments = new ArrayList<>();

        try (Reader reader = ResourceReaderFactory.resourceFileReader(fileName);
             CSVParser csvParser = ResourceReaderFactory.csvParser(reader)) {
            for (CSVRecord csvRecord : csvParser) {
                String investorId = csvRecord.get("InvestorId");
                validateStringValue(investorId, "Investor ID is missing");

                String investmentTypeString = csvRecord.get("InvestmentType");
                validateStringValue(investmentTypeString, "Investment type is missing");
                InvestmentType investmentType = InvestmentType.valueOf(investmentTypeString);

                String investmentId = csvRecord.get("InvestmentId");
                validateStringValue(investmentId, "Investment ID is missing");

                String isin = csvRecord.get("ISIN");
                String city = csvRecord.get("City");
                String fondsInvestor = csvRecord.get("FondsInvestor");
                validateInvestment(isin, city, fondsInvestor, investmentType);

                investments.add(new Investment(
                        investorId,
                        investmentId,
                        investmentType,
                        isin,
                        city,
                        fondsInvestor));
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to load investments from file " + fileName, e);
        }

        return investments;
    }

    private static void validateStringValue(String stringValue, String exceptionMessage) {
        if (stringValue == null || stringValue.isBlank()) {
            throw new IllegalArgumentException(exceptionMessage);
        }
    }

    private static void validateInvestment(String isin, String city, String fondsInvestor, InvestmentType investmentType) {
        switch (investmentType) {
            case Stock -> Objects.requireNonNull(isin, "ISIN is missing");
            case RealEstate -> Objects.requireNonNull(city, "City is missing");
            case Fonds -> Objects.requireNonNull(fondsInvestor, "Fonds Investor is missing");
        }
    }

    /**
     * Represents investment tree hierarchy.
     */
    @AllArgsConstructor
    public static final class InvestmentTree {

        /**
         * The identifier of the investment
         */
        @Getter
        private final String investmentId;
        /**
         * The type of investment
         */
        @Getter
        private final InvestmentType investmentType;
        /**
         * The identifier of the asset (fund ID, stock ID, real estate ID)
         */
        @Getter
        private final String assetId;

        private final List<InvestmentTree> underlyings;

        /**
         * The underlyings of given investment (if applicable)
         *
         * @return the list of underlyings
         */
        public List<InvestmentTree> getUnderlyings() {
            return Collections.unmodifiableList(underlyings); // protect leaking of modifiable collection
        }

        @Override
        public String toString() {
            return "Investments into " + assetId;
        }

    }

    /**
     * Represents investment tree hierarchy by investor.
     */
    @AllArgsConstructor
    public static final class InvestorTree {

        /**
         * The identifier of the investor
         */
        @Getter
        private final String investorId;

        private final List<InvestmentTree> underlyings;

        /**
         * The underlyings of given investment (if applicable).
         */
        public List<InvestmentTree> getUnderlyings() {
            return Collections.unmodifiableList(underlyings); // protect leaking of modifiable collection
        }

        @Override
        public String toString() {
            return "Investments of investor " + investorId;
        }
    }

    /**
     * Represents raw investment from CSV file.
     *
     * @param investorId     Investor ID (either an investor or a fond investor).
     * @param investmentId   Investment ID (identifier of an operation that can be done multiple times).
     * @param investmentType Investment type (Fonds, Stock, RealEstate).
     * @param isin           ISIN of security bought (if investment is into {@link InvestmentType#Stock}).
     * @param city           City of estate bought (if investment is into {@link InvestmentType#RealEstate}).
     * @param fondsInvestor  Investor ID of fund bought (if investment is into {@link InvestmentType#Fonds}).
     */
    public record Investment(String investorId, String investmentId, InvestmentType investmentType,
                             String isin, String city, String fondsInvestor) {

        public String getAssetId() {
            return switch (investmentType) {
                case Stock -> isin;
                case RealEstate -> city;
                case Fonds -> fondsInvestor;
            };
        }
    }

    /**
     * Type of investment.
     */
    public enum InvestmentType {
        Fonds,
        Stock,
        RealEstate
    }
}
