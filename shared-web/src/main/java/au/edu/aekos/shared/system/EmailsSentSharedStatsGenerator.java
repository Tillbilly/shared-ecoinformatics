package au.edu.aekos.shared.system;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class EmailsSentSharedStatsGenerator implements SharedStatsGenerator {

	@Autowired
	@Qualifier("dataSource")
	private DataSource dataSource;
	
	@Override
	public SharedStatsResult generateStat() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		int emailsSentCount = jdbcTemplate.queryForObject("SELECT count(*) FROM email_notifications", Integer.class);
		return new SharedStatsResult("Number of emails sent", emailsSentCount);
	}
}
