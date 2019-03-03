import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {

	// alphabet size of extended ASCII
	private static final int R = 256;
	// char size
	private static final int CHAR_SIZE = 8;

	// apply move-to-front encoding, reading from standard input and writing to
	// standard output
	public static void encode() {
		char[] sequence = getInitializedSequence();
		while (!BinaryStdIn.isEmpty()) {
			char c = BinaryStdIn.readChar();
			int index = getIndex(sequence, c);
			BinaryStdOut.write(index, CHAR_SIZE);
			moveCharToFront(sequence, c, index);
		}
		BinaryStdOut.close();
	}

	private static void moveCharToFront(char[] sequence, char c, int index) {
		for (int i = index; i > 0; i--) {
			sequence[i] = sequence[i - 1];
		}
		sequence[0] = c;
	}

	private static int getIndex(char[] sequence, char c) {
		for (int i = 0; i < R; i++) {
			if (sequence[i] == c) {
				return i;
			}
		}
		return 0;
	}

	// apply move-to-front decoding, reading from standard input and writing to
	// standard output
	public static void decode() {
		char[] sequence = getInitializedSequence();
		while (!BinaryStdIn.isEmpty()) {
			int index = BinaryStdIn.readInt(CHAR_SIZE);
			char c = sequence[index];
			BinaryStdOut.write(c);
			moveCharToFront(sequence, c, index);
		}
		BinaryStdOut.close();
	}

	private static char[] getInitializedSequence() {
		char[] sequence = new char[R];
		for (int i = 0; i < R; i++) {
			sequence[i] = (char) i;
		}
		return sequence;
	}

	// if args[0] is '-', apply move-to-front encoding
	// if args[0] is '+', apply move-to-front decoding
	public static void main(String[] args) {
		if (args[0].equals("-")) {
			encode();
		} else if (args[0].equals("+")) {
			decode();
		}
	}
}