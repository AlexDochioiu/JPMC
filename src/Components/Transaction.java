package Components;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

/**
 * @author Alexandru Dochioiu
 * Date : 21/10/17
 */

/**
 * Class used for processing the details of a transaction
 */
public class Transaction implements ITransaction{
    /**
     * Constructor
     * @param entity the entity taking part in the transaction
     * @param direction the direction of the operation (buy/sell)
     * @param agreedFx the exchange rate for the transaction currency to US Dollars
     * @param currency the currency of the transaction
     * @param desiredSettlementDate the date when the transaction is desired to settle
     * @param units the number of units to be transacted
     * @param pricePerUnit the price per unit
     */
    public Transaction(
            String entity,
            CashflowDirection direction,
            double agreedFx,
            String currency,
            LocalDate desiredSettlementDate,
            int units,
            double pricePerUnit
    ) {
        if (entity == null) {
            throw new IllegalArgumentException("entityName");
        }
        if (direction == null) {
            throw new IllegalArgumentException("direction");
        }
        if (currency == null) {
            throw new IllegalArgumentException("currency");
        }
        if (desiredSettlementDate == null) {
            throw new IllegalArgumentException("desiredSettlementDate");
        }

        if (agreedFx <= 0 || units <= 0 || pricePerUnit <= 0) {
            // TODO: log warning or error message
        }

        entityName = entity;
        cashflowDirection = direction;
        usdValue = pricePerUnit * units * agreedFx;
        actualSettlementDate = computeActualSettlementDate(desiredSettlementDate, currency);
    }

    /**
     * Getter used for obtaining the name of the entity taking part in the transaction
     * @return the name of the entity taking part in the transaction
     */
    public String getEntityName() {
        return entityName;
    }

    /**
     * Getter used for obtaining the value of the transaction in US Dollars
     * @return the USD value of the transaction
     */
    public double getUsdValue() {
        return usdValue;
    }

    /**
     * Getter used for obtaining the direction of the cash flow
     * @return the direction of the cash flow
     */
    public CashflowDirection getCashflowDirection() {
        return cashflowDirection;
    }

    /**
     * Getter used for obtaining the actual settlement date of the transaction
     * @return the settlement date of the transaction
     */
    public LocalDate getActualSettlementDate() {
        return actualSettlementDate;
    }

    /**
     * Get the actual settlement date for a given operation
     * @param desiredDate the date when the operation is desired to settle
     * @param currency the currency of the operation
     * @return the actual settlement date of the operation
     */
    private LocalDate computeActualSettlementDate(LocalDate desiredDate, String currency)
    {
        String dayOfWeek = simpleDateformat.format(desiredDate);
        LocalDate settlementDate;

        if (sundayToThursdayCurrencies.contains(currency.toUpperCase()))
        {
            switch (dayOfWeek)
            {
                case "Fri":
                    settlementDate = desiredDate.plusDays(2);
                    break;
                case "Sat":
                    settlementDate = desiredDate.plusDays(1);
                    break;
                default:
                    settlementDate = desiredDate;
            }
        }
        else
        {
            switch (dayOfWeek)
            {
                case "Sat":
                    settlementDate = desiredDate.plusDays(2);
                    break;
                case "Sun":
                    settlementDate = desiredDate.plusDays(1);
                    break;
                default:
                    settlementDate = desiredDate;
            }
        }

        return settlementDate;
    }

    private String entityName;
    private CashflowDirection cashflowDirection;
    private LocalDate actualSettlementDate;
    private double usdValue;

    // an array containing the currencies with weekday from Sunday to Thursday
    private static List<String> sundayToThursdayCurrencies = Arrays.asList("AED", "SAR");
    // date formatter giving the day of the week
    private static DateTimeFormatter simpleDateformat = DateTimeFormatter.ofPattern("E");
}
