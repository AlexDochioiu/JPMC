package Fakes;

import Components.ITransaction;

import java.time.LocalDate;

/**
 * @author Alexandru Dochioiu
 * Date : 22/10/17
 */
public class FakeTransaction implements ITransaction {
    public FakeTransaction(double usdValue, CashflowDirection direction) {
        this.usdValue = usdValue;
        this.direction = direction;
    }

    public String getEntityName() {
        throw new UnsupportedOperationException();
    }

    public double getUsdValue() {
        return usdValue;
    }

    public CashflowDirection getCashflowDirection() {
        return direction;
    }

    public LocalDate getActualSettlementDate() {
        throw new UnsupportedOperationException();
    }

    private double usdValue;
    private CashflowDirection direction;
}
