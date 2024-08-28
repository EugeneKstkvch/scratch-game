package y.kastsiukevich;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import y.kastsiukevich.pojo.GameConfig;
import y.kastsiukevich.pojo.GameRunResult;

public class ScratchGameProcessor {

    private final GameConfigParser gameConfigParser;
    private final FileReader fileReader;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ScratchGameProcessor() {
        this.gameConfigParser = new GameConfigParser();
        this.fileReader = new FileReader();
    }

    public void process(String[] args) throws JsonProcessingException {
        Double bet = Double.parseDouble(getArgument(args, "--betting-amount"));
        String filename = getArgument(args, "--config");

        String data = fileReader.readFile(filename);

        GameConfig gameConfig = gameConfigParser.parseGameConfigJson(data);
        GameRunner gameRunner = new GameRunner(gameConfig);

        GameRunResult result = gameRunner.run(bet);
        System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(result));
    }

    private String getArgument(String[] args, String key) {
        for(int i = 0; i < args.length; i++) {
            if(args[i].equals(key) && i + 1 < args.length) {
                return args[i + 1];
            }
        }
        throw new IllegalArgumentException("No argument provided for " + key);
    }
}
