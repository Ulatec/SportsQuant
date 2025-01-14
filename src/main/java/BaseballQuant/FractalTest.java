package BaseballQuant;

//import jdk.incubator.vector.FloatVector;
//import jdk.incubator.vector.VectorSpecies;
//import jdk.incubator.vector.FloatVector;
//import jdk.incubator.vector.VectorSpecies;
//import jdk.incubator.vector.FloatVector;
//import jdk.incubator.vector.VectorSpecies;
import BaseballQuant.Model.MLBPitcherPerformance;
import org.springframework.boot.autoconfigure.SpringBootApplication;






import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.JPanel;

import static java.lang.Thread.sleep;

public class FractalTest
{

    public static void main(String[] args) throws InterruptedException {
        List<Integer> l = new ArrayList<>();

        for (int i = 0; i < 1000000; ++i) {
            l.add(i);
        }
        Collections.shuffle(l);

        long start1 = System.currentTimeMillis();
        l.sort(Comparator.comparing(Integer::intValue).reversed());
        int sum1 = 0;
        for (int j = 0; j < 100; ++j) {
            for (int i = 0; i < l.size(); ++i) {
                sum1 += l.get(i);
            }
        }
        long end1 = System.currentTimeMillis();

//        long start2 = System.currentTimeMillis();
//        int sum2 = 0;
//
//        for (int j = 0; j < 10000; ++j) {
//            for (int i = 0; i < limit2; ++i) {
//                sum2 = sum2 + (Integer) newList[i];
//            }
//        }
//        long end2 = System.currentTimeMillis();
        System.out.format("Time1 = %d (%d)\n", (end1 - start1), sum1);
       // System.out.format("Time2 = %d (%d)\n", (end2 - start2), sum2);
    }
}
