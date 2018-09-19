// Adapted from: https://rosettacode.org/wiki/Jaro_distance#Java

import java.io.*;
import java.util.*;

public class Jaro {
    private static final int LIST_CMP = 0;
    private static final int HASH_CMP = 1;
    private static double total_diff = 0;

    private static JaroWinklerDistance jaroWinkler;
    private static HashMap<String, String> examplesMap = new HashMap<>();
    private static ArrayList<String> examplesList = new ArrayList<>();
    private static Map<Double, String> cmpResults = new TreeMap<Double, String>(Collections.reverseOrder());

    public static double getDistance(String str, String test) {
        int s_len = str.length();
        int t_len = test.length();
        if (s_len == 0 && t_len == 0) return 1;

        // make input case insensitive and cast to char arrays
        char[] s = str.toLowerCase().toCharArray();
        char[] t = test.toLowerCase().toCharArray();
        int match_distance = Integer.max(s_len, t_len) / 2 - 1;

        // instantiate boolean matching arrays
        boolean[] s_matches = new boolean[s_len];
        boolean[] t_matches = new boolean[t_len];

        int matches = 0;
        int transpositions = 0;

        // determine matches
        for (int i = 0; i < s.length; i++) {
            int x0 = Integer.max(0, i-match_distance);
            int x1 = Integer.min(i + match_distance + 1, t_len);

            for (int j = x0; j < x1; j++) {
                if (t_matches[j]) continue;
                if (s[i] != t[j]) continue;
                s_matches[i] = true;
                t_matches[j] = true;
                matches++;
                break;
            }
        }

        if (matches == 0) return 0;

        // determine transpositions
        int k = 0;
        for (int i = 0; i < s_len; i++) {
            if (!s_matches[i]) continue;
            while (!t_matches[k]) k++;
            if (s[i] != t[k]) transpositions++;
            k++;
        }

        return (((double)matches / s_len) +
                ((double)matches / t_len) +
                (((double)matches - transpositions/2.0) / matches)) / 3.0;
    }

    public static void compareJaroAndWinkler(String s1, String s2) {
        // make case insensitive
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

        long startTime = System.nanoTime();
        double d1 = getDistance(s1, s2);
        long endTime = System.nanoTime();
        double jaroTime = (endTime - startTime)/1000000.0;

        startTime = System.nanoTime();
        double d2 = jaroWinkler.apply(s1, s2);
        endTime = System.nanoTime();
        double jaroWinklerTime = (endTime - startTime)/1000000.0;

        double aveScore = (d1 + d2) / 2.0;
        double diff = jaroWinklerTime - jaroTime;
        total_diff += diff;

        String res = String.format("%12s ?= %-12s | %4.2f (%6.5f ms) | %8.2f (%6.5f ms) | %6.5f ms", s1, s2, d1, jaroTime, d2, jaroWinklerTime, diff);
        cmpResults.put(aveScore, res);
    }

    public static void readHashExamples(String filePath) throws IOException {
        File file = new File(filePath);

        BufferedReader br = new BufferedReader(new FileReader(file));

        String st, s1, s2;
        while ((st = br.readLine()) != null) {
            s1 = st.split(",")[0].trim();
            s2 = st.split(",")[1].trim();
            examplesMap.put(s1, s2);
        }
    }

    public static void readListExamples(String filePath) throws IOException {
        File file = new File(filePath);

        BufferedReader br = new BufferedReader(new FileReader(file));

        String st;
        while ((st = br.readLine()) != null) {
            examplesList.add(st.trim());
        }
    }

    public static void initJaroWinkler() {
        jaroWinkler = new JaroWinklerDistance();

        long startTime = System.nanoTime();
        jaroWinkler.apply("", "");
        long endTime = System.nanoTime();
        double initTime = (endTime - startTime)/1000000.0;
        System.out.println(String.format("Inital JW call: %s\n", initTime));
    }


    public static void main(String[] args) throws IOException {
        // read examplesMap
        int compareType = LIST_CMP; // could get as input
        System.out.println("Please enter name to test: ");

        Scanner scanner = new Scanner(System.in);
        String test_str = scanner.next();

        // init JWD
        initJaroWinkler();

        System.out.println(String.format("%-28s | %-17s | %-17s | %10s", "Compare", "Jaro", "Jaro-Winkler (Apache)", "Time diff (JW-J)"));
        System.out.println("--------------------------------------------------------------------------------------------");

        if (compareType == LIST_CMP) {
            readListExamples("test01.txt");

            // compare input to list
            for (String s : examplesList) {
                compareJaroAndWinkler(test_str, s);
            }

        } else if (compareType == HASH_CMP) {
            readHashExamples("test01.txt");

            // test examplesMap
            for (HashMap.Entry<String, String> entry : examplesMap.entrySet())
            {
                compareJaroAndWinkler(entry.getKey(), entry.getValue());
            }
        }

        // print ordered results
        int top_n = 3;

        int i = 0;
        for (Map.Entry<Double, String> entry : cmpResults.entrySet()) {
            if (i < top_n) {
                System.out.println(entry.getValue());
                i++;
            } else {
                break;
            }
        }

        // ave time diff
        double ave_diff = total_diff/(double) cmpResults.size();
        System.out.println("--------------------------------------------------------------------------------------------");
        System.out.println(String.format("%72s | %6.5f ms", "Ave time diff", ave_diff));

        System.out.println("\n(Notice that the top results for close matching JW vary more than that of normal Jaro.\nThis allows more confidence in match selection.)");
    }
}