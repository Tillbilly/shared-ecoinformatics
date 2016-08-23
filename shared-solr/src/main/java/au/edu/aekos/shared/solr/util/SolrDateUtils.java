package au.edu.aekos.shared.solr.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.solr.common.util.DateUtil;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

//TODO Need to convert to using the DateUtil.getThreadLocalDateFormat().format(dayMin)
//Something changed between 4.3 and 4.5

public class SolrDateUtils {

	public static final String SOLR_DATE_FMT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	
	public static SimpleDateFormat getSimpleDateFormat(){
		return new SimpleDateFormat(SOLR_DATE_FMT);
	}
	
	public static DateTimeFormatter getDateTimeFormatter(){
		return DateTimeFormat.forPattern(SOLR_DATE_FMT);
	}
	
	public static String getDayDateRangeString(Date day){
		return getDayDateRangeString(day, day, null);
	}
	
	public static String getDayDateRangeString(Date day, DateTimeFormatter dtf){
	    return getDayDateRangeString(day, day, dtf);
	}
	
	public static String getDayDateRangeString(Date startDate, Date endDate) {
		return getDayDateRangeString(startDate,endDate, null);
	}
	
	public static String getDayDateRangeString(Date startDate, Date endDate, DateTimeFormatter dtf){
		if(dtf == null){
			dtf = getDateTimeFormatter();
		}
		DateTime startDateTime = new DateTime(startDate);
		DateTime startMidnight = startDateTime.dayOfMonth().roundFloorCopy();
		
		DateTime endDateTime = new DateTime(endDate);
		DateTime endMilliBeforeMidnight = endDateTime.dayOfMonth().roundFloorCopy().plusDays(1).minusMillis(1);
		StringBuilder builder = new StringBuilder("[");
		builder.append(dtf.print(startMidnight))
		.append(" TO ").append(dtf.print(endMilliBeforeMidnight)).append("]");
		return builder.toString();
	}
	
}
