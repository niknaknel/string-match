import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Compare {

    private static final int JARO = 0;
    private static final int JARO_WINK = 1;
    private static final int JARO_WINK_CHAR = 2;
    private static final String[] algNames = {"JARO", "JARO_WINK", "JARO_WINK_CHAR"};
    private static ArrayList<String> examplesList = new ArrayList<>();

    public static TreeMap<Double, String> search_all(String target, int alg) {
        TreeMap<Double, String> results = new TreeMap<>(Collections.reverseOrder());
        JaroWinklerDistance jaroWinkler = new JaroWinklerDistance();
        double timeElapsed = 0;

        if (alg == JARO) {
            long startTime = System.nanoTime();
            for (String s : examplesList) {
                double distance = Jaro.getDistance(target.toLowerCase(), s.toLowerCase());
                results.put(distance, s);
            }
            long endTime = System.nanoTime();
            timeElapsed = (endTime - startTime)/1000000.0;

        } else if (alg == JARO_WINK) {
            long startTime = System.nanoTime();
            for (String s : examplesList) {
                double distance = jaroWinkler.apply(target.toLowerCase(), s.toLowerCase());
                results.put(distance, s);
            }
            long endTime = System.nanoTime();
            timeElapsed = (endTime - startTime)/1000000.0;

        } else if (alg == JARO_WINK_CHAR) {
            long startTime = System.nanoTime();
            for (String s : examplesList) {
                double distance = jaroWinkler.applyCharArray(target.toLowerCase(), s.toLowerCase());
                results.put(distance, s);
            }
            long endTime = System.nanoTime();
            timeElapsed = (endTime - startTime)/1000000.0;
        }

        System.out.println(String.format("Searching %d names took %-7.5f ms (%s)", examplesList.size(), timeElapsed, algNames[alg]));
        return getTopN(results, 3);
    }

    public static TreeMap<Double, String> getTopN(TreeMap<Double, String> results, int n) {
        TreeMap<Double, String> top = new TreeMap<>(Collections.reverseOrder());
        int  i = 0;
        for (Map.Entry<Double, String> entry : results.entrySet()) {
            if (i < n) {
                top.put(entry.getKey(), entry.getValue());
                i++;
            } else {
                break;
            }
        }
        return top;
    }

    public static void printTree(String target, TreeMap<Double, String> tree) {
        System.out.println("***************************************");
        System.out.println(String.format("%-28s | %-12s", "String", "Match %"));
        System.out.println("---------------------------------------");
        for (Map.Entry<Double, String> entry : tree.entrySet()) {
            System.out.println(String.format("%12s ?= %-12s | %6.2f |", target, entry.getValue(), entry.getKey()));
        }
        System.out.println("---------------------------------------\n");
    }

    public static void readListExamples(String filePath) throws IOException {
        File file = new File(filePath);

        BufferedReader br = new BufferedReader(new FileReader(file));

        String st;
        while ((st = br.readLine()) != null) {
            examplesList.add(st.trim());
        }
    }

    public static void main(String[] args) throws IOException {
        TreeMap<Double, String> results;

        // get input
        System.out.println("Please enter name to test: ");
        Scanner scanner = new Scanner(System.in);
        String test_str = scanner.next();

        // read in test file
        readListExamples("./test_files/151k.txt");

        // test JARO
        results = search_all(test_str, JARO);
        printTree(test_str, results);

        // test JARO_WINK
        results = search_all(test_str, JARO_WINK);
        printTree(test_str, results);

        // test JARO_WINK_CHAR
        results = search_all(test_str, JARO_WINK_CHAR);
        printTree(test_str, results);
    }
}
