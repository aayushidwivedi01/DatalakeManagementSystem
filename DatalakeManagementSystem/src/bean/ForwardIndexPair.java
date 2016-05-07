package bean;

public class ForwardIndexPair {

	private ForwardIndex f1;
	private ForwardIndex f2;

	public ForwardIndexPair(ForwardIndex f1, ForwardIndex f2) {
		super();
		this.f1 = f1;
		this.f2 = f2;
	}

	public ForwardIndex getF1() {
		return f1;
	}

	public void setF1(ForwardIndex f1) {
		this.f1 = f1;
	}

	public ForwardIndex getF2() {
		return f2;
	}

	public void setF2(ForwardIndex f2) {
		this.f2 = f2;
	}

	@Override
	public String toString() {
		return "ForwardIndexPairs [f1=" + f1 + ", f2=" + f2 + "]";
	}

}
