import java.util.HashSet;
import java.util.Iterator;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;

public class BoggleSolver {

	private static final int R = 26;
	private final DictionarySet dictionarySet;

	// Initializes the data structure using the given array of strings as the
	// dictionary.
	// (You can assume each word in the dictionary contains only the uppercase
	// letters A through Z.)
	public BoggleSolver(String[] dictionary) {
		dictionarySet = new DictionarySet();
		int len = dictionary.length;
		for (int i = 0; i < len; i++) {
			dictionarySet.add(dictionary[i]);
		}
	}

	// Returns the set of all valid words in the given Boggle board, as an
	// Iterable.
	public Iterable<String> getAllValidWords(BoggleBoard boggleBoard) {
		HashSet<String> validWordSet = new HashSet<>();
		char[] board = new char[boggleBoard.rows() * boggleBoard.cols()];
		for (int i = 0; i < boggleBoard.rows(); i++) {
			for (int j = 0; j < boggleBoard.cols(); j++) {
				board[getIndex(i, j, boggleBoard)] = boggleBoard.getLetter(i, j);
			}
		}
		for (int i = 0; i < boggleBoard.rows(); i++) {
			for (int j = 0; j < boggleBoard.cols(); j++) {
				dfsBoard(boggleBoard, i, j, validWordSet, board);
			}
		}
		return validWordSet;
	}

	private Iterable<String> dfsBoard(BoggleBoard boggleBoard, int row, int col, HashSet<String> validWordSet,
			char[] board) {
		boolean[] marked = new boolean[boggleBoard.rows() * boggleBoard.cols()];
		collect(boggleBoard, row, col, "", validWordSet, marked, null, board);
		return validWordSet;
	}

	private void collect(BoggleBoard boggleBoard, int row, int col, String prefix, HashSet<String> validWordSet,
			boolean[] marked, Node node, char[] board) {
		int index = getIndex(row, col, boggleBoard);
		char c = board[index];
		prefix += (c == 'Q') ? "QU" : c;
		node = dictionarySet.prefix(prefix, node);
		if (node == null) {
			return;
		}
		marked[index] = true;
		if ((prefix.length() > 2) && (node.isString)) {
			validWordSet.add(prefix);
		}
		for (int i = row - 1; i < row + 2; i++) {
			for (int j = col - 1; j < col + 2; j++) {
				if ((checkIndex(boggleBoard, i, j)) && (!marked[getIndex(i, j, boggleBoard)])) {
					collect(boggleBoard, i, j, prefix, validWordSet, marked, node, board);
				}
			}
		}
		marked[index] = false;
	}

	private int getIndex(int row, int col, BoggleBoard board) {
		int index = (row * board.cols()) + col;
		return index;
	}

	private boolean checkIndex(BoggleBoard board, int row, int col) {
		if ((row < 0) || (col < 0) || (row >= board.rows()) || (col >= board.cols())) {
			return false;
		}
		return true;
	}

	// Returns the score of the given word if it is in the dictionary, zero
	// otherwise.
	// (You can assume the word contains only the uppercase letters A through
	// Z.)
	public int scoreOf(String word) {
		if (dictionarySet.contains(word)) {
			switch (word.length()) {
			case 0:
			case 1:
			case 2:
				return 0;
			case 3:
			case 4:
				return 1;
			case 5:
				return 2;
			case 6:
				return 3;
			case 7:
				return 5;
			default:
				return 11;
			}
		}
		return 0;
	}

	public static void main(String[] args) {
		In in = new In(args[0]);
		String[] dictionary = in.readAllStrings();
		BoggleSolver solver = new BoggleSolver(dictionary);
		BoggleBoard board = new BoggleBoard(args[1]);
		long t1 = System.currentTimeMillis();
		int count = 0;
		while ((System.currentTimeMillis() - t1) < 1000) {
			int score = 0;
			for (String word : solver.getAllValidWords(new BoggleBoard())) {
				// StdOut.println(word);
				score += solver.scoreOf(word);
			}
			// StdOut.println("Score = " + score);
			count++;
		}
		long t2 = System.currentTimeMillis();
		System.out.println("Count: " + count + " Execution time: " + (t2 - t1));
	}

	private static class Node {

		private Node[] next = new Node[R];
		private boolean isString;
		private int d;

		public Node(int d) {
			this.d = d;
		}
	}

	private static class DictionarySet implements Iterable<String> {

		private Node root; // root of trie

		public DictionarySet() {
			root = new Node(0);
		}

		public boolean contains(String key) {
			if (key == null)
				throw new IllegalArgumentException("argument to contains() is null");
			if (key.length() < 3) {
				return false;
			}
			Node x = get(root, key, 0);
			if (x == null)
				return false;
			return x.isString;
		}

		private Node get(Node x, String key, int d) {
			if (x == null)
				return null;
			if (d == key.length())
				return x;
			char c = key.charAt(d);
			return get(x.next[c - 'A'], key, d + 1);
		}

		public void add(String key) {
			if (key == null)
				throw new IllegalArgumentException("argument to add() is null");
			if (key.length() < 3) {
				return;
			}
			root = add(root, key, 0);
		}

		private Node add(Node x, String key, int d) {
			if (x == null)
				x = new Node(d);
			if (d == key.length()) {
				x.isString = true;
			} else {
				char c = key.charAt(d);
				x.next[c - 'A'] = add(x.next[c - 'A'], key, d + 1);
			}
			return x;
		}

		public Node prefix(String prefix, Node node) {
			Node x = (node == null) ? root : node;
			for (int i = x.d; i < prefix.length(); i++) {
				x = x.next[prefix.charAt(i) - 'A'];
				if (x == null) {
					break;
				}
			}
			return x;
		}

		@Override
		public Iterator<String> iterator() {
			return keysWithPrefix("").iterator();
		}

		public Iterable<String> keysWithPrefix(String prefix) {
			Queue<String> results = new Queue<String>();
			Node x = get(root, prefix, 0);
			collect(x, new StringBuilder(prefix), results);
			return results;
		}

		private void collect(Node x, StringBuilder prefix, Queue<String> results) {
			if (x == null)
				return;
			if (x.isString)
				results.enqueue(prefix.toString());
			for (char c = 'A'; c < R + 'A'; c++) {
				prefix.append(c);
				collect(x.next[c - 'A'], prefix, results);
				prefix.deleteCharAt(prefix.length() - 1);
			}
		}

	}

}
