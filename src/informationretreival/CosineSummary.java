/* 
 * Copyright 2019 Samer Diab.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package informationretreival;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Samer Diab
 */
public class CosineSummary {
    /**
     * This method will calculate cosine values for each document based on give query
     * @param input
     * @param queryWords
     */
    public void calculateCosineScores(Map<String, Map<Integer, Map<String, Double>>> input, List<String> queryWords)
    {
        System.out.println("Genrating cosine similarities....");
        
        //total number of documents
        int totalDocuments = 0;
        //document name to cosine score map
        HashMap<String, Double> docToCosineScore = new HashMap<>();
        //word to document frequency
        HashMap<String, Integer> wordToDocFreq = new HashMap<>();
        //query word to score(tj)
        HashMap<String, Double> queryWordToScore = new HashMap<>();
        //document name to -> word to score(ti)
        HashMap<String, HashMap<String, Double>> docToWordToScore = new HashMap<>();
        
        //populating word to score for each document
        Iterator inputIterator = input.entrySet().iterator();
        while (inputIterator.hasNext()) {
            Map.Entry wordPair = (Map.Entry) inputIterator.next();
            //word
            String word = (String) wordPair.getKey();
            Map<Integer, Map<String, Double>> freqToDocValuesMap = (Map<Integer, Map<String, Double>>) wordPair.getValue();
            Iterator freqToDocValuesMapIterator = freqToDocValuesMap.entrySet().iterator();
            while (freqToDocValuesMapIterator.hasNext()) {
                Map.Entry docPair = (Map.Entry) freqToDocValuesMapIterator.next();
                //document frequency of word
                int frequency = (Integer) docPair.getKey();
                if(!wordToDocFreq.containsKey(word))
                {
                    wordToDocFreq.put(word, frequency);
                }
                Map<String, Double> docToValuesMap = (Map<String, Double>) docPair.getValue();
                Iterator docIterator = docToValuesMap.entrySet().iterator();
                while (docIterator.hasNext()) {
                    Map.Entry valuePair = (Map.Entry) docIterator.next();
                    String docName = (String) valuePair.getKey();
                    double score = (Double) valuePair.getValue();
                    if(!docToWordToScore.containsKey(docName))
                    {
                        HashMap<String, Double> wordToScore = new HashMap<>();
                        wordToScore.put(word, score);
                        docToWordToScore.put(docName, wordToScore);
                    }
                    else
                    {
                        docToWordToScore.get(docName).put(word, score);
                    }
                }
            }
        }
        
        //populating word score for query words
        totalDocuments = docToWordToScore.size();
        Iterator wordIToDocFreqIterator = wordToDocFreq.entrySet().iterator();
        while (wordIToDocFreqIterator.hasNext()) {
            Map.Entry wordPair = (Map.Entry) wordIToDocFreqIterator.next();
            //word
            String word = (String) wordPair.getKey();
            int frequency = (Integer) wordPair.getValue();
            double score = 0;
            if(queryWords.contains(word))
            {
                score = Math.log10((Math.round((1.0 * totalDocuments) / frequency * 1000.0) / 1000.0));
            }            
            queryWordToScore.put(word, score);
        }
        
        //generating cosine scores for each word
        Iterator docToWordToScoreIterator = docToWordToScore.entrySet().iterator();
        while (docToWordToScoreIterator.hasNext()) {
            Map.Entry wordPair = (Map.Entry) docToWordToScoreIterator.next();
            //word
            String docName = (String) wordPair.getKey();
            HashMap<String, Double> docWordToScore = (HashMap<String, Double>) wordPair.getValue();
            double numerator = 0;
            double temp1 = 0;
            double temp2 = 0;
            Iterator docWordToScoreIterator = docWordToScore.entrySet().iterator();
            while (docWordToScoreIterator.hasNext()) {
                Map.Entry docWordPair = (Map.Entry) docWordToScoreIterator.next();
                //word
                String word = (String) docWordPair.getKey();
                double ti = (Double) docWordPair.getValue();
                double tj = queryWordToScore.get(word);
                numerator += ti * tj;
                temp1 += ti*ti;
                temp2 += tj*tj;                
            }
            double denominator = Math.sqrt(temp1 * temp2);
            double cosineScore = 0;
            if(denominator > 0)
            {
                cosineScore = (Math.round((1.0 * numerator) / denominator * 1000.0) / 1000.0);
            }
            if(cosineScore > 0)
            {
                docToCosineScore.put(docName, cosineScore);
            }            
        }
        List<String> rankedDocuments = new ArrayList<>();
        System.out.println("Printing cosine similarities....");
        //sorting document name to cosine score
        docToCosineScore = sortByValue(docToCosineScore);
        Iterator docToCosineScoreIterator = docToCosineScore.entrySet().iterator();
        while (docToCosineScoreIterator.hasNext()) {
            Map.Entry wordPair = (Map.Entry) docToCosineScoreIterator.next();
            //word
            String docName = (String) wordPair.getKey();
            double score = (Double) wordPair.getValue();
            System.out.println("Document Name : " + docName + " and cosine value : " + score);
            rankedDocuments.add(docName);
        }
        //calculating precision and recall
        calcualtePrecisionRecall(rankedDocuments);
    }
    
    /**
     * This method will sort HashMap by value
     * @param unsortMap hash map to be sorted
     * @return HashMap sorted hash map
     */
    private static HashMap<String, Double> sortByValue(HashMap<String, Double> unsortMap) {

        // 1. Convert Map to List of Map
        List<HashMap.Entry<String, Double>> list
                = new LinkedList<HashMap.Entry<String, Double>>(unsortMap.entrySet());

        // 2. Sort list with Collections.sort(), provide a custom Comparator
        //    Try switch the o1 o2 position for a different order
        Collections.sort(list, new Comparator<HashMap.Entry<String, Double>>() {
            public int compare(HashMap.Entry<String, Double> o1,
                    HashMap.Entry<String, Double> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        // 3. Loop the sorted list and put it into a new insertion order Map LinkedHashMap
        HashMap<String, Double> sortedMap = new LinkedHashMap<String, Double>();
        for (HashMap.Entry<String, Double> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }
    
    /**
     * This method will calculate precision and recall of ranked documents with greater than 0 cosine value for each doc
     * @param rankedDocuments ranked documents
     */
    public void calcualtePrecisionRecall(List<String> rankedDocuments)
    {
        String[] queryFileNames = getQueryFileNames();
        //converting array to list
        List<String> queryDocNameList = new ArrayList<>(Arrays.asList(queryFileNames));
        List<Boolean> rankedDocumentStatus = new ArrayList<>();
        int relevant = 0;
        //setting flag to denote whether a document is in relevant retrived or not for each document
        for(String documentName : rankedDocuments)
        {
            if(queryDocNameList.contains(documentName))
            {
                rankedDocumentStatus.add(true);
                relevant++;
            }
            else
            {
                rankedDocumentStatus.add(false);
            }
        }
        LinkedHashMap<Double, List<Double>> recallToPrecisionList = new LinkedHashMap<>();
        double relevantRetrivedCounter = 0;
        double retrivedCounter = 0;
        List<Double> precisionList = new ArrayList<>();
        List<Double> recallList = new ArrayList<>();
        //generating precision list, recall list and precision list for each recall
        for(boolean isRelevantRetrived : rankedDocumentStatus)
        {
            retrivedCounter++;
            if(isRelevantRetrived)
            {
                relevantRetrivedCounter++;
            }
            double precision = (Math.round((1.0 * relevantRetrivedCounter) / retrivedCounter * 100.0) / 100.0);
            double recall = (Math.round((1.0 * relevantRetrivedCounter) / relevant * 100.0) / 100.0);
            precisionList.add(precision);
            recallList.add(recall);
            List<Double> precisions = new ArrayList<>();
            if(recallToPrecisionList.containsKey(recall))
            {
                precisions = recallToPrecisionList.get(recall);
            }
            precisions.add(precision);
            recallToPrecisionList.put(recall, precisions);
        }
        
        System.out.println("Printing precisions:");
        for(Double precision: precisionList)
        {
            System.out.println(precision);
        }
        System.out.println("Printing recalls:");
        for(Double recall: recallList)
        {
            System.out.println(recall);
        }
        
        System.out.println("Printing recall to average precision:");
        Iterator recallToPrecisionListIterator = recallToPrecisionList.entrySet().iterator();
        while (recallToPrecisionListIterator.hasNext()) {
            Map.Entry pair = (Map.Entry) recallToPrecisionListIterator.next();
            Double recall = (Double) pair.getKey();
            List<Double> precisions = (List<Double>) pair.getValue();
            double totalPrecision = 0;
            double counter = 0;
            for(Double precision: precisions)
            {
                totalPrecision += precision;
                counter++;
            }
            double avgPrecision = 0;
            if(counter > 0)
            {
                avgPrecision = (Math.round((1.0 * totalPrecision) / counter * 100.0) / 100.0);
            }
            System.out.println(recall + " " + avgPrecision);
        }
    }
    
    /**
     * This method will return query document list
     * @return String[] doc name in array
     */
    public String[] getQueryFileNames()
    {
        String[] st;
        st = new String[]
        {
            "doc13.stp",
            "doc14.stp",
            "doc15.stp",
            "doc72.stp",
            "doc79.stp",
            "doc138.stp",
            "doc142.stp",
            "doc164.stp",
            "doc165.stp",
            "doc166.stp",
            "doc167.stp",
            "doc168.stp",
            "doc169.stp",
            "doc170.stp",
            "doc171.stp",
            "doc172.stp",
            "doc180.stp",
            "doc181.stp",
            "doc182.stp",
            "doc184.stp",
            "doc185.stp",
            "doc186.stp",
            "doc211.stp",
            "doc212.stp",
            "doc499.stp",
            "doc500.stp",
            "doc501.stp",
            "doc502.stp",
            "doc504.stp",
            "doc506.stp",
            "doc507.stp",
            "doc508.stp",
            "doc510.stp",
            "doc522.stp",
            "doc513.stp"
        };
        return st;
    }
}
