package au.edu.aekos.shared.system;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class UserCountSharedStatsGenerator implements SharedStatsGenerator {

	@Autowired
	@Qualifier("dataSource")
	private DataSource dataSource;
	
	@Override
	public SharedStatsResult generateStat() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		int userAccountsCount = jdbcTemplate.queryForObject("SELECT count(*) FROM shared_user", Integer.class);
		return new SharedStatsResult("Number of user accounts", userAccountsCount);
	}
}
