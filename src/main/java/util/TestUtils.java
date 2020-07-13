package util;

import org.apfloat.Apfloat;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static util.MonitorUtil.*;

public final class TestUtils {
    public static int checkNumber(Apfloat e, String path){
        try{
            String truth = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.US_ASCII);
            String eString = e.toString(true);

            if(truth.length() < eString.length()){
                err("Could not validate as file's {e} length is shorter");
                return -1;
            }
            for(int i=0;i<eString.length();i++){
                if(eString.charAt(i) != truth.charAt(i)){
                    err("Validation number miss at "+i);
                    return i;
                }
            };
        }catch (Exception ex){
            err(ex);
        }

        return -1;

    }
}
