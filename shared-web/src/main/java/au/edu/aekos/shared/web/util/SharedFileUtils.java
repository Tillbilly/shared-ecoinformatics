package au.edu.aekos.shared.web.util;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class SharedFileUtils {
	
	//Had some issues with getting files to and from the object store with long 'windowsy' file names.
	public static String cleanseUploadedFileName(String uploadedFileName){
		//Initially, don't truncate length.
		//Replace all spaces with underscores.
		//Remove any characters that are not a-zA-Z0-9 . - _ 
		String fileName = uploadedFileName.replaceAll(" ", "_");
		StringBuilder cleansedFilename = new StringBuilder();
		for(int x = 0; x < fileName.length(); x++){
			char c = fileName.charAt(x);
			if( Character.isLetterOrDigit(c) || c == '_' || c == '-' || c== '.'){
				cleansedFilename.append(c);
			}
		}
		return cleansedFilename.toString();
	}
	
	
	public static File createUniqueFile(String fileName, String fileSystemPath ) throws IOException{
		File f = new File(fileSystemPath + fileName );
		int suffix = 1;
		while(f.exists()){
			String incrementedFilename = SharedFileUtils.addIncrementToFileName(fileName, suffix);
			f = new File(fileSystemPath + incrementedFilename );
			suffix++;
		}
		f.createNewFile();
		return f;
	}
	
	public static String addIncrementToFileName(String fileName, int increment) {
		int periodIndex = fileName.lastIndexOf(".");
		if(periodIndex >= 0 ){
		    String name = fileName.substring(0,periodIndex);
		    String fileSuffix = fileName.substring(periodIndex);
		    return name + "-" + Integer.toString(increment) + fileSuffix;
		}
		return fileName + "-" + Integer.toString(increment);
	}
	
	public static void deleteFile(String fileName, String fileSystemPath ) throws IOException {
		File f = new File(fileSystemPath + fileName );
		if(f.exists()){
			f.delete();
		}
	}
	
	/**
	 * Taken from http://stackoverflow.com/a/3758880/1410035
	 * <pre><code>
	                              SI     BINARY
	
	                   0:        0 B        0 B
	                  27:       27 B       27 B
	                 999:      999 B      999 B
	                1000:     1.0 kB     1000 B
	                1023:     1.0 kB     1023 B
	                1024:     1.0 kB    1.0 KiB
	                1728:     1.7 kB    1.7 KiB
	              110592:   110.6 kB  108.0 KiB
	             7077888:     7.1 MB    6.8 MiB
	           452984832:   453.0 MB  432.0 MiB
	         28991029248:    29.0 GB   27.0 GiB
	       1855425871872:     1.9 TB    1.7 TiB
	 9223372036854775807:     9.2 EB    8.0 EiB   (Long.MAX_VALUE)
	 * </code></pre>
	 * 
	 * @param bytes		The value to convert
	 * @param si		true if you want SI units, false if you want binary units
	 * @return			A human readable size string, see table above for examples
	 */
	public static String humanReadableByteCount(long bytes, boolean si) {
	    int unit = si ? 1000 : 1024;
	    if (bytes < unit) return bytes + " B";
	    int exp = (int) (Math.log(bytes) / Math.log(unit));
	    String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
	    return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}
	
	//Help cleaning up orphaned submission files
	public static boolean isFileOlderThan(File file, int timeHours){
		if(! file.exists() || ! file.canRead()){
			return false;
		}
		//Work out the Date fromHours ago
		Calendar cal = GregorianCalendar.getInstance();
		cal.add(Calendar.HOUR, -timeHours);
		if( cal.getTimeInMillis() > file.lastModified() ){
			return true;
		}
		return false;
		
	}
	
	
	
	
}
