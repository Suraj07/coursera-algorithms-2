import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class SAP {

    private final Digraph graph;
    
    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null) {
            throw new IllegalArgumentException("arguments cannot be null");
        }
        graph = new Digraph(G);
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        int length = -1;
        if ((v < 0) || (v >= graph.V()) || (w < 0) || (w >= graph.V())) {
            throw new IllegalArgumentException("arguments outside range");
        }
        BreadthFirstDirectedPaths vbfdp = new BreadthFirstDirectedPaths(graph, v);
        BreadthFirstDirectedPaths wbfdp = new BreadthFirstDirectedPaths(graph, w);
        for (int i = 0; i < graph.V(); i++) {
            if (vbfdp.hasPathTo(i) && wbfdp.hasPathTo(i)) {
                int len = vbfdp.distTo(i) + wbfdp.distTo(i);
                if ((length == -1) || (len < length)) {
                    length = len;
                }
            }
        }
        return length;
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        int length = -1;
        int ca = -1;
        if ((v < 0) || (v >= graph.V()) || (w < 0) || (w >= graph.V())) {
            throw new IllegalArgumentException("arguments outside range");
        }
        BreadthFirstDirectedPaths vbfdp = new BreadthFirstDirectedPaths(graph, v);
        BreadthFirstDirectedPaths wbfdp = new BreadthFirstDirectedPaths(graph, w);
        for (int i = 0; i < graph.V(); i++) {
            if (vbfdp.hasPathTo(i) && wbfdp.hasPathTo(i)) {
                int len = vbfdp.distTo(i) + wbfdp.distTo(i);
                if ((length == -1) || (len < length)) {
                    length = len;
                    ca = i;
                }
            }
        }
        return ca;
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        int length = -1;
        checkNull(v);
        checkNull(w);
        BreadthFirstDirectedPaths vbfdp = new BreadthFirstDirectedPaths(graph, v);
        BreadthFirstDirectedPaths wbfdp = new BreadthFirstDirectedPaths(graph, w);
        for (int i = 0; i < graph.V(); i++) {
            if (vbfdp.hasPathTo(i) && wbfdp.hasPathTo(i)) {
                int len = vbfdp.distTo(i) + wbfdp.distTo(i);
                if ((length == -1) || (len < length)) {
                    length = len;
                }
            }
        }
        return length;
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        int length = -1;
        int ca = -1;
        checkNull(v);
        checkNull(w);
        BreadthFirstDirectedPaths vbfdp = new BreadthFirstDirectedPaths(graph, v);
        BreadthFirstDirectedPaths wbfdp = new BreadthFirstDirectedPaths(graph, w);
        for (int i = 0; i < graph.V(); i++) {
            if (vbfdp.hasPathTo(i) && wbfdp.hasPathTo(i)) {
                int len = vbfdp.distTo(i) + wbfdp.distTo(i);
                if ((length == -1) || (len < length)) {
                    length = len;
                    ca = i;
                }
            }
        }
        return ca;
    }
    
    private void checkNull(Iterable<Integer> v) {
        if (v == null) {
            throw new IllegalArgumentException("arguments cannot be null");
        }
        for (Integer i : v) {
            if (i == null) {
                throw new IllegalArgumentException("arguments cannot be null");
            }
        }
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length   = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}