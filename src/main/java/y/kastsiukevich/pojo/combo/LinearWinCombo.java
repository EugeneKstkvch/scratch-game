package y.kastsiukevich.pojo.combo;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class LinearWinCombo implements WinCombo {
    private String name;
    private Double multiplier;
    private String group;
    private List<List<Pair<Integer, Integer>>> lines;

    public List<String> getWinSymbols(String[][] matrix) {
        List<String> winSymbols = new ArrayList<>();
        for(List<Pair<Integer, Integer>> line: lines) {
            int i = 0;
            String curr = matrix[line.get(i).getKey()][line.get(i).getValue()];
            i++;
            while(i < line.size()) {
                int row = line.get(i).getKey();
                int column = line.get(i).getValue();
                if(curr == null || row >= matrix.length || column >= matrix[0].length || !curr.equals(matrix[row][column])) {
                    break;
                }
                i++;
            }
            if(i == line.size()) {
                winSymbols.add(curr);
            }
        }
        return winSymbols;
    }
}
