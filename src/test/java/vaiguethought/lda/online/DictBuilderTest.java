package vaiguethought.lda.online;
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
 *
 */
import junit.framework.TestCase;

import java.util.List;

public class DictBuilderTest extends TestCase{
    
    public void testExtractSentence(){
        String d = "one two three";
        List<String> extract = VocabularyBuilder.extractTokens(d);
        assertEquals(5, extract.size());
    }
    
    public void testDocFreqMap(){
        String doc1 = "one two three";
        String doc2 = "two three four";
        VocabularyBuilder dictionaryBuilder = new VocabularyBuilder(2, 1);
        dictionaryBuilder.addTokensToFreqMap(doc1);
        dictionaryBuilder.addTokensToFreqMap(doc2);
        assertTrue(dictionaryBuilder.docFreqMap.get("one").equals(1));
        assertTrue(dictionaryBuilder.docFreqMap.get("two").equals(2));
    }

    public void testBoundedSet(){
        String doc1 = "one two three three three";
        String doc2 = "two three four";

        VocabularyBuilder dictionaryBuilder = new VocabularyBuilder(3, 1);
        dictionaryBuilder.addTokensToFreqMap(doc1);
        dictionaryBuilder.addTokensToFreqMap(doc2);

        dictionaryBuilder.addToTfIdf(doc1);
        dictionaryBuilder.addToTfIdf(doc2);

        assertTrue(dictionaryBuilder.bondedSet.size()==3);
    }

}
