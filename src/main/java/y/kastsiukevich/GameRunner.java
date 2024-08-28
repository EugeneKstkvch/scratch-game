package y.kastsiukevich;

import y.kastsiukevich.pojo.*;
import y.kastsiukevich.pojo.Probability;
import y.kastsiukevich.pojo.combo.LinearWinCombo;
import y.kastsiukevich.pojo.combo.SameSymbolWinCombo;
import y.kastsiukevich.pojo.combo.WinCombo;
import y.kastsiukevich.pojo.symbol.BonusSymbol;
import y.kastsiukevich.pojo.symbol.StandardSymbol;

import java.util.*;

public class GameRunner {

    private final GameConfig gameConfig;

    public GameRunner(GameConfig gameConfig) {
        this.gameConfig = gameConfig;
    }

    public GameRunResult run(double bet) {
        GameRunResult gameRunResult = new GameRunResult();
        String[][] matrix = new String[gameConfig.getRows()][gameConfig.getColumns()];
        Map<String, Integer> symbolCount = new HashMap<>();
        generateMatrix(matrix, symbolCount, gameRunResult);

        Map<String, List<WinCombo>> symbolWinningCombos = new HashMap<>();
        runSymbolCountCombinations(symbolCount, symbolWinningCombos);
        runLinearSymbolCombinations(matrix, symbolWinningCombos);

        populateGameResult(bet, gameRunResult, matrix, symbolWinningCombos);

        return gameRunResult;
    }

    /**
     * Counts total winning and populates game result
     */
    private void populateGameResult(double bet, GameRunResult gameRunResult, String[][] matrix, Map<String, List<WinCombo>> symbolWinningCombos) {
        double total = 0d;
        gameRunResult.setMatrix(matrix);
        if(symbolWinningCombos.isEmpty()) {
            gameRunResult.setReward(total);
        } else {
            Map<String, List<String>> winningCombinations = new HashMap<>();
            for(Map.Entry<String, List<WinCombo>> entry : symbolWinningCombos.entrySet()) {
                double symbolWin = bet * ((StandardSymbol) gameConfig.getSymbols().get(entry.getKey())).getMultiplier();
                for(WinCombo winCombo: entry.getValue()) {
                    symbolWin *= winCombo.getMultiplier();
                    winningCombinations.computeIfAbsent(entry.getKey(), k -> new ArrayList<>()).add(winCombo.getName());
                }
                total += symbolWin;

            }
            if(gameRunResult.getAppliedBonusSymbol() != null) {
                total = gameRunResult.getAppliedBonusSymbol().bonusReward(total);
            }
            gameRunResult.setReward(total);
            gameRunResult.setWinningCombinations(winningCombinations);
        }
    }

    /**
     * Runs linear_symbols combinations and collects winning combinations
     */
    private void runLinearSymbolCombinations(String[][] matrix, Map<String, List<WinCombo>> symbolWinningCombos) {
        for(LinearWinCombo combo: gameConfig.getLinearWinCombos()) {
            combo.getWinSymbols(matrix).forEach(s -> symbolWinningCombos.computeIfAbsent(s, k -> new ArrayList<>()).add(combo));
        }
    }

    /**
     * Runs same_symbols combinations and collects winning combinations
     */
    private void runSymbolCountCombinations(Map<String, Integer> symbolCount, Map<String, List<WinCombo>> symbolWinningCombos) {
        for(Map.Entry<String, Integer> entry: symbolCount.entrySet()) {
            for(SameSymbolWinCombo combo : gameConfig.getSameSymbolWinCombos()) {
                if(entry.getValue() >= combo.getCount()) {
                    symbolWinningCombos.put(entry.getKey(), new ArrayList<>(){{add(combo);}});
                } else {
                    //Optimization since gameConfig.sameSymbolWinCombos sorted by symbol count
                    break;
                }
            }
        }
    }

    /**
     * Generate matrix of symbols based on {@code GameConfig.probabilities}, counts presence of each element.
     */
    private void generateMatrix(String[][] matrix, Map<String, Integer> symbolCount, GameRunResult gameRunResult) {
        BonusSymbol bonus = null;
        for(Probability probability : gameConfig.getProbabilities()) {
            if(probability.getRow() < matrix.length && probability.getColumn() < matrix[0].length) {
                if(bonus == null && gameConfig.getBonusProbabilities().isProbabilityHit(gameConfig.getProbabilitiesTotalSum())) {
                    String bonusCurrent = gameConfig.getBonusProbabilities().getRandomSymbolBasedOnProbability();
                    matrix[probability.getRow()][probability.getColumn()] = bonusCurrent;
                    bonus = (BonusSymbol) gameConfig.getSymbols().get(bonusCurrent);
                } else {
                    String currentSymbol = probability.getRandomSymbolBasedOnProbability();
                    matrix[probability.getRow()][probability.getColumn()] = currentSymbol;
                    //count symbols to check against win-probability
                    symbolCount.merge(currentSymbol, 1, Integer::sum);
                }
            }
        }
        gameRunResult.setAppliedBonusSymbol(bonus);
    }
}
