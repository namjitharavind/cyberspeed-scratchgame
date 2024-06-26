package org.cyberspeed;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.cyberspeed.model.Matrix;
import org.cyberspeed.model.OutputDto;
import org.cyberspeed.service.ScratchGameService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;

public class App {
    public static void main(String[] args) throws IOException {

        if (args.length != 4 && args[0].equals("--config") && args[2].equals("--betting-amount")) {
            System.out.println("Error: Insufficient parameters provided.");
            System.out.println("Usage: java -jar <jar-file-name> --config <config-file> --betting-amount <amount>");
            return;
        }

        String configFileName = args[1];
        int bettingAmount = Integer.parseInt(args[3]);

        /**
         * Parsing json data with required information to generate a 3x3 Matrix with symbols given
         */
        String jsonFilePath = Paths.get("").toAbsolutePath() + File.separator + configFileName;
        ObjectMapper objectMapper = new ObjectMapper();
        Matrix config = objectMapper.readValue(new File(jsonFilePath), Matrix.class);

        /**
         * Generate a randomly changing 3x3 Matrix with the given possible symbols and its probabilities
         */
        ScratchGameService scratchGameService = new ScratchGameService(config);
        String[][] generatedMatrix = scratchGameService.generateMatrix();
        /**
         * Randomly Assign a bonus point on any one of the generated 3x3 Matrix cell
         */

        StringBuilder assignedBonus = new StringBuilder();
        scratchGameService.assignBonusSymbol(generatedMatrix, assignedBonus);
        /**
         * Check the winning combinations applied for the above matrix generated
         */

        Map<String, Set<String>> appliedWinningCombinations = scratchGameService.checkWinningCombinations(generatedMatrix);


        /**
         *  calculate the reward
         */

        double reward = scratchGameService.calculateReward(bettingAmount, appliedWinningCombinations, assignedBonus.toString());
        OutputDto outputDto = new OutputDto(generatedMatrix, reward, appliedWinningCombinations, assignedBonus.toString());
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        String jsonString = objectMapper.writeValueAsString(outputDto);
        System.out.println(jsonString);

    }
}
