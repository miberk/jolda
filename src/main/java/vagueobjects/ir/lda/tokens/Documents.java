package vagueobjects.ir.lda.tokens;
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

import java.text.BreakIterator;
import java.util.*;

/**
 * Parses a document into a list of token ids and a list of counts,
 * and builds  document representation as  2D arrays of token ids and counts.
 */
public class Documents {
    private final Vocabulary vocabulary;
    /**
     * wordIds[i][j] gives the jth unique token present in document i
     */
    private int[][] wordIds;
    /**
     * tokenCts[i][j] is the number of times that the token given
     * by wordIds[i][j] appears in document i.
     */
    private int[][] tokenCts;

    public Documents(List<String> docs, Vocabulary vocab) {
        this.vocabulary = vocab;
        build(docs, vocab);
    }

    public Documents(String  doc, Vocabulary vocab) {
        this.vocabulary = vocab;
        List<String> docs = new ArrayList<String> ();
        docs.add(doc);
        build(docs, vocab);
    }

    public List<String> toString(List<Tuple> tuples){
        List<String> list = new ArrayList<String>();
        for (Tuple tuple: tuples){
            list.add(this.vocabulary.getToken(tuple.position));
        }
        return list;
    }

    private void build(List<String> docs, Vocabulary vocab){

        int numDocs = docs.size();
        this.wordIds = new int[numDocs][];
        this.tokenCts = new int[numDocs][];

        for(int docId=0; docId<docs.size();++docId){
            String doc = docs.get(docId);
            doc = doc.toLowerCase()
                    .replaceAll("-", " ")
                    .replaceAll("[^a-z ]", "")
                    .replaceAll(" +", " ");
            Map<Integer,Integer> counts = new LinkedHashMap<Integer, Integer>();
            for(String token:  extractTokens(doc)){
                if(vocab.contains(token)){
                    int tokenId = vocab.getId(token);
                    if(!counts.containsKey(tokenId)){
                        counts.put(tokenId, 1);
                    } else {
                        int c = counts.get(tokenId);
                        counts.put(tokenId, c+1);
                    }
                }
            }
            int tokenCount = counts.size();
            wordIds[docId] = new int[tokenCount];
            tokenCts[docId] = new int[tokenCount];
            int i=0 ;
            for(Map.Entry<Integer,Integer> e: counts.entrySet()){
                wordIds[docId][i] = e.getKey();
                tokenCts[docId][i] = e.getValue();
                ++i;
            }
        }
    }


    public String getToken(int i){
        return vocabulary.getToken(i);
    }


    public int[][] getTokenIds() {
        return wordIds;
    }

    /**
     * document Id x  token Id
     * @return
     */
    public int[][] getTokenCts() {
        return tokenCts;
    }

    public int size() {
        return tokenCts.length;
    }

    public int getTokenCount() {
        int total = 0;
        for(int [] d: tokenCts){
            for(int c: d){
                total+=c;
            }
        }
        return total;
    }
    private List<String> extractTokens(String doc ) {
        List<String> result = new ArrayList<String>();
        BreakIterator boundary = BreakIterator.getWordInstance( );
        boundary.setText(doc);
        int start = boundary.first();
        for (int end = boundary.next();
             end != BreakIterator.DONE;
             start = end, end = boundary.next()) {
            String s = doc.substring(start, end);
            if (s.trim().length() > 0) {
                result.add(s.toLowerCase( ));
            }
        }
        return result;
    }
}
