package Helpers;

import Components.ITransaction;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;

import static Helpers.ParsingUtil.lineToTransaction;

/**
 * @author Alexandru Dochioiu
 * Date : 21/10/17
 */
public class ParsingUtilTest {
    /**
     * Tests that the lines with the expected format are parsed accordingly
     */
    @Test
    public void lineToTransactionParsesExpectedFormat() {
        String line = "foo,B,0.50,SGP,01 Jan 2016,02 Jan 2016,200,100.25";
        double expectedUsdValue = 0.5 * 200 * 100.25;
        LocalDate expectedSettlementDate = LocalDate.of(2016,01,04);
        ITransaction transaction = lineToTransaction(line);

        Assert.assertEquals(
                "The entity name is not te expected one",
                "foo",
                transaction.getEntityName()
        );
        Assert.assertEquals(
                "The cashflow direction is not te expected one",
                ITransaction.CashflowDirection.Outgoing,
                transaction.getCashflowDirection()
        );
        Assert.assertEquals(
                "The equivalent USD value is not matching expected one",
                expectedUsdValue,
                transaction.getUsdValue(),
                1e-6
        );
        Assert.assertEquals(
                "The settlement date of the transaction is not the expected one",
                expectedSettlementDate,
                transaction.getActualSettlementDate()
        );
    }

    /**
     * Tests that the Buy/Sell marker is parsed and accounted for properly
     * Tests that no transaction is created for unexpected value in the Buy/Sell field
     */
    @Test
    public void lineToTransactionParsesCashflowDirectionProperly() {
        String line = "foo,B,0.50,SGP,01 Jan 2016,02 Jan 2016,200,100.25";
        ITransaction transaction = lineToTransaction(line);

        Assert.assertEquals(
                "The cashflow direction is not te expected one",
                ITransaction.CashflowDirection.Outgoing,
                transaction.getCashflowDirection()
        );

        line = "foo,S,0.50,SGP,01 Jan 2016,02 Jan 2016,200,100.25";
        transaction = lineToTransaction(line);

        Assert.assertEquals(
                "The cashflow direction is not te expected one",
                ITransaction.CashflowDirection.Incoming,
                transaction.getCashflowDirection()
        );

        line = "foo,YY,0.50,SGP,01 Jan 2016,02 Jan 2016,200,100.25";
        transaction = lineToTransaction(line);

        Assert.assertNull(
                "No transaction is created for unexpected cashflow direction",
                transaction
        );
    }

    /**
     * Tests that an unexpected input causes the method to return null
     */
    @Test
    public void lineToTransactionReturnsNoTransactionForUnexpectedInput() {
        String line = "unexpected input";
        ITransaction transaction = lineToTransaction(line);

        Assert.assertNull(
                "No transaction is created for unexpected input string",
                transaction
        );
    }

    /**
     * Tests that a null input causes the method to throw
     */
    @Test(expected = IllegalArgumentException.class)
    public void lineToTransactionThrowsForNullInput() {
        lineToTransaction(null);
    }
}
