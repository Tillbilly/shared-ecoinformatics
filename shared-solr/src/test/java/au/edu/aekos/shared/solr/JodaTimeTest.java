package au.edu.aekos.shared.solr;

import org.junit.Assert;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;

import au.edu.aekos.shared.solr.util.SolrDateUtils;

/*
 * Lets test out JodaTime
 */
public class JodaTimeTest {
	
	@Test
	public void testCreateDateTimeAndPrintOutISOFormat(){
		DateTime t = new DateTime();
		System.out.println( t.toString() );
		
		DateTimeFormatter dtf = SolrDateUtils.getDateTimeFormatter();
		System.out.println(dtf.print(t) );
		
	}
	
	@Test
	public void testRoundDownAndUp(){
		DateTime t = new DateTime();
		DateTime day = t.dayOfMonth().roundFloorCopy();
		DateTimeFormatter dtf = SolrDateUtils.getDateTimeFormatter();
		//Some kind of test
		Assert.assertEquals(-1, day.compareTo(t) );
		System.out.println(dtf.print(day) );
		
		//Round up to midnight minus 1 millisecond
		DateTime roundedUp = t.dayOfMonth().roundFloorCopy().plusDays(1).minusMillis(1);
		Assert.assertTrue( t.isBefore(roundedUp) );
		System.out.println(dtf.print(roundedUp));
	}

}
