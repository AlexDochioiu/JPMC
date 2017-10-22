package Components;

/**
 * @author Alexandru Dochioiu
 * Date : 22/10/17
 */
public class ConsoleOutput implements IOutput {
    /**
     * Prints a string to console
     * @param str the String to be outputted
     */
    public void outputString(String str) {
        if(str.equals("\n")) {
            str = "";
        }
        System.out.println(str);
    }
}
