# string-match
Sandbox repo for testing Jaro-Winkler.

### So far...
* I've determined that for matching names, JW is more suitable than Jaro since it uses the fact that names tend to have more difference in the end of the string than in the beginning.

* I've observed that JW with char[] instead of CharSequence is generally faster and scales well! It searches 151k strings in apprx 0.05 to 0.15 seconds (shorter string length = shorter search time). Even for ridiculously long strings the upper bound is roughly 0.15s.
