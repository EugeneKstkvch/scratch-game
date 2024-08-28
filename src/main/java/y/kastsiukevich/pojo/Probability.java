package y.kastsiukevich.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Random;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class Probability {
    private int column;
    private int row;
    private Map<String, Integer> symbols;
    private int symbolProbSum;
    private final Random random = new Random();

    public boolean isProbabilityHit(int totalProbSum) {
        return random.nextInt(totalProbSum) < symbolProbSum;
    }

    public String getRandomSymbolBasedOnProbability() {
        int index = random.nextInt(symbolProbSum);
        int sum = 0;
        int i=0;
        Map.Entry<String,Integer>[] entries = symbols.entrySet().toArray(new Map.Entry[symbols.size()]);
        while(sum < index) {
            sum = sum + entries[i++].getValue();
        }
        return entries[Math.max(0,i-1)].getKey();
    }
}
