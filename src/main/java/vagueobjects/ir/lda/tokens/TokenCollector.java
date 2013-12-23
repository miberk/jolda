package vagueobjects.ir.lda.tokens;

import java.text.BreakIterator;
import java.util.*;
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
/**
 * Given a set of document, first remove tokens that belong to the
 * stop list, then extract tokens with highest TF-IDF score.   Also
 * removes tokens that appeared in less than <code>minimalDocFrequency</code>
 * documents.
 * <p/>
 *
 *
 */
public class TokenCollector {
    final BoundedSet bondedSet;
    int docCount = 0;
    final int minimalDocFrequency;
    //Token mapped to number of documents it belongs to
    final CountingMap docFreqMap = new CountingMap();

    public TokenCollector(int cutOff) {
        this.bondedSet= new BoundedSet(cutOff);
        this.minimalDocFrequency = 3;

    }
    public TokenCollector(int cutOff, int minimalDocFrequency) {
        this.bondedSet= new BoundedSet(cutOff);
        this.minimalDocFrequency = minimalDocFrequency;

    }

    void addTokensToFreqMap(String document) {
        for (String s : new HashSet<String>(extractTokens(document))) {
            if(s.length()>1){
                docFreqMap.update(s);
            }
        }
        ++docCount;
    }

    void addToTfIdf(String document) {
        CountingMap termFreq = new CountingMap();

        for (String s : extractTokens(document)) {
            if(s.length()>1){
                termFreq.update(s);
            }
        }

        for (Map.Entry<String, Integer> entry : termFreq.entrySet()) {
            String token = entry.getKey();
            if (termFreq.containsKey(token)) {
                int df = docFreqMap.get(token);
                if(df<minimalDocFrequency){
                    continue;
                }
                double f = (double)docCount / docFreqMap.get(token);
                double tfIdf = (double) termFreq.get(token) * Math.log(f);
                bondedSet.add(new Token(token, tfIdf));
            }

        }
    }

    public Set<String> getWords( ){
        Set<String> result = new TreeSet<String>();
        for(Token token: bondedSet){
            result.add(token.word);
        }
        return result;
    }

    static class BoundedSet extends TreeSet<Token>{
        int bound;
        BoundedSet(int bound){
            this.bound = bound;
        }
        @Override
        public boolean add(Token token){
            boolean added = super.add(token);
            if(size()>bound){
                Token last = last();
                remove(last);
            }
            return added;
        }
    }
    
    
    static class CountingMap extends HashMap<String, Integer> {
        void update(String s) {
            if (containsKey(s)) {
                int count = get(s);
                put(s, count + 1);
            } else {
                put(s, 1);
            }
        }
    }

    public static final List<String> ENGLISH_STOP_WORDS = Arrays.asList(
            "a", "an", "and", "are", "as", "at", "be", "but", "by",
            "for", "if", "in", "into", "is", "it",
            "no", "not", "of", "on", "or", "such",
            "that", "the", "their", "then", "there", "these",
            "they", "this", "to", "was", "will", "with",
            "we", "my","me", "our", "you", "so", "use", "has", "when");

    static List<String> extractTokens(String document) {
        BreakIterator iterator = BreakIterator.getWordInstance();
        iterator.setText(document);
        ArrayList<String> result = new ArrayList<String>();
        int start = iterator.first();
        for (int end = iterator.next(); end != BreakIterator.DONE;
            start = end, end = iterator.next()) {
            String s = document.substring(start, end).toLowerCase().replaceAll("[^a-z]", "");
            if(!ENGLISH_STOP_WORDS.contains(s)){
                result.add(s);
            }
        }

        return result;

    }

    static class Token implements Comparable<Token>{
        final String word;
        final double tfIdf;

        Token(String word, double tfIdf) {
            this.word = word;
            this.tfIdf = tfIdf;
        }

        @Override
        public int compareTo(Token o) {
            if(o.tfIdf==tfIdf){
                return word.compareTo(o.word);
            }
            return (tfIdf<o.tfIdf)?1:-1;
        }
    }

}
