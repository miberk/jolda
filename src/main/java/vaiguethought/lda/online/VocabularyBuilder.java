package vaiguethought.lda.online;

import java.text.BreakIterator;
import java.util.*;
/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
public class VocabularyBuilder {
    final BoundedSet bondedSet;
    int docCount = 0;
    final int minimalDocFrequency;
    //Token mapped to number of documents it belongs to
    final CountingMap docFreqMap = new CountingMap();

    public VocabularyBuilder(int cutOff) {
        this.bondedSet= new BoundedSet(cutOff);
        this.minimalDocFrequency = 3;

    }
    public VocabularyBuilder(int cutOff, int minimalDocFrequency) {
        this.bondedSet= new BoundedSet(cutOff);
        this.minimalDocFrequency = minimalDocFrequency;

    }
    public void addTokensToFreqMap(String document) {
        for (String s : new HashSet<String>(extractTokens(document))) {
            if(s.length()>1){
                docFreqMap.update(s);
            }
        }
        ++docCount;
    }

    public void addToTfIdf(String document) {
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

    public static final List<String> ENGLISH_STOP_WORDS = Arrays.asList(new String[]{
            "a", "an", "and", "are", "as", "at", "be", "but", "by",
            "for", "if", "in", "into", "is", "it",
            "no", "not", "of", "on", "or", "such",
            "that", "the", "their", "then", "there", "these",
            "they", "this", "to", "was", "will", "with",
            "we", "my","me", "our", "you", "so", "use", "has", "when"
    });
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

    static class Pair {
        int docIndex;
        int termFrequency;

    }
}
