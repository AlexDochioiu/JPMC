package Components;

import Components.ITransaction.CashflowDirection;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

/**
 * @author Alexandru Dochioiu
 * Date : 21/10/17
 */
public class TransactionTest {
    private String entityName;
    private CashflowDirection direction;
    private double agreedFx;
    private String currency;
    private LocalDate desiredSettlementDate;
    private int units;
    private double pricePerUnit;

    @Before
    public void setUp() {
        entityName = "default_entity_name";
        direction = CashflowDirection.Incoming;
        agreedFx = 0.26;
        currency = "RON";
        desiredSettlementDate = LocalDate.of(2017, 10, 20);
        units = 100;
        pricePerUnit = 2;
    }

    /**
     * Tests that a null entityName will cause the constructor to throw
     */
    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsForNullEntity() {
        entityName = null;
        getTransaction();
    }

    /**
     * Tests that a null cashflow direction will cause the constructor to throw
     */
    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsForNullDirection() {
        direction = null;
        getTransaction();
    }

    /**
     * Tests that a null currency string will cause the constructor to throw
     */
    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsForNullCurrency() {
        currency = null;
        getTransaction();
    }

    /**
     * Tests that a null desired settlement date will cause the constructor to throw
     */
    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsForNullDesiredSettlementDate() {
        desiredSettlementDate = null;
        getTransaction();
    }

    /**
     * Tests that the transaction registers and returns the correct entity name
     */
    @Test
    public void getEntityNameWorksProperly() {
        Transaction transaction = getTransaction();
        Assert.assertEquals(
                "The transaction does not return the expected entity name",
                entityName,
                transaction.getEntityName()
        );
    }

    /**
     * Tests that the transaction calculates the value in US Dollars properly
     */
    @Test
    public void getUsdValueWorksProperly() {
        agreedFx = 0.3;
        units = 10;
        pricePerUnit = 20;

        double expectedUsdValue = agreedFx * units * pricePerUnit;
        Transaction transaction = getTransaction();
        Assert.assertEquals(
                "The returned USD value of the transaction does not match the expected one",
                expectedUsdValue,
                transaction.getUsdValue(),
                1e-6
        );
    }

    /**
     * Tests that the transaction returns the proper cashflow direction
     */
    @Test
    public void getCashflowDirectionWorksProperly() {
        direction = CashflowDirection.Incoming;

        Transaction transaction = getTransaction();
        Assert.assertEquals(
                "The returned cashflow direction does not match the expected one",
                direction,
                transaction.getCashflowDirection()
        );

        direction = CashflowDirection.Outgoing;

        transaction = getTransaction();
        Assert.assertEquals(
                "The returned cashflow direction does not match the expected one",
                direction,
                transaction.getCashflowDirection()
        );
    }

    /**
     * Tests that the actual settlement date of a transaction is computed properly for
     * a currency where the weekday starts on Monday and finishes on Friday
     */
    @Test
    public void getSettlementDateWorksProperlyForCurrenciesWithMondayToFridayWeekdays() {
        // desired settlement date is on a Wednesday
        desiredSettlementDate = LocalDate.of(2017, 10, 25);
        currency = "SGP";

        Transaction transaction = getTransaction();

        Assert.assertEquals(
                "The returned settlement date should have been the same as the desired one",
                desiredSettlementDate,
                transaction.getActualSettlementDate()
        );

        // on a Friday
        desiredSettlementDate = LocalDate.of(2017, 10, 27);
        transaction = getTransaction();

        Assert.assertEquals(
                "The returned settlement date should have been the same as the desired one",
                desiredSettlementDate,
                transaction.getActualSettlementDate()
        );

        // on a Saturday
        desiredSettlementDate = LocalDate.of(2017, 10, 28);
        transaction = getTransaction();

        LocalDate expectedActualSettlementDate = LocalDate.of(2017, 10, 30);
        Assert.assertEquals(
                "The returned settlement date is on weekend so the actual settlement date should be the next Monday",
                expectedActualSettlementDate,
                transaction.getActualSettlementDate()
        );

        // on a Sunday
        desiredSettlementDate = LocalDate.of(2017, 10, 29);
        transaction = getTransaction();

        expectedActualSettlementDate = LocalDate.of(2017, 10, 30);
        Assert.assertEquals(
                "The returned settlement date is on weekend so the actual settlement date should be the next Monday",
                expectedActualSettlementDate,
                transaction.getActualSettlementDate()
        );
    }

    /**
     * Tests that the actual settlement date of a transaction is computed properly for
     * a currency where the weekday starts on Sunday and finishes on Thursday
     */
    @Test
    public void getSettlementDateWorksProperlyForCurrenciesWithSundayToThursdayWeekdays() {
        // desired settlement date is on a Wednesday
        desiredSettlementDate = LocalDate.of(2017, 10, 25);
        currency = "AED";

        Transaction transaction = getTransaction();

        Assert.assertEquals(
                "The returned settlement date should have been the same as the desired one",
                desiredSettlementDate,
                transaction.getActualSettlementDate()
        );

        // on a Friday
        desiredSettlementDate = LocalDate.of(2017, 10, 27);
        transaction = getTransaction();

        LocalDate expectedActualSettlementDate = LocalDate.of(2017, 10, 29);
        Assert.assertEquals(
                "The returned settlement date is not on workday so the actual settlement date should be the next Sunday",
                expectedActualSettlementDate,
                transaction.getActualSettlementDate()
        );

        // on a Saturday
        desiredSettlementDate = LocalDate.of(2017, 10, 28);
        transaction = getTransaction();

        expectedActualSettlementDate = LocalDate.of(2017, 10, 29);
        Assert.assertEquals(
                "The returned settlement date is not on workday so the actual settlement date should be the next Sunday",
                expectedActualSettlementDate,
                transaction.getActualSettlementDate()
        );

        // on a Sunday
        desiredSettlementDate = LocalDate.of(2017, 10, 29);
        transaction = getTransaction();

        Assert.assertEquals(
                "The returned settlement date should have been the same as the desired one",
                desiredSettlementDate,
                transaction.getActualSettlementDate()
        );
    }

    /**
     * Internal method used for getting the Transaction object
     * @return the Transaction
     */
    private Transaction getTransaction() {
        return new Transaction(
                entityName,
                direction,
                agreedFx,
                currency,
                desiredSettlementDate,
                units,
                pricePerUnit
        );
    }
}
