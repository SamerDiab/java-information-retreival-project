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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Writer;
import java.util.Hashtable; 
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.tartarus.snowball.ext.PorterStemmer;

/**
 *
 * @author Samer Diab
 */
public class StopListHandler implements Runnable {

    final Hashtable<Integer, String> table;
    private Thread loader;
   
    private String outFileName,stratMessage = "Thread is handling " , doneMessage = "Outputed file  ", threadName="stoplist";
    public String next;
    String stem;
    private int wordsRemoved=0;
    private long startTime, threadTime;
    
    private LineNumberReader az;
    private Pattern p0,p1;
    private PorterStemmer porter;
            int lines=0;

    File doc; //document to handle
    Scanner docInput;
    BufferedWriter docWriterOutput, docstemoutput;
    private ThreadFinishedListener finished;

    public void setFinished(ThreadFinishedListener finished) {
        this.finished = finished;
    }
    private ConcurrentMap<String,Map<String,Integer>> wordToDoc;
   Map<String,Integer>  documentToCount;
    String currentDocumentName;
   
    int count;
    public int count(){
     return count;
}
    StopListHandler(File doc, Hashtable<Integer, String> table,long time, Pattern p0,Pattern p1, ConcurrentMap<String , Map<String,Integer>> wordToDoc) throws FileNotFoundException, IOException {
        this.table = table;
        this.doc = doc;
        stratMessage += doc.getName();
        docInput = new Scanner(doc);
        outFileName  = doc.getName().replace(".txt", ".stp");
        String outName = "textbank/"+outFileName;
        this.wordToDoc = wordToDoc;
        docWriterOutput = new BufferedWriter(new FileWriter(outName)); 
        docstemoutput =new BufferedWriter( new FileWriter(outName.replace(".stp", ".stem")));
        
        currentDocumentName = outFileName.replace(".txt","");
        
        
        this.startTime = time;
        this.p0=p0;
        this.p1=p1;
       
        porter = new PorterStemmer();
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        
    }

    @Override
    public void run() {
   
        try {
            while (docInput.hasNext()) {
                
                 next = p1.matcher(p0.matcher(docInput.next()).replaceAll("")).replaceAll("").toLowerCase();
                  // if(!(next.length()>10))
                if (!table.containsKey(next.hashCode()) && !next.isEmpty()){
                   
                   
                    docWriterOutput.append(next);
                    docWriterOutput.newLine();
                    
                    //docstemoutput.append(next); Uncomment for testing
                    //  docstemoutput.newLine();
                    porter.setCurrent(next);
                    porter.stem();
                    next = porter.getCurrent().replaceAll("[^a-zA-Z0-9]", "");
                    //if(next.length()>1){
                    docstemoutput.append(next);
                    docstemoutput.newLine(); 
                   
                    
                    documentToCount = wordToDoc.get(next);
                    
                    
                   if(documentToCount == null) {
   
                      documentToCount = new ConcurrentSkipListMap<>();
                         wordToDoc.put(next, documentToCount);
                }
                    Integer currentCount = documentToCount.get(currentDocumentName);
                       if(currentCount == null) {
                            currentCount = 0;}
                       documentToCount.put(currentDocumentName, currentCount + 1);
       
                   
                //}
                    lines++;
                }else
                wordsRemoved++;
                

            }//end of while loop
               
            docWriterOutput.close();
            docstemoutput.close();
            threadTime = System.currentTimeMillis();
            long n = (threadTime-startTime);
            
            System.out.println(doneMessage+outFileName+"\n"+"total stop words removed "+wordsRemoved+
                    "\n"+"Time taken for this thread is: "+ n);
            
            System.out.println("Lines "+lines);
            
            finished.threadListener();
            
        } catch (IOException ex) {
            Logger.getLogger(StopListHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void start() {
        System.out.println(threadName + " is loading\n"+stratMessage);
        
        loader = new Thread(this, threadName);
        loader.start();
          
    }
  
}
