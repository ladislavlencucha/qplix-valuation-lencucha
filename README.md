This is a simple demonstration of valuation calculator.

### Assumptions are made

The following assumptions were made in order to proceed with the development:
* investmentId is unique in Investments.csv and represents identification of who buys what
* Transactions.csv contains all transactions for the investmentIds present in Investments.csv
* Reading file is considered a slow operation, thus loading into memory is preferred to have fast results.
  The code can be rewritten to load data repetitively (e.g. Transactions.csv) if needed to save memory.
  The Investments.csv sadly have no way of speeding up the process, because we cannot eliminate many rows (Fund might contain another Fund)
  and the data sample clearly shows the majority of records are Funds.
* The valuation is performed with exclusive date range, meaning 2025-03-25 valuation date includes transactions
  until midnight of 2025-03-24 and also the quote as of 2025-03-24.
* No special rounding handling is being done, simple multiplication and division is used.
* Buying a Fund means buying percentage in it
* Fund's value is estimated as total number of transactions made by the Fund
* Percentage <0, 100%> is represented as <0, 100>

### What is not implemented: 
* Further optimizations include pre-summing transaction values not to sum again from scratch
  every time. This is a simple example demonstrating how to approach validation and various cases
* Valuation does not check for cycles

### Some business insights
* Having no liquidity/currency inflows/outflows make the whole thing strange (but simple)
* We have no currency, thus everything is in some virtual currency and no FX conversion is being done
* Fund investment is not based on units, but based on percentage bought, which is simplification to make the demo easier
