public class CircularSuffixArray {

	private final int n;
	private int[] index;

	// circular suffix array of s
	public CircularSuffixArray(String s) {
		if (s == null) {
			throw new IllegalArgumentException("s is null");
		}
		n = s.length();
		index = new int[n];
		for (int i = 0; i < n; i++) {
			index[i] = i;
		}
		sort(s, 0, n - 1, 0);
	}

	private void sort(String s, int lo, int hi, int d) {
		if (hi <= lo)
			return;
		int lt = lo, gt = hi;
		int v = charAt(s, lo, d);
		int i = lo + 1;
		while (i <= gt) {
			int t = charAt(s, i, d);
			if (t < v)
				exch(lt++, i++);
			else if (t > v)
				exch(i, gt--);
			else
				i++;
		}
		sort(s, lo, lt - 1, d);
		if (v >= 0)
			sort(s, lt, gt, d + 1);
		sort(s, gt + 1, hi, d);
	}

	private void exch(int i, int j) {
		int t = index[i];
		index[i] = index[j];
		index[j] = t;
	}

	private int charAt(String s, int offset, int d) {
		if (d >= n)
			return -1;
		return s.charAt((index[offset] + d) % n);
	}

	// length of s
	public int length() {
		return n;
	}

	// returns index of ith sorted suffix
	public int index(int i) {
		if ((i < 0) || (i >= n)) {
			throw new IllegalArgumentException("index out of range");
		}
		return index[i];
	}

	// unit testing (required)
	public static void main(String[] args) {
		CircularSuffixArray circularSuffixArray = new CircularSuffixArray(args[0]);
		System.out.println("length: " + circularSuffixArray.length());
		for (int i = 0; i < args[0].length(); i++) {
			System.out.print(circularSuffixArray.index(i) + " ");
			for (int j = 0; j < args[0].length(); j++) {
				int index = (circularSuffixArray.index(i) + j) % circularSuffixArray.length();
				System.out.print(args[0].charAt(index));
			}
			System.out.println();
		}
	}
}