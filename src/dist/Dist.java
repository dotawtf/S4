package dist;

public class Dist {
	private int sampleIndex;
	private double dist;
	
	public Dist() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Dist(int sampleIndex, double dist) {
		super();
		this.sampleIndex = sampleIndex;
		this.dist = dist;
	}

	public int getSampleIndex() {
		return sampleIndex;
	}

	public void setSampleIndex(int sampleIndex) {
		this.sampleIndex = sampleIndex;
	}

	public double getDist() {
		return dist;
	}

	public void setDist(double dist) {
		this.dist = dist;
	}
	

}
