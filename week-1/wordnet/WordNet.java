import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import java.util.HashMap;
import java.util.Map;
import edu.princeton.cs.algs4.Bag;
import java.util.Set;
import java.util.HashSet;

public class WordNet {
    
    private int synsetCount = 0;
    private Digraph d;
    private final Map<String, Bag<Integer>> map = new HashMap<>();
    private final Map<Integer, String> synsetMap = new HashMap<>();
    private final SAP sap;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if ((synsets == null) || (hypernyms == null)) {
            throw new IllegalArgumentException("arguments cannot be null");
        }
        // The constructor should throw a java.lang.IllegalArgumentException if the input does not correspond to a rooted DAG.
        parseSynsets(synsets);
        parseHypernyms(hypernyms);
        sap = new SAP(d);
    }
    
    private void parseSynsets(String synsets) {
        In in = new In(synsets);
        while (!in.isEmpty()) {
            synsetCount++;
            String line = in.readLine();
            String[] fields = line.split(",");
            int synsetId = Integer.parseInt(fields[0]);
            synsetMap.put(synsetId, fields[1]);
            String[] nouns = fields[1].split("\\s+");
            for (String noun : nouns) {
                if (map.get(noun) == null) {
                    Bag<Integer> bag = new Bag<Integer>();
                    bag.add(synsetId);
                    map.put(noun, bag);
                } else {
                    map.get(noun).add(synsetId);
                }
            }
        }
    }
    
    private void parseHypernyms(String hypernyms) {
        In in = new In(hypernyms);
        d = new Digraph(synsetCount);
        Set<String> set = new HashSet<String>();
        while (!in.isEmpty()) {
            String line = in.readLine();
            String[] fields = line.split(",");
            int synsetId = Integer.parseInt(fields[0]);
            if (fields.length > 1) {
                set.add(fields[0]);
            }
            for (int i = 1; i < fields.length; i++) {
                int hypernymId = Integer.parseInt(fields[i]);
                d.addEdge(synsetId, hypernymId);
            }
        }
        if (synsetCount - set.size() != 1) {
            throw new IllegalArgumentException("not a rooted DAG");
        }
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return map.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) {
            throw new IllegalArgumentException("argument cannot be null");
        }
        return (map.get(word) != null) ? true : false;
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if ((nounA == null) || (nounB == null)) {
            throw new IllegalArgumentException("arguments cannot be null");
        }
        // The distance() and sap() methods should throw a java.lang.IllegalArgumentException unless both of the noun arguments are WordNet nouns.
        if ((!isNoun(nounA)) || (!isNoun(nounB))) {
            throw new IllegalArgumentException("arguments not wordnet nouns");
        }
        return sap.length(map.get(nounA), map.get(nounB));
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if ((nounA == null) || (nounB == null)) {
            throw new IllegalArgumentException("arguments cannot be null");
        }
        // The distance() and sap() methods should throw a java.lang.IllegalArgumentException unless both of the noun arguments are WordNet nouns.
        if ((!isNoun(nounA)) || (!isNoun(nounB))) {
            throw new IllegalArgumentException("arguments not wordnet nouns");
        }
        return synsetMap.get(sap.ancestor(map.get(nounA), map.get(nounB)));
    }

    // do unit testing of this class
    public static void main(String[] args) {
        WordNet wn = new WordNet(args[0], args[1]);
        while (!StdIn.isEmpty()) {
            String v = StdIn.readString();
            String w = StdIn.readString();
            int length   = wn.distance(v, w);
            String ancestor = wn.sap(v, w);
            StdOut.printf("length = %d, ancestor = %s\n", length, ancestor);
        }
    }
}