import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
    
    private final WordNet w;
    
    // constructor takes a WordNet object
    public Outcast(WordNet wordnet) {
        w = wordnet;
    }
    
    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        String outcast = null;
        int maxdis = 0;
        for (int i = 0; i < nouns.length; i++) {
            int dis = getdis(i, nouns);
            if (dis >= maxdis) {
                maxdis = dis;
                outcast = nouns[i];
            }
        }
        return outcast;
    }
    
    private int getdis(int index, String[] nouns) {
        int dis = 0;
        for (int i = 0; i < nouns.length; i++) {
            dis += w.distance(nouns[i], nouns[index]);
        }
        return dis;
    }
    
    // see test client below
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}