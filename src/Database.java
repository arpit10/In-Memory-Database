import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Stack;

/**
 * A Simple database problem that takes stdin from command line and writes appropriate output.
 * AUTHOR: ARPIT JAIN
 */
public class Database {

    private final static String SET = "SET";
    private final static String UNSET = "UNSET";
    private final static String GET = "GET";
    private final static String NUM_EQUAL_TO = "NUMEQUALTO";
    private final static String BEGIN = "BEGIN";
    private final static String ROLLBACK = "ROLLBACK";
    private final static String COMMIT = "COMMIT";
    private final static String END = "END";

    private final static String NO_TRANSACTION = "> NO TRANSACTION";
    private final static String LINE_SPLIT_ON_SPACE = "\\s";

    private HashMap<String, String> map = new HashMap<>();
    private Stack<HashMap<String, String>> transactions = new Stack<>(); //Stores details about all active transactions.
    private HashMap<String, String> currentTransaction = new HashMap<>();

    private boolean activeTransactions;

    public static void main(String[] args) throws IOException {

        Database d = new Database();
        d.getInput();
    }

    /**
     * This function reads the input from console line by line until it encounters the string END.
     */
    private void getInput(){
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String line = reader.readLine();
            while (!line.equals(END)) {
                handleInput(line);
                line = reader.readLine();
            }
            System.out.println(END);
        } catch (IOException e) {
            System.err.print("Issue with command line input");
            e.printStackTrace();
        }
    }


    /**
     * Each input line is passed to this function and this function computes everyline based on the operation associated
     * with the line.
     * @param line - Input line.
     */
    private void handleInput(String line){
        System.out.println(line);
        String[] inputLine = line.split(LINE_SPLIT_ON_SPACE);
        String operator = inputLine[0]; //first word is always an operator

        //If there are any open transactions then set activeTransactions to true.
        if(!transactions.isEmpty()){
            activeTransactions = true;
            currentTransaction = transactions.peek();
        }else{
            activeTransactions = false;
        }
        switch(operator.toUpperCase()){
            case SET:
                setValue(inputLine[1],inputLine[2]);
                break;
            case GET:
                String value = getValue(inputLine[1]);
                System.out.println("> " + value);
                break;
            case UNSET:
                unset(inputLine[1]);
                break;
            case NUM_EQUAL_TO:
                int count = numEqualTo(inputLine[1]);
                System.out.println("> " + count);
                break;
            case BEGIN:
                HashMap<String, String> beginMap = new HashMap<>();
                //When the operator passed is BEGIN then start a new transaction. For every transaction add a new
                //Map to the stack and push contents to it.
                transactions.push(beginMap);
                break;
            case ROLLBACK:
                if(!activeTransactions){
                    System.out.println(NO_TRANSACTION);
                    break;
                }
                //Undo all commands issued in the most recent transaction.
                transactions.pop();
                break;
            case COMMIT:
                if(!activeTransactions){
                    System.out.println(NO_TRANSACTION);
                    break;
                }
                //Stop all transactions and set the value to the latest transaction.
                map = currentTransaction;
                transactions.removeAllElements();
                break;
            default:
                System.out.println("Invalid input");
                break;
        }

    }


    /**
     * Set the value of the variable in the hashmap so that it can be easily retrieved
     * @param variable - variable passed by the input
     * @param value - value passed by the input
     */
    public void setValue(String variable, String value){
        if(activeTransactions){
            currentTransaction.put(variable, value);
        }else {
            map.put(variable, value);
        }
    }

    /**
     * Get value of a specific variable from the latest transaction.
     * @param variable - Variable whose value is to be retrieved
     * @return - value of the variable in the hashmap
     */
    public String getValue(String variable){
        String value;
        if(activeTransactions && !currentTransaction.isEmpty()){
                value = currentTransaction.get(variable);
        }else {
            value = map.get(variable);
        }
        if(value == null) return "NULL";
        return value;
    }

    /**
     * This function will unset the value of any variavle and make it null.
     * @param variable - variable whose value is to be unset.
     */
    public void unset(String variable){
        if(activeTransactions){
            currentTransaction.put(variable, null);
        }else {
            map.remove(variable);
        }
    }

    /**
     * This function finds the number of occurrences of a value in the ddatabase.
     * @param value - value to be found
     * @return - number of occurrences
     */
    public int numEqualTo(String value){
        int count = 0;
        Collection<String> allValues;
        if(activeTransactions && !currentTransaction.isEmpty()){
                allValues = currentTransaction.values();
        }else {
            allValues = map.values();
        }
        for (String v : allValues) {
            if(v == null)break;
            if (v.equalsIgnoreCase(value)) {
                count += 1;
            }
        }
        return count;
    }

}