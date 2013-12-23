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

import static vagueobjects.ir.lda.online.matrix.MatrixUtil.*;
import static vagueobjects.ir.lda.online.matrix.MatrixUtil.dirichletExpectation;
import static vagueobjects.ir.lda.online.matrix.MatrixUtil.sum;

public class OnlineLDA {
    public final static double MEAN_CHANGE_THRESHOLD = 1e-5;
    public final static int NUM_ITERATIONS = 200;

    private int batchCount;
    private final double kappa;
    private final int K;
    private final int  D;
    private final int W;
    private final double tau0;
    private final double alpha;
    private final double eta;
    private double rhot;
    private Matrix lambda;
    private Matrix eLogBeta;
    private Matrix expELogBeta;
    private Matrix stats ;
    private Matrix gamma;
    /**
     * For a vector theta ~ Dir(alpha), computes E[log(theta)] given alpha.
     * @param W  - vocabulary length
     * @param K  - number of topics
     * @param D  - total number of documents in the population.
     * @param alpha - hyperparameter for prior on weight vectors theta
     * @param eta   - hyperparameter for prior on topics beta
     * @param tau   - controls early iterations
     * @param kappa -  learning rate: exponential decay rate, should be within (0.5, 1.0].
     */
    public OnlineLDA(int W, int K, int D, double alpha,
                     double eta, double tau , double kappa) {
        this.K = K;
        this.D = D;
        this.W = W;
        this.alpha = alpha;
        this.eta = eta;
        this.tau0 = tau + 1;
        this.kappa = kappa;
        this.batchCount = 0;
        //initialize the variational distribution q(beta|lambda)
        this.lambda = sampleGamma(W, K);
        //posterior over topics -beta is parameterized by lambda
        this.eLogBeta = dirichletExpectation(lambda);
        this.expELogBeta = exp(eLogBeta);
    }

    private void expectationStep(Documents docs) {
        int [][] wordIds = docs.getTokenIds();
        int [][] wordCts = docs.getTokenCts();
        int batchD = docs.size();

        //variational parameter over documents x topics
        //parameters over documents x topics
        this.gamma = sampleGamma(K, batchD);

        Matrix eLogTheta = dirichletExpectation(this.gamma);
        Matrix expELogTheta = exp(eLogTheta);

        this.stats = lambda.shape();
        for (int d = 0; d < batchD; ++d) {
            int[] ids = wordIds[d];
            if(ids.length==0){
                continue;
            }

            Vector cts = new Vector(wordCts[d]);
            //Topic proportions
            Vector gammaD = gamma.getRow(d);

            Vector expELogThetaD = expELogTheta.getRow(d);
            Matrix expELogBetaD = this.expELogBeta.extractColumns(ids);

            Vector phiNorm = expELogThetaD.dot(expELogBetaD);
            phiNorm = phiNorm.add(1E-100);
            Vector lastGamma;

            for (int it=0; it < NUM_ITERATIONS; ++it) {
                lastGamma = gammaD;
                Vector v1 = cts.div(phiNorm).dot(expELogBetaD.tr());
                gammaD =  expELogThetaD.product(v1).add(alpha);

                Vector eLogThetaD = dirichletExpectation(gammaD);
                expELogThetaD = exp(eLogThetaD);

                phiNorm = expELogThetaD.dot(expELogBetaD).add(1E-100);
                if (gammaD.closeTo(lastGamma, MEAN_CHANGE_THRESHOLD*K)) {
                    break;
                }
            }
            gamma.setRow(d, gammaD);
            Matrix m = expELogThetaD.outer (cts.div(phiNorm));
            stats.incrementColumns(ids, m);

        }

        stats = stats.product(this.expELogBeta);

    }


    public Result workOn(Documents docs) {
        this.rhot = Math.pow(this.tau0 + this.batchCount, -this.kappa);
        expectationStep(docs );

        double bound = approxBound( docs);

        Matrix a = this.lambda.product(1 - rhot);
        Matrix b = (stats.product( (double )D/ docs.size())).add(eta);
        b = b.product(rhot);
        this.lambda = a.add(b);

        this.eLogBeta = dirichletExpectation(lambda);
        this.expELogBeta = exp(eLogBeta);


        this.batchCount++;
        return new Result(docs, D, bound, lambda);
    }


    double approxBound( Documents docs) {
        int[][] wordIds = docs.getTokenIds();
        int[][] wordCts = docs.getTokenCts();
        int batchD = docs.size();

        double score =0d;
        Matrix eLogTheta = dirichletExpectation(this.gamma);

        double tMax=0;
        for(int d=0; d<batchD;++d){
            Vector ids = new Vector(wordIds[d]);
            Vector cts = new Vector(wordCts[d]);
            Vector phiNorm = new Vector(ids.getLength());

            for(int i=0; i< ids.getLength();++i){
                Vector v =eLogBeta.extractColumn(wordIds[d][i]);
                Vector topics =eLogTheta.getRow(d);
                Vector u = v.add(topics) ;
                tMax = u.max();

                phiNorm.set(i, Math.log(sum(exp(u.add(-tMax)))) + tMax);
            }

            score += sum(cts.product(phiNorm));
        }
        score-= sum(gamma.add(-alpha).product(eLogTheta));
        score+= sum(gammaLn(gamma).add(-gammaLn(alpha)));

        score-= sum(gammaLn(gamma.sumByRows()).add(-gammaLn(alpha * K)));
        score*= D/(double)docs.size();
        score-= sum(lambda.add(-eta).product(eLogBeta ));
        score+= sum(gammaLn(lambda).add(-gammaLn(eta)));
        score-= sum(gammaLn(lambda.sumByRows()).add(-gammaLn(eta * W)));
        return score;
    }
}


