package au.edu.aekos.shared.system;

public class SharedStatsResult {

	private final String text;
	private final long stat;

	public SharedStatsResult(String text, long stat) {
		this.text = text;
		this.stat = stat;
	}

	public String getText() {
		return text;
	}

	public long getStat() {
		return stat;
	}
}
