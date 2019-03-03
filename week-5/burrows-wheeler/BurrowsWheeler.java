import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler {

	private static final int R = 256;

	// apply Burrows-Wheeler transform, reading from standard input and writing
	// to standard output
	public static void transform() {
		String s = BinaryStdIn.readString();
		int n = s.length();
		char[] t = new char[n];
		int first = 0;
		CircularSuffixArray circularSuffixArray = new CircularSuffixArray(s);
		for (int i = 0; i < n; i++) {
			int index = circularSuffixArray.index(i);
			t[i] = charAt(s, index, n - 1);
			if (index == 0) {
				first = i;
			}
		}
		BinaryStdOut.write(first);
		for (int i = 0; i < n; i++) {
			BinaryStdOut.write(t[i]);
		}
		BinaryStdOut.close();
	}

	private static char charAt(String s, int offset, int d) {
		return s.charAt((offset + d) % s.length());
	}

	// apply Burrows-Wheeler inverse transform, reading from standard input and
	// writing to standard output
	public static void inverseTransform() {
		int first = BinaryStdIn.readInt();
		String t = BinaryStdIn.readString();
		int n = t.length();
		int[] next = new int[n];
		int[] count = new int[R + 1];
		char[] sorted = new char[n];
		for (int i = 0; i < n; i++) {
			count[t.charAt(i) + 1]++;
		}
		for (int i = 0; i < R; i++) {
			count[i + 1] += count[i];
		}
		for (int i = 0; i < n; i++) {
			sorted[count[t.charAt(i)]] = t.charAt(i);
			next[count[t.charAt(i)]++] = i;
		}

		int i = first;
		for (int j = 0; j < n; j++) {
			BinaryStdOut.write(sorted[i]);
			i = next[i];
		}
		BinaryStdOut.close();
	}

	// if args[0] is '-', apply Burrows-Wheeler transform
	// if args[0] is '+', apply Burrows-Wheeler inverse transform
	public static void main(String[] args) {
		if (args[0].equals("-")) {
			transform();
		} else if (args[0].equals("+")) {
			inverseTransform();
		}
	}
}