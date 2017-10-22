package Helpers;

import Components.ITransaction.CashflowDirection;
import Components.Transaction;
import Components.ITransaction;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author Alexandru Dochioiu
 * Date : 21/10/17
 */
public final class ParsingUtil {
    /**
     * Static method used for parsing a string line into a transaction
     * @param line the line to be parsed
     * @return the Transaction parsed from the line or null if the line could not be parsed
     */
    public static ITransaction lineToTransaction(String line) {
        if (line == null) {
            throw new IllegalArgumentException("line");
        }

        String[] transactionParams = line.split(",");

        // Is the input length fine?
        if (transactionParams.length != 8) {
            return null;
        }

        try {
            String entity = transactionParams[0];
            CashflowDirection direction = getCashflowDirection(transactionParams[1]);
            double agreedFx = Double.parseDouble(transactionParams[2]);
            LocalDate desiredSettlementDate = LocalDate.parse(transactionParams[5], dateFormat);
            int units = Integer.parseInt(transactionParams[6]);
            double pricePerUnit = Double.parseDouble(transactionParams[7]);

            return new Transaction(
                    entity,
                    direction,
                    agreedFx,
                    transactionParams[3],
                    desiredSettlementDate,
                    units,
                    pricePerUnit
            );
        }
        catch (Exception e) {
            return null;
        }
    }

    /**
     * Method used to compute the direction of the cashflow from the operation type (buy/sell)
     * @param operationType string containing the operation type
     * @return the cashflow direction
     */
    private static CashflowDirection getCashflowDirection(String operationType) {
        if (operationType.equals("S")) {
            return CashflowDirection.Incoming;
        }
        else {
            if (operationType.equals("B")) {
                return CashflowDirection.Outgoing;
            }
            else {
                return null;
            }
        }
    }

    public static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd MMM yyyy");
}
