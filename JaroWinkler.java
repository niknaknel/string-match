/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.Arrays;

/**
 * A similarity algorithm indicating the percentage of matched characters between two character sequences.
 *
 * <p>
 * The Jaro measure is the weighted sum of percentage of matched characters
 * from each file and transposed characters. Winkler increased this measure
 * for matching initial characters.
 * </p>
 *
 * <p>
 * This implementation is based on the Jaro Winkler similarity algorithm
 * from <a href="http://en.wikipedia.org/wiki/Jaro%E2%80%93Winkler_distance">
 * http://en.wikipedia.org/wiki/Jaro%E2%80%93Winkler_distance</a>.
 * </p>
 *
 * <p>
 * This code has been adapted from Apache Commons Lang 3.3 as follows:
 *  1) adapted to make use of char[] type instead of CharSequence (for performance increase)
 *  2) class and method names changed from 'JaroWinklerDistance' and 'apply' to 'JaroWinkler'
 *    and 'getScore', respectively.
 *  3) arguments changed from CharSequence to String
 * </p>
 *
 * @since 1.0
 */
public class JaroWinkler {

    /**
     * Find the Jaro Winkler Distance which indicates the similarity score
     * between two Strings.
     *
     * @param left_str the first String, must not be null
     * @param right_str the second String, must not be null
     * @return resulting score i.e. match percentage
     * @throws IllegalArgumentException if either String input is {@code null}
     */
    public static Double getScore(final String left_str, final String right_str) {
        final double defaultScalingFactor = 0.1;

        if (left_str == null || right_str == null) {
            throw new IllegalArgumentException("CharSequences must not be null");
        }

        char[] left = left_str.toCharArray();
        char[] right = right_str.toCharArray();

        final int[] mtp = matches(left, right);
        final double m = mtp[0];
        if (m == 0) {
            return 0D;
        }
        final double j = ((m / left.length + m / right.length + (m - mtp[1]) / m)) / 3;
        final double jw = j < 0.7D ? j : j + Math.min(defaultScalingFactor, 1D / mtp[3]) * mtp[2] * (1D - j);
        return jw;
    }

    /**
     * This method returns the Jaro-Winkler string matches, transpositions, prefix, max array.
     *
     * @param first the first string (char[]) to be matched
     * @param second the second string (char[]) to be matched
     * @return mtp array containing: matches, transpositions, prefix, and max length
     */
    protected static int[] matches(final char[] first, final char[] second) {
        char[] max, min;
        if (first.length > second.length) {
            max = first;
            min = second;
        } else {
            max = second;
            min = first;
        }
        final int range = Math.max(max.length / 2 - 1, 0);
        final int[] matchIndexes = new int[min.length];
        Arrays.fill(matchIndexes, -1);
        final boolean[] matchFlags = new boolean[max.length];
        int matches = 0;
        for (int mi = 0; mi < min.length; mi++) {
            final char c1 = min[mi];
            for (int xi = Math.max(mi - range, 0), xn = Math.min(mi + range + 1, max.length); xi < xn; xi++) {
                if (!matchFlags[xi] && c1 == max[xi]) {
                    matchIndexes[mi] = xi;
                    matchFlags[xi] = true;
                    matches++;
                    break;
                }
            }
        }
        final char[] ms1 = new char[matches];
        final char[] ms2 = new char[matches];
        for (int i = 0, si = 0; i < min.length; i++) {
            if (matchIndexes[i] != -1) {
                ms1[si] = min[i];
                si++;
            }
        }
        for (int i = 0, si = 0; i < max.length; i++) {
            if (matchFlags[i]) {
                ms2[si] = max[i];
                si++;
            }
        }
        int transpositions = 0;
        for (int mi = 0; mi < ms1.length; mi++) {
            if (ms1[mi] != ms2[mi]) {
                transpositions++;
            }
        }
        int prefix = 0;
        for (int mi = 0; mi < min.length; mi++) {
            if (first[mi] == second[mi]) {
                prefix++;
            } else {
                break;
            }
        }
        return new int[] {matches, transpositions / 2, prefix, max.length};
    }
}
