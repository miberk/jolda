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

import vaiguethought.lda.online.matrix.Matrix;
import vaiguethought.lda.online.matrix.Vector;

import java.util.*;

/**
 * Displays topics discovered by Online LDA. Topics are sorted by
 * their statistical importance.
 */
public class Result {
    /**Number of terms per each tokens to show*/
    static int NUMBER_OF_TOKENS = 15;
    private final Matrix lambda;
    private final double perplexity;
    private final Documents documents; 
    private final int totalTokenCount;

    /**
     *
     * @param docs  - documents in the batch
     * @param D   - total number of documents in corpus
     * @param bound  - variational bound
     * @param lambda   - variational distribution q(beta|lambda)
     */
    public Result(Documents docs, int D, double bound, Matrix lambda) {
        this.lambda = lambda; 
        this.documents = docs;
        this.totalTokenCount = docs.getTokenCount();
        double perWordBound = (bound * docs.size())  / D / totalTokenCount;
        this.perplexity = Math.exp(-perWordBound);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Perplexity estimate: ").append(perplexity).append("\n");
        int numTopics = lambda.getNumberOfRows();
        int numTerms = Math.min(NUMBER_OF_TOKENS, lambda.getNumberOfColumns());
        for (int k = 0; k < numTopics; ++k) {
            Vector termScores = lambda.getRow(k);

            for(Tuple tuple:  sortTopicTerms(termScores,  numTerms )){
                tuple.addToString(sb, documents);
            }

            sb.append('\n');
        }
        sb.append("\n");
        return sb.toString();
    }

    private Collection<Tuple> sortTopicTerms(Vector termScores, int numTerms ) {
        Set<Tuple> tuples = new TreeSet<Tuple>();
        double sum=0d;
        for(int i=0; i< termScores.getLength();++i){
            sum += termScores.elementAt(i);
        }

        double [] p = new double[termScores.getLength()];
        for(int i=0; i< termScores.getLength();++i){
            p[i] = termScores.elementAt(i)/sum;
        }


        for(int i=0; i< termScores.getLength();++i){
            Tuple tuple = new Tuple(i, p[i]);
            tuples.add(tuple);
        }
        return new ArrayList<Tuple>(tuples).subList(0, numTerms);
    }

}
