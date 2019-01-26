# java-information-retrieval-project
This project is a demonstration for a information retrieval tool. 

Textbank is the folder containing the medline files. </br>
These files are passed through the following steps: </br>
1. Stopwords remover based on a stoplist file. It generates a .stp file. </br>
2. Stemmer using the proter algorithym. It generates a .stem file </br>
3. A tfidf generation algorythim that stores the values in a inverted file. 
&nbsp&nbsp;The inverted file has a custom JSON data structure. 
`{`</br>
 &nbsp;&nbsp;&nbsp;`"word":{`</br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`"1(document frequency)":{`</br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`"doc1.stp":0.01(tfidf values)`</br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; `}`</br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; `}`</br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; `}`</br>
4.A cosine value generation and document retrieval tool.
&nbsp;&nbsp;These are the top three results for the following query on the medline collection: </br>  `the` `crystallin` `len` `vertebr` `includ` `human`</br></br></br>
&nbsp; `Document Name : doc72.stp and cosine value : 0.357`</br>
&nbsp; `Document Name : doc500.stp and cosine value : 0.284`</br>
&nbsp; `Document Name : doc965.stp and cosine value : 0.265`</br>

5. A precision and recall calculation tool. 
    These are the graphs showing the values of the precision and recall for the first query results of the medline collection. 
    
 ## Precsion and Recall </br>
 ![PrecisionAndRecall](https://github.com/SamerDiab/java-information-retrieval-project/blob/master/precision%20and%20recall%20graph.png)
    
 ## Average precision per recall</br>
  ![AvgPrecisionAndRecall](https://github.com/SamerDiab/java-information-retrieval-project/blob/master/average%20precision%20and%20recall%20curve.png)

</br>

The code performs well on older machines, averaging on 28 seconds for the entire process. The tool is still a work in progress. It will be optimized. </br>

With the custom json data structure, we can convert this project into a firebase project for optimizing performance speed. </br>
The code utilizes multiple coding concepts. Like multithreading, concurrent skip list maps, hashmaps, hastables and iterators.</br>
