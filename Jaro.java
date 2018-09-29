// Adapted from: https://rosettacode.org/wiki/Jaro_distance#Java

public class Jaro {

    public static double getScore(String str, String test) {
        int s_len = str.length();
        int t_len = test.length();
        if (s_len == 0 && t_len == 0) return 1;

        // make input case insensitive and cast to char arrays
        char[] s = str.toCharArray();
        char[] t = test.toCharArray();
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
}