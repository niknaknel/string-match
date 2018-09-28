# string-match
Sandbox repo for testing Jaro-Winkler.

### So far...
* I've determined that for matching names, JW is more suitable than Jaro since it uses the fact that names tend to have more difference in the end of the string than in the beginning.

* I've observed that JW usually has more variation in its top 3 scores, meaning that the confidence with which we may choose the top results is greater than that of normal Jaro. JW is especially accurate for **alias detection** since it accounts for **mispellings** and **spelling variations**.

* Busy collecting timing statistics. So far I couldn't identify any meaningful trends. However, the JW algorithm may take anywhere from 1-6.5 times as long as normal Jaro.
