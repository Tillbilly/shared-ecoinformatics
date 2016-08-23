package au.edu.aekos.shared.service.security;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

import au.edu.aekos.shared.web.model.ActiveSessionModel;

public class SecurityServiceImplTest {

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	/**
	 * Can we calculate the expiry of a session?
	 */
	@Test
	public void testCalculateActiveSessionExpiry01() {
		int sessionTimeoutSeconds = 6000;
		Date now = theDate("2016-04-04 10:00:00");
		String user = "some.user";
		Date lastRequest = theDate("2016-04-04 09:00:00");;
		ActiveSessionModel result = SecurityServiceImpl.calculateActiveSessionExpiry(sessionTimeoutSeconds, now, user, lastRequest);
		assertThat(result.getMinutesUntilTimeout(), is(40));
		assertThat(result.getTimeoutTime(), is(theDate("2016-04-04 10:40:00")));
	}

	private static Date theDate(String dateString) {
		try {
			return sdf.parse(dateString);
		} catch (ParseException e) {
			throw new RuntimeException("Failed to create a date", e);
		}
	}
}
