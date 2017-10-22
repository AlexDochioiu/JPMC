import Components.ConsoleOutput;
import Components.ITransaction.CashflowDirection;

/**
 * @author Alexandru Dochioiu
 * Date : 20/10/17
 */
public class Main {
    public static void main(String[] args)
    {
        // this is used only to make the output from the ReportGenerator testable
        ConsoleOutput consoleOut = new ConsoleOutput();

        // follow the csv style
        String inputData = "foo,B,0.50,SGP,01 Jan 2016,02 Jan 2016,200,100.25\n" +
                "bar,S,0.22,AED,05 Jan 2016,07 Jan 2016,450,150.5\n" +
                "covfefe,S,0.26,AED,05 Mar 2016,07 Mar 2016,450,150.5\n" +
                "test2,B,0.7,AED,05 Oct 2016,07 Oct 2016,100,120.5\n" +
                "test,S,0.1,SGP,01 Jan 2016,02 Jan 2016,10,10\n" +
                "test2,B,0.25,SGP,01 Jan 2016,02 Jan 2016,100,10";

        ReportGenerator reportGen = new ReportGenerator(inputData, consoleOut);

        // TODO: Make those callable for a date range
        consoleOut.outputString("------------- Print Daily Summaries -------------");
        reportGen.printDailySummaries();
        consoleOut.outputString("-------------------------------------------------");
        consoleOut.outputString("\n");

        consoleOut.outputString("------------- Print Incoming Ranking ------------");
        reportGen.printRanking(CashflowDirection.Incoming);
        consoleOut.outputString("-------------------------------------------------");
        consoleOut.outputString("\n");

        consoleOut.outputString("------------- Print Outgoing Ranking ------------");
        reportGen.printRanking(CashflowDirection.Outgoing);
        consoleOut.outputString("-------------------------------------------------");
        consoleOut.outputString("\n");
    }
}
