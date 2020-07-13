import general.Calculator;
import general.Context;
import org.apfloat.Apfloat;
import util.TestUtils;

import java.io.FileWriter;
import java.io.IOException;

import static util.MonitorUtil.*;

public class Main {

    public static void main(String... args) throws Exception{

        try(Context c = Context.generateFromArgs(args)){
            setQuiet(c.isQuiet());
            Calculator calc = new Calculator(c);
            Apfloat value = logTime(calc::calculate,"Calculating E");
            if(c.getTestFile() == null){
                log("Omitting testing as test file not available.");
            }else{
                logTime(()->TestUtils.checkNumber(value,c.getTestFile()),"Validating E");
            }
            logTime(simplify(()-> writeToFile(value,c)),"Writing E");
        }
    }

    private static void writeToFile(Apfloat value, Context c){
        try {
            FileWriter writer = new FileWriter(c.getOutputFile());
            writer.write(value.toString(true));
            writer.close();
        } catch (IOException ioException) {
            err(ioException);
        }
    }
}
