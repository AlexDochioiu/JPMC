package Components;

import java.time.LocalDate;

/**
 * @author Alexandru Dochioiu
 * Date : 21/10/17
 *
 * Interface used for managing transactions
 */
public interface ITransaction {
    /**
     * Enumeration giving the direction of a Cashflow
     */
    enum CashflowDirection
    {
        Incoming,
        Outgoing
    }

    /**
     * Getter used for obtaining the name of the entity taking part in the transaction
     * @return the name of the entity taking part in the transaction
     */
    String getEntityName();

    /**
     * Getter used for obtaining the value of the transaction in US Dollars
     * @return the USD value of the transaction
     */
    double getUsdValue();

    /**
     * Getter used for obtaining the direction of the cash flow
     * @return the direction of the cash flow
     */
    CashflowDirection getCashflowDirection();

    /**
     * Getter used for obtaining the actual settlement date of the transaction
     * @return the settlement date of the transaction
     */
    LocalDate getActualSettlementDate();

}
