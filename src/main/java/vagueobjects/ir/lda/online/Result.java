package vagueobjects.ir.lda.online;
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

import vagueobjects.ir.lda.online.matrix.Matrix;
import vagueobjects.ir.lda.online.matrix.Vector;
import vagueobjects.ir.lda.tokens.Documents;
import vagueobjects.ir.lda.tokens.Tuple;

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
