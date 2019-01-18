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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;


public class InformationRetreival {

    /**
     * @param args the command line arguments
     */
    
    private static int totalFiles = 0;
    private static int totalReadFiles = 0;
    
    private static int counter=0;
    private static int tmp =0;
     private static Map<String,Map<String,Integer>> wor;
        private static List<String> docName =  new ArrayList<>();
    public static void main(String[] args) throws FileNotFoundException, IOException {
        // TODO code application logic here
        
    
        File textBank = new File("textbank/");
        Hashtable<Integer,Integer> frequency;
        Hashtable<Integer, String> table;
        table = new Hashtable<>();
        frequency = new Hashtable<>();
        Scanner input = new Scanner(new File("stoplist.txt"));
        int max=0;
        while (input.hasNext()) {
            String next;
            next = input.next();
            if(next.length()>max)
            max=next.length();
            table.put(next.hashCode(), next); //to set up the hashmap with the wordsd
               
        }
        
        System.out.println(max);
        
        Pattern p0=Pattern.compile("[\\,\\=\\{\\}\\\\\\\"\\_\\+\\*\\#\\<\\>\\!\\`\\-\\?\\'\\:\\;\\~\\^\\&\\%\\$\\(\\)\\]\\[]")  
                ,p1=Pattern.compile("[\\.\\,\\@\\d]+(?=(?:\\s+|$))");
        
      
        
     wor = new ConcurrentSkipListMap<>();
        
       ConcurrentMap<String , Map<String,Integer>> wordToDoc = new ConcurrentMap<String, Map<String, Integer>>() {
            @Override
            public Map<String, Integer> putIfAbsent(String key, Map<String, Integer> value) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public boolean remove(Object key, Object value) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public boolean replace(String key, Map<String, Integer> oldValue, Map<String, Integer> newValue) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Map<String, Integer> replace(String key, Map<String, Integer> value) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public int size() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public boolean isEmpty() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public boolean containsKey(Object key) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public boolean containsValue(Object value) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Map<String, Integer> get(Object key) {
                return wor.get((String)key);//To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Map<String, Integer> put(String key, Map<String, Integer> value) {
                   wor.put(key, value);  // this saving me n    
                 //if(frequency.get(key.hashCode())== null)
                  
                  //frequency.put(key.hashCode(), )
                return null;
            }

            @Override
            public Map<String, Integer> remove(Object key) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void putAll(Map<? extends String, ? extends Map<String, Integer>> m) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void clear() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Set<String> keySet() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Collection<Map<String, Integer>> values() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Set<Map.Entry<String, Map<String, Integer>>> entrySet() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
        totalFiles = textBank.listFiles().length;
        for (File textFile : textBank.listFiles()) {
            totalReadFiles++;
            //checking whether file has txt extension and file size is higher than 0
            if(textFile.getName().contains(".txt") && textFile.length() > 0){
                docName.add(textFile.getName().replace(".txt", ".stp"));
            StopListHandler sp = new StopListHandler(textFile, table,System.currentTimeMillis(),p0,p1,wordToDoc);
            sp.setFinished(() -> {
                 
              tmp++;
             
             if(tmp==counter)
               //printTable(counter);
             
             if(totalReadFiles == totalFiles)
             {
                 printTable(counter);
             }
        
            });
            
            sp.start();
            counter ++;}
            
            
        }
        System.out.println(counter);
        //while(true){if(tmp==counter-1) break;}
            System.out.println("s");
           
    }

    private static void printTable(int numberOfDocs) {
      //  AtomicInteger freq = new AtomicInteger(0);
      //  AtomicInteger df = new AtomicInteger(0);
        
       Gson gs = new Gson();
        String json =  gs.toJson(wor);
     //   System.out.println(json);
        
        List<String> queryWords = new ArrayList();
        //to test it for other query word you can change the following two words
        //queryWords.add("carpet");
        //queryWords.add("hous");
        queryWords.add("the");
        queryWords.add("crystallin");
        queryWords.add("len");
        queryWords.add("vertebr");
        queryWords.add("includ");
        
        
          FrequencySummary frequencySummary = new FrequencySummary();
        frequencySummary.getSummary(json,docName, queryWords);
        
        System.exit(0);
        
        Hashtable<Integer,Integer> g = new Hashtable<>();
                
      /*  wor.entrySet().forEach((wordToDocument) -> {
            String currentWord = wordToDocument.getKey();
            Map<String, Integer> documentToWordCount = wordToDocument.getValue();
            freq.set(0);
            df.set(0);
            documentToWordCount.entrySet().forEach((documentToFrequency) -> {
                String document = documentToFrequency.getKey();
                Integer wordCount = documentToFrequency.getValue();
                freq.addAndGet(wordCount);
                System.out.println("Word " + currentWord + " found " + wordCount +
                        " times in document " + document);
             
                if(g.getOrDefault(currentWord.hashCode(), null)==null){
                    g.put(currentWord.hashCode(),1);
                
                }else {
                     System.out.println("Hello");
                     
                    int i = g.get(currentWord.hashCode());
                      System.out.println("i "+i);
                    g.put(currentWord.hashCode(), i++);
                }
           //     System.out.println(currentWord+" "+ g.get(currentWord.hashCode()));
               // g.put(currentWord.hashCode(), g.getOrDefault(currentWord.hashCode(), 0)+wordCount);
            
            });
            //   System.out.println(freq.doubleValue());
               
           // System.out.println("IDF for this word: "+Math.log10( (double)(counter/freq.doubleValue())));
        });
       // System.out.println(g.get("plai".hashCode()));
        //System.out.println("IDF for this word: "+Math.log10( (double)(counter/(double)g.get("plai".hashCode()))));
           */
    }

}