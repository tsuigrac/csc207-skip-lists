Skip Lists Assignment
=====================
By: Grace Tsui

In this assignment, we implemented skip lists from a research paper and then tested its time efficiency.

NOTE: I followed the reading and decided to make 16 the max height instead of the initial height. I wrote my program accordingly so no new nodes would exceed this height. 

Sources:
How to measure time in a program execution:
https://stackoverflow.com/questions/180158/how-do-i-time-a-methods-execution-in-java


Time Testing Results (in nanoseconds)

| Size    | Set       | Get       | Remove  |
| --------|-----------|-----------|---------|
|1	      |335651	    |67942	    |86465    |
|10	      |355701	    |382059	    |403441   |
|100	    |12512908	  |1099864	  |453776   |
|1000	    |40870078	  |30338224	  |5389267  |
|2500	    |96188862	  |82112705	  |60093709 |
|5000	    |280853062	|383038709	|76026247 |
|7000	    |635443748	|603197886	|107968180|
|10000	  |1315733404	|1184456872	|106863119|


Overall, I found that it was logarithmic for all three methods in the beginning but then turned linear or exponential. This may be because I decided to make 16 the max height instead of making it expandable, so when the array had too many elements it would have been slower because the nodes were shorter. 


Here is the link to the graphs/data:
https://docs.google.com/spreadsheets/d/16OwDtKPziMPR3gIxvJEAHVR5IRaHrQt9cwid2vRgkNM/edit?usp=sharing
