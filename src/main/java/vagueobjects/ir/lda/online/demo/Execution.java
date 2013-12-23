package vagueobjects.ir.lda.online.demo;
/*
Copyright (c) 2013 miberk

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
"Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

import org.apache.commons.io.IOUtils;
import vagueobjects.ir.lda.tokens.Documents;
import vagueobjects.ir.lda.online.OnlineLDA;
import vagueobjects.ir.lda.online.Result;
import vagueobjects.ir.lda.tokens.PlainVocabulary;
import vagueobjects.ir.lda.tokens.Vocabulary;

import java.io.*;
import java.util.*;
import java.util.List;

public class Execution {
 
    public static void main(String[] args) throws Exception{ 
        if(args.length<0){
            System.out.println("please provide paths for document directory and dictionary file");
            return;
        }
        String docPath= args[0];
        String dictPath = args[1];
  

        int D=  10800;
        int K = 100;
        int batchSize= 1024;

        double tau =  1d;
        double kappa =  0.8d;

        double alpha = 1.d/K;
        double eta = 1.d/K;

        Vocabulary vocabulary = new PlainVocabulary( dictPath);
        OnlineLDA lda = new OnlineLDA(vocabulary.size(),K, D, alpha, eta, tau, kappa);
        List<String> docs = readDocs(docPath);
        long delta=0;

        for(int i=0; i*batchSize < docs.size();++i){
            int max = Math.min((i+1)*batchSize, docs.size());
            Documents documents = new Documents(docs.subList(i * batchSize, max), vocabulary);
            Result result = lda.workOn(documents);
            System.out.println(result);
        }
        System.out.println("Time " + delta/1000);
    }
 
    static List<String> readDocs(String path) throws IOException{

        List<String> strings = new ArrayList<String>();
        File dir = new File(path);
        for(File f :  dir.listFiles()){
            InputStream is = new FileInputStream(f);
            StringWriter writer = new StringWriter();
            IOUtils.copy(is, writer);
            strings.add(writer.toString());
            is.close();

        }
        return strings;
    }

}
