package y.kastsiukevich;

import org.apache.commons.math3.util.Pair;
import org.json.JSONArray;
import org.json.JSONObject;
import y.kastsiukevich.pojo.*;
import y.kastsiukevich.pojo.Probability;
import y.kastsiukevich.pojo.combo.LinearWinCombo;
import y.kastsiukevich.pojo.combo.SameSymbolWinCombo;
import y.kastsiukevich.pojo.symbol.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class GameConfigParser {

    public GameConfig parseGameConfigJson(String json) {
        try {
            GameConfig gameConfig = new GameConfig();
            JSONObject root = new JSONObject(json);

            gameConfig.setColumns(root.getInt("columns"));
            gameConfig.setRows(root.getInt("rows"));

            readGameConfigSymbols(root, gameConfig);
            readGameConfigProbabilities(root, gameConfig);
            readGameConfigWinCombinations(root, gameConfig);

            return gameConfig;
        } catch (Exception e) {
            throw new RuntimeException("Error during json config parsing, check validity of config json file", e);
        }
    }

    private void readGameConfigWinCombinations(JSONObject root, GameConfig gameConfig) {
        JSONObject winComboObject = root.getJSONObject("win_combinations");
        List<SameSymbolWinCombo> winCombinations = new ArrayList<>();
        List<LinearWinCombo> linearWinCombos = new ArrayList<>();
        winComboObject.keys().forEachRemaining(key -> {
            JSONObject winCombo = winComboObject.getJSONObject(key);
            String when = winCombo.getString("when");
            switch (when) {
                case "same_symbols": {
                    winCombinations.add(new SameSymbolWinCombo(key, winCombo.getDouble("reward_multiplier"), winCombo.getInt("count")));
                    break;
                }
                case "linear_symbols": {
                    JSONArray coveredAreasArray = winCombo.getJSONArray("covered_areas");
                    List<List<Pair<Integer, Integer>>> coveredAreas = new ArrayList<>();
                    for(int i = 0; i < coveredAreasArray.length(); i++) {
                        JSONArray coveredArea = coveredAreasArray.getJSONArray(i);
                        List<Pair<Integer, Integer>> line = new ArrayList<>();
                        for(int j = 0; j < coveredArea.length(); j++) {
                            String[] coords = coveredArea.getString(j).split(":");
                            line.add(new Pair<>(Integer.parseInt(coords[0]), Integer.parseInt(coords[1])));
                        }
                        coveredAreas.add(line);
                    }
                    linearWinCombos.add(new LinearWinCombo(key, winCombo.getDouble("reward_multiplier"), winCombo.getString("group"), coveredAreas));
                    break;
                }
            }
        });
        Collections.sort(winCombinations, Comparator.comparing(SameSymbolWinCombo::getCount));
        gameConfig.setSameSymbolWinCombos(winCombinations);
        gameConfig.setLinearWinCombos(linearWinCombos);
    }

    private void readGameConfigProbabilities(JSONObject root, GameConfig gameConfig) {
        JSONObject probabilitiesObject = root.getJSONObject("probabilities");
        JSONArray standardSymbolProbs = probabilitiesObject.getJSONArray("standard_symbols");
        List<Probability> probabilities = new ArrayList<>();
        int totalSum = 0;
        for(int i = 0; i < standardSymbolProbs.length(); i++) {
            JSONObject probability = standardSymbolProbs.getJSONObject(i);
            JSONObject symbolMapObject = probability.getJSONObject("symbols");
            AtomicInteger probSum = new AtomicInteger();
            Map<String, Integer> symbolProbs = new HashMap<>();
            symbolMapObject.keys().forEachRemaining(key -> {
                int prob = symbolMapObject.getInt(key);
                probSum.addAndGet(prob);
                symbolProbs.put(key, prob);
            });
            totalSum += probSum.get();
            probabilities.add(new Probability(probability.getInt("column"), probability.getInt("row"), symbolProbs, probSum.get()));
        }
        gameConfig.setProbabilities(probabilities);

        JSONObject bonusSymbolProbs = probabilitiesObject.getJSONObject("bonus_symbols").getJSONObject("symbols");
        AtomicInteger bonusProbSum = new AtomicInteger();
        Map<String, Integer> symbolProbs = new HashMap<>();
        bonusSymbolProbs.keys().forEachRemaining(key -> {
            int prob = bonusSymbolProbs.getInt(key);
            bonusProbSum.addAndGet(prob);
            symbolProbs.put(key, prob);
        });
        gameConfig.setBonusProbabilities( new Probability(-1, -1, symbolProbs, bonusProbSum.get()));
        totalSum += bonusProbSum.get();
        gameConfig.setProbabilitiesTotalSum(totalSum);
    }

    private void readGameConfigSymbols(JSONObject root, GameConfig config) {
        JSONObject symbols = root.getJSONObject("symbols");
        Map<String, Symbol> symbolMap = new HashMap<>();
        symbols.keys().forEachRemaining(key -> {
            JSONObject symbol = symbols.getJSONObject(key);
            String symbolType = symbol.getString("type");
            switch (symbolType) {
                case "standard":
                    symbolMap.put(key, new StandardSymbol(key, symbol.getDouble("reward_multiplier")));
                    break;
                case "bonus":
                    String impact = symbol.getString("impact");
                    switch(impact) {
                        case "multiply_reward":
                            symbolMap.put(key, new MultiplyBonusSymbol(key, impact, symbol.getDouble("reward_multiplier")));
                            break;
                        case "extra_bonus":
                            symbolMap.put(key, new ExtraBonusSymbol(key, impact, symbol.getDouble("extra")));
                            break;
                        case "miss":
                            symbolMap.put(key, new MissBonusSymbol(key, impact));
                            break;
                    }
                    break;
            }
        });
        config.setSymbols(symbolMap);
    }
}
