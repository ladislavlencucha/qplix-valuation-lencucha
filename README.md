This is a simple demonstration of valuation calculator.

### The following assumptions are made:
* investmentId is unique in Investments.csv and represents identification of who buys what
* Transactions.csv contains all transactions for the investmentIds present in Investments.csv
* Reading file is considered a slow operation, thus loading into memory is preferred to have fast results.
  The code can be rewritten to load data repetitively (e.g. Transactions.csv) if needed to save memory.
  The Investments.csv sadly have no way of speeding up the process, because we cannot eliminate many rows (Fund might contain another Fund)
  and the data sample clearly shows the majority of records are Funds.

### What is not implemented: 
* Further optimizations include pre-summing transaction values not to sum again from scratch
  every time. This is a simple example demonstrating how to approach validation and various cases 