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

import com.google.gson.Gson;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Samer Diab
 */
public class FrequencySummary {

    public void getSummary(String json, List<String> docName, List<String> queryWords) {
        //input document name list

        //json string
        //  String jsonString = "{\"hello\":{\"1 - Copy.stp\":1},\"plai\":{\"1 - Copy.stp\":4,\"1.stp\":3},\"play\":{\"1 - Copy.stp\":1,\"1.stp\":1},\"player\":{\"1 - Copy.stp\":3,\"1.stp\":2},\"univers\":{\"1 - Copy.stp\":3,\"1.stp\":3}}";
        generateSummary(docName, json, queryWords);
    }

    private void generateSummary(List<String> documentNameList, String text, List<String> queryWords) {
        Map<String, Map<Integer, Map<String, Double>>> output = new HashMap<>();
        BufferedWriter docWriterOutput = null;
        try {
            //if document name list is missing then return
            if (documentNameList == null || documentNameList.isEmpty()) {
                return;
            }   //total documents
            int totalDocuments = documentNameList.size();
           
            Type type = new TypeToken<Map<String, Map<String, Integer>>>() {
            }.getType();
            Gson gson = new Gson();
            Map<String, Map<String, Integer>> input = gson.fromJson(text, type);
          //  System.out.println(gson.toJson(input));
            
            Iterator wordIterator = input.entrySet().iterator();
            //traversing each word
            while (wordIterator.hasNext()) {
                Map.Entry wordPair = (Map.Entry) wordIterator.next();
                //word
                String word = (String) wordPair.getKey();
                //if word is null or empty then we are skipping the word
                if(word == null || word.isEmpty())
                {
                    continue;
                }
                //document name with occurence for the word
                Map<String, Integer> documentFrequency = (Map<String, Integer>) wordPair.getValue();
                int docFreq = 0;
                //int occurence = 0;
                //document name list where the word exists
                List<String> wordInDocumentList = new ArrayList<>();
                //traversing each document name with frequency
                Iterator documentFrequencyIterator = documentFrequency.entrySet().iterator();
                Map<String, Double> documentNameToValue = new HashMap<>();
                int documentLength = documentFrequency.size();
                while (documentFrequencyIterator.hasNext()) {
                    Map.Entry documentFrequencyPair = (Map.Entry) documentFrequencyIterator.next();
                    String documentName = (String) documentFrequencyPair.getKey();
                    int frequency = (Integer) documentFrequencyPair.getValue();
                    
                    if (!wordInDocumentList.contains(documentName)) {
                        wordInDocumentList.add(documentName);
                    }
                    docFreq++;
                    //occurence += frequency;
                    
                    //calculating tf and tfidf
                    int tf = frequency;
                    
                    double tfidf = tf * Math.log10((Math.round((1.0 * totalDocuments) / documentLength * 10.0) / 10.0));
                    documentNameToValue.put(documentName, tfidf);
                }
                
                //int tf = occurence;
                //double tfidf = tf * Math.log(( Math.round((1.0 * totalDocuments) / docFreq * 10.0)/10.0 ));
                Map<String, Double> docToValue = new HashMap<>();
                for (String docName : documentNameList) {
                    double value = 0;
                    //if the document name has frequency then we are including the value otherwise it will be 0
                    if (documentNameToValue.containsKey(docName)) {
                        value = documentNameToValue.get(docName);
                    }
                    docToValue.put(docName, value);
                }

                Map<Integer, Map<String, Double>> freqToDocValue = new HashMap<>();
                freqToDocValue.put(docFreq, docToValue);
                output.put(word, freqToDocValue);
            }  
            //System.out.println(gson.toJson(output));
            docWriterOutput = new BufferedWriter(new FileWriter("invertedFile.txt"));
            docWriterOutput.append(gson.toJson(output));
            
        } catch (IOException ex) {
            Logger.getLogger(FrequencySummary.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if(docWriterOutput != null)
                {
                    docWriterOutput.close();
                }
            } catch (Exception ex) {
                Logger.getLogger(FrequencySummary.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //generating cosine similarity with inverted values
        CosineSummary cosineSummary = new CosineSummary();
        cosineSummary.calculateCosineScores(output, queryWords);
    }
}
