package org.cyberspeed;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.cyberspeed.model.Matrix;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class App {
    public static void main(String[] args) throws IOException {

        if(args.length!=4 && args[0].equals("--config")  && args[2].equals("--betting-amount")){
            System.out.println("Error: Insufficient parameters provided.");
            System.out.println("Usage: java -jar <jar-file-name> --config <config-file> --betting-amount <amount>");
            return;
        }

        String  configFileName = args[1];
        int  bettingAmount = Integer.parseInt(args[3]);



        /**
         * TODO: Parsing json data with required information to generate a 3x3 Matrix with symbols given
         */
            String jsonFilePath = Paths.get("").toAbsolutePath()+ File.separator + configFileName;
            ObjectMapper objectMapper = new ObjectMapper();
            Matrix config = objectMapper.readValue(new File(jsonFilePath), Matrix.class);
            System.out.println(config.getSymbols().get("A").getRewardMultiplier());

        /**
         * TODO: Generate a randomly changing 3x3 Matrix with the given possible symbols
         */


        /**
         * TODO: Randomly Assign a bonus point on any one of the generated 3x3 Matrix cell
         */

        /**
         * TODO: Check the winning combinations applied for the above matrix generated
         */

        /**
         * TODO: calculated the reward
         */

    }
}
