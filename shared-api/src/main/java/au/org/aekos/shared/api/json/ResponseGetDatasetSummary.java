package au.org.aekos.shared.api.json;

import java.util.EnumSet;
import java.util.Set;

import com.google.gson.Gson;

public class ResponseGetDatasetSummary {
	private final String errorMessage;
	private final SharedDatasetSummary payload;
	private final Outcome outcome;
	
	public enum Outcome {
		SUCCESS_PUBLISHED,
		SUCCESS_APPROVED,
		FAILURE;
		
		public static final Set<Outcome> SUCCESS_OUTCOMES = EnumSet.of(SUCCESS_APPROVED, SUCCESS_PUBLISHED);

		public boolean isSuccess() {
			return SUCCESS_OUTCOMES.contains(this);
		}

		public boolean isPendingPublish() {
			return SUCCESS_APPROVED.equals(this);
		}
	}
	
	ResponseGetDatasetSummary(Outcome outcome, String errorMessage, SharedDatasetSummary payload) {
		this.outcome = outcome;
		this.errorMessage = errorMessage;
		this.payload = payload;
	}

	public String getJsonString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
	
	/**
	 * Success in this case means nothing exploded. It does not mean that the ID in the request existed
	 * or that there is a payload.
	 * 
	 * @return	<code>true</code> if no errors/exceptions occurred, <code>false</code> otherwise.
	 */
	public boolean isSuccess() {
		return outcome.isSuccess();
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public SharedDatasetSummary getPayload() {
		return payload;
	}

	public boolean isPendingPublish() {
		return outcome.isPendingPublish();
	}
	
	public static ResponseGetDatasetSummary newInstanceFailure(String errorMessage) {
		return new ResponseGetDatasetSummary(Outcome.FAILURE, errorMessage, null);
	}
	
	public static ResponseGetDatasetSummary newInstancePendingPublish(SharedDatasetSummary partiallyPopulatedSummary) {
		return new ResponseGetDatasetSummary(Outcome.SUCCESS_APPROVED, null, partiallyPopulatedSummary);
	}
	
	public static ResponseGetDatasetSummary newInstanceSuccess(SharedDatasetSummary source) {
		return new ResponseGetDatasetSummary(Outcome.SUCCESS_PUBLISHED, null, source);
	}
}
