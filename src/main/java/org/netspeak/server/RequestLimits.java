package org.netspeak.server;


public class RequestLimits {

	private int maxPhraseCount;
	private int phraseLengthMax;
	private int phraseLengthMin;
	private int pruningHigh;
	private int pruningLow;

	public int getMaxPhraseCount() {
		return maxPhraseCount;
	}

	public void setMaxPhraseCount(int value) {
		this.maxPhraseCount = checkUInt32(value);
	}

	public int getPhraseLengthMax() {
		return phraseLengthMax;
	}

	public void setPhraseLengthMax(int value) {
		this.phraseLengthMax = checkUInt32(value);
	}

	public int getPhraseLengthMin() {
		return phraseLengthMin;
	}

	public void setPhraseLengthMin(int value) {
		this.phraseLengthMin = checkUInt32(value);
	}

	public int getPruningHigh() {
		return pruningHigh;
	}

	public void setPruningHigh(int value) {
		this.pruningHigh = checkUInt32(value);
	}

	public int getPruningLow() {
		return pruningLow;
	}

	public void setPruningLow(int value) {
		this.pruningLow = checkUInt32(value);
	}

	private static int checkUInt32(int value) {
		if (value < 0) {
			throw new IllegalArgumentException("The value is an uint32 and cannot be less than 0.");
		}
		return value;
	}
}
