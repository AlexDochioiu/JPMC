# Daily Trade Reporting Engine

Simple engine taking a set of instructions and generating:
* Daily report including all the transactions
* Ranking of entities based on total cashflow inflow *or* outflow

##### Daily Report - Notes

* The output is reverse chronologically ordered
* The cashflow for a given transaction is considered to be in the day when it actually setteled (*not on the desired settlement date*)
* The reported amount is in US Dollars

##### Ranking of Entities - Notes

* The total cash inflow/outflow for an entity is accounting all the transactions in which that particular entity appears
* The reported amount is in US Dollars

## Input Data

The input data is a string following the csv format, with each individual line being separated by the "\n" character

The input data contains the following:
* _Entity_: A financial entity whose shares are to be bought or sold
* _Operation Type_: Buy(Cash Outflow) or Sell(Cash Inflow)
* _Agreed Forex Rate_: The foreign exchange rate with respect to USD that was agreed
* _Currency_: The currency in which the shares are trading
* _Instruction Date_: Date on which the instruction was sent to JP Morgan by various clients
* _Desired Settlement Date_: Date on which the client wished for the instruction to be settled
* _Units_: Number of shares to be transacted
* _Price per Unit_: The price of a single share (in the previously defined _Currency_)

_Example of input data for one transaction_: `foo,B,0.50,SGP,01 Jan 2016,02 Jan 2016,200,100.25\n`

## Requirements

* USD value of a transaction is calculated by `USD = AgreedFxRate * Unites * PricePerUnit`
* Transactions can settle only on workdays.

##### Transaction Settlement - Notes
* Holidays are not excluded from the working days.
* If a transaction's desired settlement date does not fall on a business day, it's actual settlement date is on the first workday *after* the desired date.
* The working days are either from Monday to Friday *or* Saturday to Thursday based on the currency of the transaction.