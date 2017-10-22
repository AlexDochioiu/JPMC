import Components.IOutput;
import Components.ITransaction.CashflowDirection;
import Components.Entity;
import Components.ITransaction;
import Helpers.ParsingUtil;

import java.time.LocalDate;
import java.util.*;

/**
 * @author Alexandru Dochioiu
 * Date : 20/10/17
 *
 * Class used for generating a report based on a list of transactions
 */
public class ReportGenerator {
    /**
     * Constructor
     * @param input the string to be processed for generating the report
     * @param outDestination the class used for outputting data
     */
    public ReportGenerator(String input, IOutput outDestination) {
        if (input == null) {
            throw new IllegalArgumentException("input");
        }
        if (outDestination == null) {
            throw new IllegalArgumentException("outDestination");
        }
        outputDestination = outDestination;
        parseInput(input);
    }

     private void parseInput(String input)
     {
         String[] lines = input.split("\n");
         String entityName;

         ITransaction transaction;

         for (String line : lines) {
             //Skip empty lines
             if (line.equals("")) {
                 continue;
             }

            transaction = ParsingUtil.lineToTransaction(line);

            // If we failed to parse that line, just skip it
            if (transaction == null) {
                // Ideally an error would be logged here
                continue;
            }

            entityName = transaction.getEntityName();

            Entity entity;

            // Get the entity from the HashMap or create it if nonexistent
            if (entities.containsKey(entityName)) {
                entity = entities.get(entityName);
            }
            else {
                entity = new Entity(entityName);
                entities.put(entityName, entity);
            }

            // link the transaction to the entity
            entity.addTransaction(transaction);

            addToDailySummary(
                transaction.getActualSettlementDate(),
                transaction.getCashflowDirection(),
                transaction.getUsdValue()
             );
         }
     }

    /**
     * Prints the reverse chronologically ordered daily summary to console
     * Note: Prints only for days in which transactions got executed (there is cashflow in either direction)
     */
    public void printDailySummaries() {
        List<LocalDate> sortedDates = new ArrayList<>(dailySummaries.keySet());
        Collections.sort(sortedDates, (day1, day2) -> day2.compareTo(day1));

        String header = String.format("%-" + String.valueOf(printColumnSize) + "s", "Date") +
                String.format("%-" + String.valueOf(printColumnSize) + "s", "Incoming") +
                String.format("%-" + String.valueOf(printColumnSize) + "s", "Outgoing");

        outputDestination.outputString(header);
        for ( LocalDate date : sortedDates) {
            outputDestination.outputString(String.format("%-" + String.valueOf(printColumnSize) + "s", ParsingUtil.dateFormat.format(date)) +
                    dailySummaries.get(date).toString());
        }
     }

    /**
     * Prints the list of entities and total cashflow in the desired direction
     * Note: The list is reverse ordered using the magnitude of cashflow
     * @param direction the direction of the cashflow we are interested in
     */
     public void printRanking(CashflowDirection direction) {
         List<Entity> sortedEntities = new ArrayList<>(entities.values());

         String header = String.format("%-" + String.valueOf(printColumnSize) + "s", "Entity") +
                 String.format("%-" + String.valueOf(printColumnSize) + "s", String.valueOf(direction));

         Collections.sort(
                 sortedEntities,
                 (entity1, entity2) -> Double.compare(
                         entity2.getTotalDirectedCashflow(direction),
                         entity1.getTotalDirectedCashflow(direction))
         );

         outputDestination.outputString(header);
         for (Entity entity : sortedEntities) {
             outputDestination.outputString(
                     String.format(
                             "%-" + String.valueOf(printColumnSize) + "s",
                             entity.getName()) +
                     String.format(
                             "%-" + String.valueOf(printColumnSize) + ".2f",
                             entity.getTotalDirectedCashflow(direction)));
         }
     }

    /**
     * Adds the cashflow to the daily summary for that given day
     * @param date the date when the cashflow happened
     * @param direction the direction of the cashflow
     * @param amount the amount of cash
     */
     private void addToDailySummary(LocalDate date, CashflowDirection direction, double amount) {
        DailySummary summaryOfDay;

        if (dailySummaries.containsKey(date)) {
            summaryOfDay = dailySummaries.get(date);
        }
        else {
            summaryOfDay = new DailySummary();
            dailySummaries.put(date, summaryOfDay);
        }

        switch (direction) {
            case Incoming:
                summaryOfDay.incoming += amount;
                break;
            case Outgoing:
                summaryOfDay.outgoing += amount;
                break;
        }
     }

    private IOutput outputDestination;
    private final Map<String, Entity> entities = new HashMap<>();
    private final Map<LocalDate, DailySummary> dailySummaries= new HashMap<>();

    public static int printColumnSize = 20;

    /**
     * Internal class used for storing the total cashflow summary for a single day
     */
    class DailySummary
    {
        @Override
        public String toString() {
            return String.format("%-" + String.valueOf(printColumnSize) + ".2f", incoming) +
                    String.format("%-" + String.valueOf(printColumnSize) + ".2f", outgoing);
        }

        public double incoming = 0;
        public double outgoing = 0;
    }
}
