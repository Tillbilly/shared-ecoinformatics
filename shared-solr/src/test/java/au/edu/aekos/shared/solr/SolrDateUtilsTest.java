package au.edu.aekos.shared.solr;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Assert;

import org.junit.Test;

import au.edu.aekos.shared.solr.util.SolrDateUtils;

public class SolrDateUtilsTest {

	SimpleDateFormat testSdf = new SimpleDateFormat("yyyy/MM/dd");
	
	@Test
	public void testGetDayDateRangeString() throws ParseException{
		Date startDate = testSdf.parse("1999/03/22");
		Date endDate = testSdf.parse("2001/01/01");
		String rangeQuery = SolrDateUtils.getDayDateRangeString(startDate, endDate, null );
		Assert.assertEquals("[1999-03-22T00:00:00.000Z TO 2001-01-01T23:59:59.999Z]", rangeQuery);
	}
}
