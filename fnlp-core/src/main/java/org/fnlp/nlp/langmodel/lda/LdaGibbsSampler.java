package org.fnlp.nlp.langmodel.lda;


import gnu.trove.list.array.TIntArrayList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import org.fnlp.ml.types.alphabet.HashFeatureAlphabet;
import org.fnlp.ml.types.alphabet.LabelAlphabet;
import org.fnlp.nlp.corpus.StopWords;
import org.fnlp.util.MyArrays;

/**
 * LDA模型
 * Modified from http://www.arbylon.net/projects/LdaGibbsSampler.java
 * Gibbs sampler for estimating the best assignments of topics for words and
 * documents in a corpus. The algorithm is introduced in Tom Griffiths' paper
 * "Gibbs sampling in the generative model of Latent Dirichlet Allocation"
 * (2002).
 * 
 * @author heinrich
 */
public class LdaGibbsSampler {

    /**
     * document data (term lists)
     */
    int[][] documents;

    /**
     * vocabulary size
     */
    int V;

    /**
     * number of topics
     */
    int K;

    /**
     * Dirichlet parameter (document--topic associations)
     */
    float alpha;

    /**
     * Dirichlet parameter (topic--term associations)
     */
    float beta;

    /**
     * topic assignments for each word.
     */
    int z[][];

    /**
     * cwt[i][j] number of instances of word i (term?) assigned to topic j.
     */
    int[][] word_topic_matrix;

    /**
     * na[i][j] number of words in document i assigned to topic j.
     */
    int[][] nd;

    /**
     * nwsum[j] total number of words assigned to topic j.
     */
    int[] nwsum;

    /**
     * nasum[i] total number of words in document i.
     */
    int[] ndsum;

    /**
     * cumulative statistics of theta
     */
    float[][] thetasum;

    /**
     * cumulative statistics of phi
     */
    float[][] phisum;

    /**
     * size of statistics
     */
    int numstats;

    /**
     * sampling lag (?)
     */
    private static int THIN_INTERVAL = 20;

    /**
     * burn-in period
     */
    private static int BURN_IN = 100;

    /**
     * max iterations
     */
    private static int ITERATIONS = 1000;

    /**
     * sample lag (if -1 only one sample taken)
     */
    private static int SAMPLE_LAG;

    private static int dispcol = 0;

    /**
     * Initialise the Gibbs sampler with data.
     * 
     * @param V
     *            vocabulary size
     * @param documents
     */
    public LdaGibbsSampler(int[][] documents, int V) {

        this.documents = documents;
        this.V = V;
    }

    /**
     * Initialisation: Must start with an assignment of observations to topics ?
     * Many alternatives are possible, I chose to perform random assignments
     * with equal probabilities
     * 
     * @param K
     *            number of topics
     *  z assignment of topics to words
     */
    public void initialState(int K) {
        int i;

        int M = documents.length;

        // initialise count variables.
        word_topic_matrix = new int[V][K];
        nd = new int[M][K];
        nwsum = new int[K];
        ndsum = new int[M];

        // The z_i are are initialised to values in [1,K] to determine the
        // initial state of the Markov chain.

        z = new int[M][];
        for (int m = 0; m < M; m++) {
            int N = documents[m].length;
            z[m] = new int[N];
            for (int n = 0; n < N; n++) {
                int topic = (int) (Math.random() * K);
                z[m][n] = topic;
                // number of instances of word i assigned to topic j
                word_topic_matrix[documents[m][n]][topic]++;
                // number of words in document i assigned to topic j.
                nd[m][topic]++;
                // total number of words assigned to topic j.
                nwsum[topic]++;
            }
            // total number of words in document i
            ndsum[m] = N;
        }
    }

    /**
     * Main method: Select initial state ? Repeat a large number of times: 1.
     * Select an element 2. Update conditional on other elements. If
     * appropriate, output summary for each run.
     * 
     * @param K
     *            number of topics
     * @param alpha
     *            symmetric prior parameter on document--topic associations
     * @param beta
     *            symmetric prior parameter on topic--term associations
     */
    private void gibbs(int K, float alpha, float beta) {
        this.K = K;
        this.alpha = alpha;
        this.beta = beta;

        // init sampler statistics
        if (SAMPLE_LAG > 0) {
            thetasum = new float[documents.length][K];
            phisum = new float[K][V];
            numstats = 0;
        }

        // initial state of the Markov chain:
        initialState(K);

        System.out.println("Sampling " + ITERATIONS
            + " iterations with burn-in of " + BURN_IN + " (B/S="
            + THIN_INTERVAL + ").");

        for (int i = 0; i < ITERATIONS; i++) {

            // for all z_i
            for (int m = 0; m < z.length; m++) {
                for (int n = 0; n < z[m].length; n++) {

                    // (z_i = z[m][n])
                    // sample from p(z_i|z_-i, w)
                    int topic = sampleFullConditional(m, n);
                    z[m][n] = topic;
                }
            }

            if ((i < BURN_IN) && (i % THIN_INTERVAL == 0)) {
                System.out.print("B");
                dispcol++;
            }
            // display progress
            if ((i > BURN_IN) && (i % THIN_INTERVAL == 0)) {
                System.out.print("S");
                dispcol++;
            }
            // get statistics after burn-in
            if ((i > BURN_IN) && (SAMPLE_LAG > 0) && (i % SAMPLE_LAG == 0)) {
                updateParams();
                System.out.print("|");
                if (i % THIN_INTERVAL != 0)
                    dispcol++;
            }
            if (dispcol >= 100) {
                System.out.println();
                dispcol = 0;
            }
        }
    }

    /**
     * Sample a topic z_i from the full conditional distribution: p(z_i = j |
     * z_-i, w) = (n_-i,j(w_i) + beta)/(n_-i,j(.) + W * beta) * (n_-i,j(d_i) +
     * alpha)/(n_-i,.(d_i) + K * alpha)
     * 
     * @param m
     *            document
     * @param n
     *            word
     */
    private int sampleFullConditional(int m, int n) {

        // remove z_i from the count variables
        int topic = z[m][n];
        word_topic_matrix[documents[m][n]][topic]--;
        nd[m][topic]--;
        nwsum[topic]--;
        ndsum[m]--;

        // do multinomial sampling via cumulative method:
        float[] p = new float[K];
        for (int k = 0; k < K; k++) {
            p[k] = (word_topic_matrix[documents[m][n]][k] + beta) / (nwsum[k] + V * beta)
                * (nd[m][k] + alpha) / (ndsum[m] + K * alpha);
        }
        
        topic = drawFromProbability(p);

        // add newly estimated z_i to count variables
        word_topic_matrix[documents[m][n]][topic]++;
        nd[m][topic]++;
        nwsum[topic]++;
        ndsum[m]++;

        return topic;
    }

	private int drawFromProbability(float[] p) {
		int idx;
		// cumulate multinomial parameters
        for (int k = 1; k < p.length; k++) {
            p[k] += p[k - 1];
        }
        // scaled sample because of unnormalised p[]
        float u = (float) (Math.random() * p[K - 1]);
        for (idx = 0; idx < p.length; idx++) {
            if (u < p[idx])
                break;
        }
		return idx;
	}

    /**
     * Add to the statistics the values of theta and phi for the current state.
     */
    private void updateParams() {
        for (int m = 0; m < documents.length; m++) {
            for (int k = 0; k < K; k++) {
                thetasum[m][k] += (nd[m][k] + alpha) / (ndsum[m] + K * alpha);
            }
        }
        for (int k = 0; k < K; k++) {
            for (int w = 0; w < V; w++) {
                phisum[k][w] += (word_topic_matrix[w][k] + beta) / (nwsum[k] + V * beta);
            }
        }
        numstats++;
    }

    /**
     * Retrieve estimated document--topic associations. If sample lag &gt; 0 then
     * the mean value of all sampled statistics for theta[][] is taken.
     * 
     * @return theta multinomial mixture of document topics (M x K)
     */
    public float[][] getTheta() {
        float[][] theta = new float[documents.length][K];

        if (SAMPLE_LAG > 0) {
            for (int m = 0; m < documents.length; m++) {
                for (int k = 0; k < K; k++) {
                    theta[m][k] = thetasum[m][k] / numstats;
                }
            }

        } else {
            for (int m = 0; m < documents.length; m++) {
                for (int k = 0; k < K; k++) {
                    theta[m][k] = (nd[m][k] + alpha) / (ndsum[m] + K * alpha);
                }
            }
        }

        return theta;
    }

    /**
     * Retrieve estimated topic--word associations. If sample lag &gt; 0 then the
     * mean value of all sampled statistics for phi[][] is taken.
     * 
     * @return phi multinomial mixture of topic words (K x V)
     */
    public float[][] getPhi() {
        float[][] phi = new float[K][V];
        if (SAMPLE_LAG > 0) {
            for (int k = 0; k < K; k++) {
                for (int w = 0; w < V; w++) {
                    phi[k][w] = phisum[k][w] / numstats;
                }
            }
        } else {
            for (int k = 0; k < K; k++) {
                for (int w = 0; w < V; w++) {
                    phi[k][w] = (word_topic_matrix[w][k] + beta) / (nwsum[k] + V * beta);
                }
            }
        }
        return phi;
    }

    /**
     * Print table of multinomial data
     * 
     * @param data
     *            vector of evidence
     * @param fmax
     *            max frequency in display
     *  the scaled histogram bin values
     */
    public static void hist(float[] data, int fmax) {

        float[] hist = new float[data.length];
        // scale maximum
        float hmax = 0;
        for (int i = 0; i < data.length; i++) {
            hmax = Math.max(data[i], hmax);
        }
        float shrink = fmax / hmax;
        for (int i = 0; i < data.length; i++) {
            hist[i] = shrink * data[i];
        }

        NumberFormat nf = new DecimalFormat("00");
        String scale = "";
        for (int i = 1; i < fmax / 10 + 1; i++) {
            scale += "    .    " + i % 10;
        }

        System.out.println("x" + nf.format(hmax / fmax) + "\t0" + scale);
        for (int i = 0; i < hist.length; i++) {
            System.out.print(i + "\t|");
            for (int j = 0; j < Math.round(hist[i]); j++) {
                if ((j + 1) % 10 == 0)
                    System.out.print("]");
                else
                    System.out.print("|");
            }
            System.out.println();
        }
    }

    /**
     * Configure the gibbs sampler
     * 
     * @param iterations
     *            number of total iterations
     * @param burnIn
     *            number of burn-in iterations
     * @param thinInterval
     *            update statistics interval
     * @param sampleLag
     *            sample interval (-1 for just one sample at the end)
     */
    public void configure(int iterations, int burnIn, int thinInterval,
        int sampleLag) {
        ITERATIONS = iterations;
        BURN_IN = burnIn;
        THIN_INTERVAL = thinInterval;
        SAMPLE_LAG = sampleLag;
    }

    /**
     * Driver with example data.
     * 
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
    	String infile = "../example-data/data-lda.txt";
    	String stopwordfile = "../models/stopwords/stopwords.txt";
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(infile ), "utf8"));

		//		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
//				outfile), enc2));
		StopWords sw = new StopWords(stopwordfile);
		
		LabelAlphabet dict = new LabelAlphabet();
		// words in documents
		ArrayList<TIntArrayList> documentsList= new ArrayList<TIntArrayList>();
		
		
		String line = null;
		while ((line = in.readLine()) != null) {
			line = line.trim();	
			if(line.length()==0)
				continue;
			String[] toks = line.split("\\s+");
			TIntArrayList wordlist = new TIntArrayList(); 
			for(int j=0;j<toks.length;j++){
				String tok = toks[j];
				if(sw.isStopWord(tok))
					continue;
				int idx = dict.lookupIndex(tok);
				wordlist.add(idx);
			}
			documentsList.add(wordlist);
		}
		in.close();
		int[][] documents;
		documents = new int[documentsList.size()][];
		for(int i=0;i<documents.length;i++){
			documents[i] = documentsList.get(i).toArray();
		}
        // vocabulary
        int V = dict.size();
        int M = documents.length;
        // # topics
        int K = 4;
        // good values alpha = 2, beta = .5
        float alpha = 2f;
        float beta = .5f;

        System.out.println("Latent Dirichlet Allocation using Gibbs Sampling.");

        LdaGibbsSampler lda = new LdaGibbsSampler(documents, V);
        lda.configure(10000, 2000, 100, 10);
        lda.gibbs(K, alpha, beta);

        float[][] theta = lda.getTheta();
        float[][] phi = lda.getPhi();

        System.out.println();
        System.out.println();
        System.out.println("Document--Topic Associations, Theta[d][k] (alpha="
            + alpha + ")");
        System.out.print("d\\k\t");
        for (int m = 0; m < theta[0].length; m++) {
            System.out.print("   " + m % 10 + "    ");
        }
        System.out.println();
        for (int m = 0; m < theta.length; m++) {
            System.out.print(m + "\t");
            for (int k = 0; k < theta[m].length; k++) {
                // System.out.print(theta[m][k] + " ");
                System.out.print(shadefloat(theta[m][k], 1) + " ");
            }
            System.out.println();
        }
        System.out.println();
        System.out.println("Topic--Term Associations, Phi[k][w] (beta=" + beta
            + ")");

        System.out.print("k\\w\t");
        for (int w = 0; w < phi[0].length; w++) {
            System.out.print("   " + dict.lookupString(w) + "    ");
        }
        System.out.println();
        for (int k = 0; k < phi.length; k++) {
            System.out.print(k + "\t");
            for (int w = 0; w < phi[k].length; w++) {
            	System.out.print(lnf.format(phi[k][w]) + " ");
//            	System.out.print(phi[k][w] + " ");
//                System.out.print(shadefloat(phi[k][w], 1) + " ");
            }
            System.out.println();
        }
        for (int k = 0; k < phi.length; k++) {
        	int[] top = MyArrays.sort(phi[k]);
        	
            for (int w = 0; w < 10; w++) {
            	System.out.print(dict.lookupString(top[w]) + " ");
            }
            System.out.println();
        }
    }

    static String[] shades = {"     ", ".    ", ":    ", ":.   ", "::   ",
        "::.  ", ":::  ", ":::. ", ":::: ", "::::.", ":::::"};

    static NumberFormat lnf = new DecimalFormat("00E0");

    /**
     * create a string representation whose gray value appears as an indicator
     * of magnitude, cf. Hinton diagrams in statistics.
     * 
     * @param d
     *            value
     * @param max
     *            maximum value
     * @return
     */
    public static String shadefloat(float d, float max) {
        int a = (int) Math.floor(d * 10 / max + 0.5);
        if (a > 10 || a < 0) {
            String x = lnf.format(d);
            a = 5 - x.length();
            for (int i = 0; i < a; i++) {
                x += " ";
            }
            return "<" + x + ">";
        }
        return "[" + shades[a] + "]";
    }
}