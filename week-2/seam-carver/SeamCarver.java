import edu.princeton.cs.algs4.Picture;

public class SeamCarver {

	private Picture picture;

	// create a seam carver object based on the given picture
	public SeamCarver(Picture picture) {
		nullCheck(picture);
		this.picture = new Picture(picture);
	}

	// current picture
	public Picture picture() {
		return new Picture(picture);
	}

	// width of current picture
	public int width() {
		return picture.width();
	}

	// height of current picture
	public int height() {
		return picture.height();
	}

	// energy of pixel at column x and row y
	// The energy of pixel (x,y) is sqrt(Δ2x(x,y)+Δ2y(x,y))
	public double energy(int x, int y) {
		checkHeightValid(y);
		checkWidthValid(x);
		if (pixelIsBorder(x, y)) {
			return 1000;
		}
		double delXSq = getDelXSq(x, y);
		double delYSq = getDelYSq(x, y);
		return Math.sqrt(delXSq + delYSq);
	}

	private double getDelYSq(int x, int y) {
		int yp1 = picture.getRGB(x, y + 1);
		int ym1 = picture.getRGB(x, y - 1);
		int rdel = ((yp1 >> 16) & 0xFF) - ((ym1 >> 16) & 0xFF);
		int gdel = ((yp1 >> 8) & 0xFF) - ((ym1 >> 8) & 0xFF);
		int bdel = ((yp1 >> 0) & 0xFF) - ((ym1 >> 0) & 0xFF);
		double delSq = (rdel * rdel) + (gdel * gdel) + (bdel * bdel);
		return delSq;
	}

	private double getDelXSq(int x, int y) {
		int xp1 = picture.getRGB(x + 1, y);
		int xm1 = picture.getRGB(x - 1, y);
		int rdel = ((xp1 >> 16) & 0xFF) - ((xm1 >> 16) & 0xFF);
		int gdel = ((xp1 >> 8) & 0xFF) - ((xm1 >> 8) & 0xFF);
		int bdel = ((xp1 >> 0) & 0xFF) - ((xm1 >> 0) & 0xFF);
		double delSq = (rdel * rdel) + (gdel * gdel) + (bdel * bdel);
		return delSq;
	}

	private boolean pixelIsBorder(int x, int y) {
		if ((x == 0) || (y == 0) || (x == width() - 1) || (y == height() - 1)) {
			return true;
		}
		return false;
	}

	// sequence of indices for horizontal seam
	public int[] findHorizontalSeam() {
		double[][] energy = getEnergyTransposeMatrix();
		int[] verticalSeam = getVerticalSeam(energy);
		return verticalSeam;
	}

	// sequence of indices for vertical seam
	public int[] findVerticalSeam() {
		double[][] energy = getEnergyMatrix();
		int[] verticalSeam = getVerticalSeam(energy);
		return verticalSeam;
	}

	private int[] getVerticalSeam(double[][] energy) {
		double[] distTo = new double[(energy.length * energy[0].length) + 1];
		int[] vertexTo = new int[(energy.length * energy[0].length) + 1];

		for (int i = 0; i < energy.length; i++) {
			for (int j = 0; j < energy[0].length; j++) {
				if (i == 0) {
					distTo[j] = energy[i][j];
					vertexTo[j] = -1;
				} else {
					distTo[(i * energy[0].length) + j] = Double.POSITIVE_INFINITY;
				}
			}
		}
		distTo[(energy.length * energy[0].length)] = Double.POSITIVE_INFINITY;

		// topological sort
		for (int i = 0; i < energy.length; i++) {
			for (int j = 0; j < energy[0].length; j++) {
				// upto 3 edged per vertex
				// relax all edges/vertices
				int from = (i * energy[0].length) + j;
				if (i == energy.length - 1) {
					int to = (energy.length * energy[0].length);
					relax(from, to, 0, distTo, vertexTo);
				} else {
					int to = ((i + 1) * energy[0].length) + j;
					double weight = energy[i + 1][j];
					relax(from, to, weight, distTo, vertexTo);
					// check
					if (j > 0) {
						to = ((i + 1) * energy[0].length) + j - 1;
						weight = energy[i + 1][j - 1];
						relax(from, to, weight, distTo, vertexTo);
					}
					// check
					if (j < (energy[0].length - 1)) {
						to = ((i + 1) * energy[0].length) + j + 1;
						weight = energy[i + 1][j + 1];
						relax(from, to, weight, distTo, vertexTo);
					}
				}
			}
		}
		int verticalSeam[] = new int[energy.length];
		int vertex = energy.length * energy[0].length;
		int index = energy.length - 1;
		while (vertexTo[vertex] != -1) {
			vertex = vertexTo[vertex];
			verticalSeam[index--] = vertex % energy[0].length;
		}
		return verticalSeam;
	}

	private void relax(int from, int to, double weight, double[] distTo, int[] vertexTo) {
		if (distTo[to] > (distTo[from] + weight)) {
			distTo[to] = distTo[from] + weight;
			vertexTo[to] = from;
		}
	}

	private double[][] getEnergyMatrix() {
		int w = width();
		int h = height();
		double[][] energy = new double[h][w];
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				energy[i][j] = energy(j, i);
			}
		}
		return energy;
	}

	private double[][] getEnergyTransposeMatrix() {
		int w = width();
		int h = height();
		double[][] energy = new double[w][h];
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				energy[i][j] = energy(i, j);
			}
		}
		return energy;
	}

	// remove horizontal seam from current picture
	public void removeHorizontalSeam(int[] seam) {
		nullCheck(seam);
		checkValidSeam(seam, false);
		argLTEOne(height());
		int width = width();
		int height = height();
		Picture p = new Picture(width, height - 1);
		for (int col = 0; col < width; col++) {
			for (int row = 0, r = 0; row < height; row++, r++) {
				if (seam[col] != row) {
					p.setRGB(col, r, picture.getRGB(col, row));
				} else {
					r--;
				}
			}
		}
		this.picture = p;
	}

	// remove vertical seam from current picture
	public void removeVerticalSeam(int[] seam) {
		nullCheck(seam);
		checkValidSeam(seam, true);
		argLTEOne(width());
		int width = width();
		int height = height();
		Picture p = new Picture(width - 1, height);
		for (int row = 0; row < height; row++) {
			for (int col = 0, c = 0; col < width; col++, c++) {
				if (seam[row] != col) {
					p.setRGB(c, row, picture.getRGB(col, row));
				} else {
					c--;
				}
			}
		}
		this.picture = p;
	}

	// between 0 and width − 1
	private void checkWidthValid(int w) {
		if ((w < 0) || (w >= width())) {
			throw new IllegalArgumentException("width not in range");
		}
	}

	// between 0 and height − 1
	private void checkHeightValid(int h) {
		if ((h < 0) || (h >= height())) {
			throw new IllegalArgumentException("height not in range");
		}
	}

	private void nullCheck(Object object) {
		if (object == null) {
			throw new IllegalArgumentException("null parameter");
		}
	}

	// array of the wrong length OR
	// either an entry is outside its prescribed range or two adjacent entries
	// differ by more than 1
	private void checkValidSeam(int[] seam, boolean isVertical) {
		int len = seam.length;
		if (isVertical) {
			if (len != height()) {
				throw new IllegalArgumentException("vertical seam length invalid");
			}
		} else {
			if (len != width()) {
				throw new IllegalArgumentException("horizontal seam length invalid");
			}
		}
		int prev = seam[0], current;
		for (int i = 0; i < len; i++) {
			current = seam[i];
			if (isVertical) {
				checkWidthValid(current);
			} else {
				checkHeightValid(current);
			}
			if (i == 0) {
				prev = seam[i];
			} else {
				prev = seam[i - 1];
			}
			checkAdjacency(current, prev);
		}
	}

	private void checkAdjacency(int current, int prev) {
		if ((Math.abs(current - prev)) > 1) {
			throw new IllegalArgumentException("seam indices not adjacent");
		}
	}

	private void argLTEOne(int arg) {
		if (arg <= 1) {
			throw new IllegalArgumentException("arg less than or equal to 1");
		}
	}
}