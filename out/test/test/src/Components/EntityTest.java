package Components;

import Fakes.FakeTransaction;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Alexandru Dochioiu
 * Date : 21/10/17
 */
public class EntityTest {
    private String entityName;
    private Entity entity;

    @Before
    public void setUp() {
        entity = null;
        entityName = "default_entity_name";
    }
    /**
     * Tests that a null entity name will cause the constructor to throw
     */
    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsForNullEntityName() {
        entity = new Entity(null);
    }

    /**
     * Tests that the returned entity name matches the expected one
     */
    @Test
    public void getNameWorksProperly() {
        entity = new Entity(entityName);
        Assert.assertEquals(
                "The returned entity name is not the expected one",
                entityName,
                entity.getName()
        );
    }

    /**
     * Tests that the incoming and outgoing cashflows are zero when there is no
     * transaction added
     */
    @Test
    public void getTotalDirectedCashflowWorksProperlyForNoTransactions() {
        entity = new Entity(entityName);
        Assert.assertEquals(
                "No incoming cashflow was expected",
                0,
                entity.getTotalDirectedCashflow(ITransaction.CashflowDirection.Incoming),
                0
        );

        Assert.assertEquals(
                "No outgoing cashflow was expected",
                0,
                entity.getTotalDirectedCashflow(ITransaction.CashflowDirection.Outgoing),
                0
        );
    }

    /**
     * Tests that the incoming cashflows are added properly
     * Tests that the outgoing cashflows remain zero when only incoming cashflows are added
     */
    @Test
    public void getTotalDirectedCashflowCorrectlyAddsIncomingCashflows() {
        entity = new Entity(entityName);
        double expectedIncomingCashflow = 0;
        Assert.assertEquals(
                "There should not be any incoming cashflow at this point",
                expectedIncomingCashflow,
                entity.getTotalDirectedCashflow(ITransaction.CashflowDirection.Incoming),
                0
        );

        // Add incoming cashflow
        expectedIncomingCashflow += 120.004;
        entity.addTransaction(new FakeTransaction(120.004, ITransaction.CashflowDirection.Incoming));
        Assert.assertEquals(
                "The returned incoming cashflow for the entity does not match the expected one",
                expectedIncomingCashflow,
                entity.getTotalDirectedCashflow(ITransaction.CashflowDirection.Incoming),
                1e-6
        );
        Assert.assertEquals(
                "The outgoing cashflow should still be zero",
                0,
                entity.getTotalDirectedCashflow(ITransaction.CashflowDirection.Outgoing),
                0
        );

        expectedIncomingCashflow += 10;
        entity.addTransaction(new FakeTransaction(10, ITransaction.CashflowDirection.Incoming));
        Assert.assertEquals(
                "The returned incoming cashflow for the entity does not match the expected one",
                expectedIncomingCashflow,
                entity.getTotalDirectedCashflow(ITransaction.CashflowDirection.Incoming),
                1e-6
        );
        Assert.assertEquals(
                "The outgoing cashflow should still be zero",
                0,
                entity.getTotalDirectedCashflow(ITransaction.CashflowDirection.Outgoing),
                0
        );
    }

    /**
     * Tests that the outgoing cashflows are added properly
     * Tests that the incoming cashflows remain zero when only outgoing cashflows are added
     */
    @Test
    public void getTotalDirectedCashflowCorrectlyAddsOutgoingCashflows() {
        entity = new Entity(entityName);
        double expectedOutgoingCashflow = 0;
        Assert.assertEquals(
                "There should not be any incoming cashflow at this point",
                expectedOutgoingCashflow,
                entity.getTotalDirectedCashflow(ITransaction.CashflowDirection.Incoming),
                0
        );

        // Add incoming cashflow
        expectedOutgoingCashflow += 105.012;
        entity.addTransaction(new FakeTransaction(105.012, ITransaction.CashflowDirection.Outgoing));
        Assert.assertEquals(
                "The returned outgoing cashflow for the entity does not match the expected one",
                expectedOutgoingCashflow,
                entity.getTotalDirectedCashflow(ITransaction.CashflowDirection.Outgoing),
                1e-6
        );
        Assert.assertEquals(
                "The incoming cashflow should still be zero",
                0,
                entity.getTotalDirectedCashflow(ITransaction.CashflowDirection.Incoming),
                0
        );

        expectedOutgoingCashflow += 5;
        entity.addTransaction(new FakeTransaction(5, ITransaction.CashflowDirection.Outgoing));
        Assert.assertEquals(
                "The returned outgoing cashflow for the entity does not match the expected one",
                expectedOutgoingCashflow,
                entity.getTotalDirectedCashflow(ITransaction.CashflowDirection.Outgoing),
                1e-6
        );
        Assert.assertEquals(
                "The incoming cashflow should still be zero",
                0,
                entity.getTotalDirectedCashflow(ITransaction.CashflowDirection.Incoming),
                0
        );
    }

    /**
     * Tests that the combination of incoming and outgoing cashflows are accounted for accordingly
     */
    @Test
    public void getTotalDirectedCashflowCorrectlyAccountsForIncomingAndOutgoingCashflows() {
        entity = new Entity(entityName);
        double expectedIncomingCashflow = 13.45;
        double expectedOutgoingCashflow = 22.5;

        entity.addTransaction(new FakeTransaction(expectedIncomingCashflow, ITransaction.CashflowDirection.Incoming));
        entity.addTransaction(new FakeTransaction(expectedOutgoingCashflow, ITransaction.CashflowDirection.Outgoing));

        Assert.assertEquals(
                "The returned outgoing cashflow for the entity does not match the expected one",
                expectedOutgoingCashflow,
                entity.getTotalDirectedCashflow(ITransaction.CashflowDirection.Outgoing),
                1e-6
        );
        Assert.assertEquals(
                "The returned incoming cashflow for the entity does not match the expected one",
                expectedIncomingCashflow,
                entity.getTotalDirectedCashflow(ITransaction.CashflowDirection.Incoming),
                1e-6
        );

        // Add incoming cashflow
        expectedIncomingCashflow += 1.1;
        entity.addTransaction(new FakeTransaction(1.1, ITransaction.CashflowDirection.Incoming));
        Assert.assertEquals(
                "The returned outgoing cashflow for the entity does not match the expected one",
                expectedOutgoingCashflow,
                entity.getTotalDirectedCashflow(ITransaction.CashflowDirection.Outgoing),
                1e-6
        );
        Assert.assertEquals(
                "The returned incoming cashflow for the entity does not match the expected one",
                expectedIncomingCashflow,
                entity.getTotalDirectedCashflow(ITransaction.CashflowDirection.Incoming),
                1e-6
        );

        // Add outgoing cashflow
        expectedOutgoingCashflow += 0.5;
        entity.addTransaction(new FakeTransaction(0.5, ITransaction.CashflowDirection.Outgoing));
        Assert.assertEquals(
                "The returned outgoing cashflow for the entity does not match the expected one",
                expectedOutgoingCashflow,
                entity.getTotalDirectedCashflow(ITransaction.CashflowDirection.Outgoing),
                1e-6
        );
        Assert.assertEquals(
                "The returned incoming cashflow for the entity does not match the expected one",
                expectedIncomingCashflow,
                entity.getTotalDirectedCashflow(ITransaction.CashflowDirection.Incoming),
                1e-6
        );
    }
}
