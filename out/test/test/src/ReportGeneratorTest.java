import Components.IOutput;

import Components.ITransaction;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexandru Dochioiu
 * Date : 21/10/17
 */
public class ReportGeneratorTest {
    private final int printColumnSize = ReportGenerator.printColumnSize;
    private MockOutput outputDestination;
    private ReportGenerator reportGenerator;

    @Before
    public void setUp() {
        outputDestination = new MockOutput();
    }

    @After
    public void tearDown() {
        reportGenerator = null;
    }

    /**
     * Tests that the constructor throws for a null input
     */
    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsForNullInput() {
        reportGenerator = new ReportGenerator(null, outputDestination);
    }

    /**
     * Tests that the constructor throws for a null outputDestination
     */
    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsForNullOutputDestination() {
        reportGenerator = new ReportGenerator("test", null);
    }

    /**
     * Tests that the daily summary is printed in the reverse chronological order and with the expected values for a
     * simple input
     */
    @Test
    public void dailySummariesWorksAsExpectedForSimpleInputs() {
        String inputData = "foo,B,0.50,SGP,01 Jan 2016,02 Jan 2016,200,100.25\n" +
                        "bar,S,0.22,AED,05 Jan 2016,07 Jan 2016,450,150.5";

        reportGenerator = new ReportGenerator(inputData, outputDestination);
        reportGenerator.printDailySummaries();

        String firstExpectedLine = String.format("%-" + String.valueOf(printColumnSize) + "s", "07 Jan 2016") +
                String.format("%-" + String.valueOf(printColumnSize) + ".2f", 14899.5) +
                String.format("%-" + String.valueOf(printColumnSize) + ".2f", 0.0);
        String secondExpectedLine = String.format("%-" + String.valueOf(printColumnSize) + "s", "04 Jan 2016") +
                String.format("%-" + String.valueOf(printColumnSize) + ".2f", 0.0) +
                String.format("%-" + String.valueOf(printColumnSize) + ".2f", 10025.0);

        Assert.assertEquals(
                "The number of lines outputted does not match the expected value",
                3,
                outputDestination.outputted.size()
        );
        Assert.assertEquals(
                "First line of the daily summaries does not match the expected one",
                firstExpectedLine,
                outputDestination.outputted.get(1) // we ignore the header line
        );
        Assert.assertEquals(
                "Second line of the daily summaries does not match the expected one",
                secondExpectedLine,
                outputDestination.outputted.get(2)
        );
    }

    /**
     * Tests that the daily summary is printed in the reverse chronological order and with the expected values for
     * an input combining multiple cashflows on a day
     */
    @Test
    public void dailySummariesWorksForMultipleCashflowsOnTheSameDay() {
        String inputData = "foo,B,0.50,SGP,01 Jan 2016,02 Jan 2016,200,100.25\n" +
                "bar,S,0.22,AED,05 Jan 2016,07 Jan 2016,450,150.5\n" +
                "covfefe,S,0.22,AED,05 Mar 2016,07 Mar 2016,450,150.5\n" +
                "test,S,0.1,SGP,01 Jan 2016,02 Jan 2016,10,10\n" +
                "test2,B,0.25,SGP,01 Jan 2016,02 Jan 2016,100,10";

        reportGenerator = new ReportGenerator(inputData, outputDestination);
        reportGenerator.printDailySummaries();

        String firstExpectedLine = String.format("%-" + String.valueOf(printColumnSize) + "s", "07 Mar 2016") +
                String.format("%-" + String.valueOf(printColumnSize) + ".2f", 14899.5) +
                String.format("%-" + String.valueOf(printColumnSize) + ".2f", 0.0);
        String secondExpectedLine = String.format("%-" + String.valueOf(printColumnSize) + "s", "07 Jan 2016") +
                String.format("%-" + String.valueOf(printColumnSize) + ".2f", 14899.5) +
                String.format("%-" + String.valueOf(printColumnSize) + ".2f", 0.0);
        String thirdExpectedLine = String.format("%-" + String.valueOf(printColumnSize) + "s", "04 Jan 2016") +
                String.format("%-" + String.valueOf(printColumnSize) + ".2f", 10.0) +
                String.format("%-" + String.valueOf(printColumnSize) + ".2f", 10275.0);

        Assert.assertEquals(
                "The number of lines outputted does not match the expected value",
                4,
                outputDestination.outputted.size()
        );
        Assert.assertEquals(
                "First line of the daily summaries does not match the expected one",
                firstExpectedLine,
                outputDestination.outputted.get(1) // we ignore the header line
        );
        Assert.assertEquals(
                "Second line of the daily summaries does not match the expected one",
                secondExpectedLine,
                outputDestination.outputted.get(2)
        );
        Assert.assertEquals(
                "Third line of the daily summaries does not match the expected one",
                thirdExpectedLine,
                outputDestination.outputted.get(3)
        );
    }

    /**
     * Tests that the ranking is printed properly for incoming cashflows when no entity appears
     * more than once
     */
    @Test
    public void printRankingWorksForIncomingCashflowsWhenNoEntityAppearsMultipleTimes() {
        String inputData = "foo,B,0.50,SGP,01 Jan 2016,02 Jan 2016,200,100.25\n" +
                "bar,S,0.22,AED,05 Jan 2016,07 Jan 2016,450,150.5\n" +
                "covfefe,S,0.26,AED,05 Mar 2016,07 Mar 2016,450,150.5\n" +
                "test,S,0.1,SGP,01 Jan 2016,02 Jan 2016,10,10\n" +
                "test2,B,0.25,SGP,01 Jan 2016,02 Jan 2016,100,10";

        reportGenerator = new ReportGenerator(inputData, outputDestination);
        reportGenerator.printRanking(ITransaction.CashflowDirection.Incoming);

        String firstExpectedLine = String.format("%-" + String.valueOf(printColumnSize) + "s", "covfefe") +
                String.format("%-" + String.valueOf(printColumnSize) + ".2f", 17608.5);
        String secondExpectedLine = String.format("%-" + String.valueOf(printColumnSize) + "s", "bar") +
                String.format("%-" + String.valueOf(printColumnSize) + ".2f", 14899.5);
        String thirdExpectedLine = String.format("%-" + String.valueOf(printColumnSize) + "s", "test") +
                String.format("%-" + String.valueOf(printColumnSize) + ".2f", 10.0);

        // those have no value so I won't check for order
        String[] otherLines = new String[2];
        otherLines[0] = String.format("%-" + String.valueOf(printColumnSize) + "s", "test2") +
                String.format("%-" + String.valueOf(printColumnSize) + ".2f", 0.0);
        otherLines[1] = String.format("%-" + String.valueOf(printColumnSize) + "s", "foo") +
                String.format("%-" + String.valueOf(printColumnSize) + ".2f", 0.0);

        Assert.assertEquals(
                "The number of lines outputted does not match the expected value",
                6,
                outputDestination.outputted.size()
        );
        Assert.assertEquals(
                "First line of the daily summaries does not match the expected one",
                firstExpectedLine,
                outputDestination.outputted.get(1) // we ignore the header line
        );
        Assert.assertEquals(
                "Second line of the daily summaries does not match the expected one",
                secondExpectedLine,
                outputDestination.outputted.get(2)
        );
        Assert.assertEquals(
                "Third line of the daily summaries does not match the expected one",
                thirdExpectedLine,
                outputDestination.outputted.get(3)
        );
        Assert.assertTrue(
                "The line containing entity \"test2\" was wrong or missing",
                outputDestination.outputted.contains(otherLines[0])
        );
        Assert.assertTrue(
                "The line containing entity \"foo\" was wrong or missing",
                outputDestination.outputted.contains(otherLines[1])
        );
    }

    /**
     * Tests that the ranking is printed properly for incoming cashflows when an entity
     * appears more than once
     */
    @Test
    public void printRankingWorksForIncomingCashflowsWhenEntityAppearsMultipleTimes() {
        String inputData = "foo,B,0.50,SGP,01 Jan 2016,02 Jan 2016,200,100.25\n" +
                "bar,S,0.22,AED,05 Jan 2016,07 Jan 2016,450,150.5\n" +
                "covfefe,S,0.26,AED,05 Mar 2016,07 Mar 2016,450,150.5\n" +
                "bar,S,0.7,AED,05 Oct 2016,07 Oct 2016,100,120.5\n" +
                "test,S,0.1,SGP,01 Jan 2016,02 Jan 2016,10,10\n" +
                "test2,B,0.25,SGP,01 Jan 2016,02 Jan 2016,100,10";

        reportGenerator = new ReportGenerator(inputData, outputDestination);
        reportGenerator.printRanking(ITransaction.CashflowDirection.Incoming);

        String firstExpectedLine = String.format("%-" + String.valueOf(printColumnSize) + "s", "bar") +
                String.format("%-" + String.valueOf(printColumnSize) + ".2f", 23334.5);
        String secondExpectedLine = String.format("%-" + String.valueOf(printColumnSize) + "s", "covfefe") +
                String.format("%-" + String.valueOf(printColumnSize) + ".2f", 17608.5);
        String thirdExpectedLine = String.format("%-" + String.valueOf(printColumnSize) + "s", "test") +
                String.format("%-" + String.valueOf(printColumnSize) + ".2f", 10.0);

        // those have no value so I won't check for order
        String[] otherLines = new String[2];
        otherLines[0] = String.format("%-" + String.valueOf(printColumnSize) + "s", "test2") +
                String.format("%-" + String.valueOf(printColumnSize) + ".2f", 0.0);
        otherLines[1] = String.format("%-" + String.valueOf(printColumnSize) + "s", "foo") +
                String.format("%-" + String.valueOf(printColumnSize) + ".2f", 0.0);

        Assert.assertEquals(
                "The number of lines outputted does not match the expected value",
                6,
                outputDestination.outputted.size()
        );
        Assert.assertEquals(
                "First line of the daily summaries does not match the expected one",
                firstExpectedLine,
                outputDestination.outputted.get(1) // we ignore the header line
        );
        Assert.assertEquals(
                "Second line of the daily summaries does not match the expected one",
                secondExpectedLine,
                outputDestination.outputted.get(2)
        );
        Assert.assertEquals(
                "Third line of the daily summaries does not match the expected one",
                thirdExpectedLine,
                outputDestination.outputted.get(3)
        );
        Assert.assertTrue(
                "The line containing entity \"test2\" was wrong or missing",
                outputDestination.outputted.contains(otherLines[0])
        );
        Assert.assertTrue(
                "The line containing entity \"foo\" was wrong or missing",
                outputDestination.outputted.contains(otherLines[1])
        );
    }

    /**
     * Tests that the ranking is printed properly for outgoing cashflows when no entity appears
     * more than once
     */
    @Test
    public void printRankingWorksForOutgoingCashflowsWhenNoEntityAppearsMultipleTimes() {
        String inputData = "foo,B,0.50,SGP,01 Jan 2016,02 Jan 2016,200,100.25\n" +
                "bar,S,0.22,AED,05 Jan 2016,07 Jan 2016,450,150.5\n" +
                "covfefe,S,0.26,AED,05 Mar 2016,07 Mar 2016,450,150.5\n" +
                "test,S,0.1,SGP,01 Jan 2016,02 Jan 2016,10,10\n" +
                "test2,B,0.25,SGP,01 Jan 2016,02 Jan 2016,100,10";

        reportGenerator = new ReportGenerator(inputData, outputDestination);
        reportGenerator.printRanking(ITransaction.CashflowDirection.Outgoing);

        String firstExpectedLine = String.format("%-" + String.valueOf(printColumnSize) + "s", "foo") +
                String.format("%-" + String.valueOf(printColumnSize) + ".2f", 10025.0);
        String secondExpectedLine = String.format("%-" + String.valueOf(printColumnSize) + "s", "test2") +
                String.format("%-" + String.valueOf(printColumnSize) + ".2f", 250.0);

        // those have no value so I won't check for order
        String[] otherLines = new String[3];
        otherLines[0] = String.format("%-" + String.valueOf(printColumnSize) + "s", "bar") +
                String.format("%-" + String.valueOf(printColumnSize) + ".2f", 0.0);
        otherLines[1] = String.format("%-" + String.valueOf(printColumnSize) + "s", "test") +
                String.format("%-" + String.valueOf(printColumnSize) + ".2f", 0.0);
        otherLines[2] = String.format("%-" + String.valueOf(printColumnSize) + "s", "covfefe") +
                String.format("%-" + String.valueOf(printColumnSize) + ".2f", 0.0);

        Assert.assertEquals(
                "The number of lines outputted does not match the expected value",
                6,
                outputDestination.outputted.size()
        );
        Assert.assertEquals(
                "First line of the daily summaries does not match the expected one",
                firstExpectedLine,
                outputDestination.outputted.get(1) // we ignore the header line
        );
        Assert.assertEquals(
                "Second line of the daily summaries does not match the expected one",
                secondExpectedLine,
                outputDestination.outputted.get(2)
        );
        Assert.assertTrue(
                "The line containing entity \"bar\" was wrong or missing",
                outputDestination.outputted.contains(otherLines[0])
        );
        Assert.assertTrue(
                "The line containing entity \"test\" was wrong or missing",
                outputDestination.outputted.contains(otherLines[1])
        );
        Assert.assertTrue(
                "The line containing entity \"covfefe\" was wrong or missing",
                outputDestination.outputted.contains(otherLines[2])
        );
    }

    /**
     * Tests that the ranking is printed properly for outgoing cashflows when an entity
     * appears more than once
     */
    @Test
    public void printRankingWorksForOutgoingCashflowsWhenEntityAppearsMultipleTimes() {
        String inputData = "foo,B,0.50,SGP,01 Jan 2016,02 Jan 2016,200,100.25\n" +
                "bar,S,0.22,AED,05 Jan 2016,07 Jan 2016,450,150.5\n" +
                "covfefe,S,0.26,AED,05 Mar 2016,07 Mar 2016,450,150.5\n" +
                "test2,B,0.7,AED,05 Oct 2016,07 Oct 2016,100,120.5\n" +
                "test,S,0.1,SGP,01 Jan 2016,02 Jan 2016,10,10\n" +
                "test2,B,0.25,SGP,01 Jan 2016,02 Jan 2016,100,10";

        reportGenerator = new ReportGenerator(inputData, outputDestination);
        reportGenerator.printRanking(ITransaction.CashflowDirection.Outgoing);

        String firstExpectedLine = String.format("%-" + String.valueOf(printColumnSize) + "s", "foo") +
                String.format("%-" + String.valueOf(printColumnSize) + ".2f", 10025.0);
        String secondExpectedLine = String.format("%-" + String.valueOf(printColumnSize) + "s", "test2") +
                String.format("%-" + String.valueOf(printColumnSize) + ".2f", 8685.0);

        // those have no value so I won't check for order
        String[] otherLines = new String[3];
        otherLines[0] = String.format("%-" + String.valueOf(printColumnSize) + "s", "bar") +
                String.format("%-" + String.valueOf(printColumnSize) + ".2f", 0.0);
        otherLines[1] = String.format("%-" + String.valueOf(printColumnSize) + "s", "test") +
                String.format("%-" + String.valueOf(printColumnSize) + ".2f", 0.0);
        otherLines[2] = String.format("%-" + String.valueOf(printColumnSize) + "s", "covfefe") +
                String.format("%-" + String.valueOf(printColumnSize) + ".2f", 0.0);

        Assert.assertEquals(
                "The number of lines outputted does not match the expected value",
                6,
                outputDestination.outputted.size()
        );
        Assert.assertEquals(
                "First line of the daily summaries does not match the expected one",
                firstExpectedLine,
                outputDestination.outputted.get(1) // we ignore the header line
        );
        Assert.assertEquals(
                "Second line of the daily summaries does not match the expected one",
                secondExpectedLine,
                outputDestination.outputted.get(2)
        );
        Assert.assertTrue(
                "The line containing entity \"bar\" was wrong or missing",
                outputDestination.outputted.contains(otherLines[0])
        );
        Assert.assertTrue(
                "The line containing entity \"test\" was wrong or missing",
                outputDestination.outputted.contains(otherLines[1])
        );
        Assert.assertTrue(
                "The line containing entity \"covfefe\" was wrong or missing",
                outputDestination.outputted.contains(otherLines[2])
        );
    }

    class MockOutput implements IOutput {
        public void outputString(String str) {
            outputted.add(str);
        }

        public final List<String> outputted = new ArrayList<>();
    }
}
