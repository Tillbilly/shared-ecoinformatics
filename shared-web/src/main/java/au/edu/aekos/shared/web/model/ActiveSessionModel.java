package au.edu.aekos.shared.web.model;

import java.util.Date;

public class ActiveSessionModel {
	private final String username;
	private final Date lastRequest;
	private final int minutesUntilTimeout;
	private final Date timeoutTime;

	public ActiveSessionModel(String username, Date lastRequest, int minutesUntilTimeout, Date timeoutTime) {
		this.username = username;
		this.lastRequest = lastRequest;
		this.minutesUntilTimeout = minutesUntilTimeout;
		this.timeoutTime = timeoutTime;
	}

	public String getUsername() {
		return username;
	}

	public Date getLastRequest() {
		return lastRequest;
	}

	public int getMinutesUntilTimeout() {
		return minutesUntilTimeout;
	}

	public Date getTimeoutTime() {
		return timeoutTime;
	}
}
