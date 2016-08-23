package au.edu.aekos.shared.upload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * This uses the Jsch library, and mostly copies their example.
 * 
 * For execution from a *nix environment, utilises ssh pem file private key
 * authorisation
 * 
 * @author btill
 */
public class ScpUtility {

	private static final Logger logger = LoggerFactory.getLogger(ScpUtility.class);
	
	/*
	 * Returns true if the scp is successful, false if it fails
	 */
	@SuppressWarnings("static-access")
	public static boolean scpTo(String sourceFilePath, String remoteFilePath,
			String pemFilePath, String user, String host) throws IOException,
			JSchException {
		
		logger.info("scp " + sourceFilePath + " to remote " + remoteFilePath );
		
		JSch jsch = new JSch();
		FileInputStream fis = null;

		try {
			jsch.addIdentity(pemFilePath);
			jsch.setConfig("StrictHostKeyChecking", "no");
			Session session = jsch.getSession(user, host, 22);
			session.connect();

			String command = "scp -t " + remoteFilePath;

			Channel channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);

			// get I/O streams for remote scp
			OutputStream out = channel.getOutputStream();
			InputStream in = channel.getInputStream();
			channel.connect();
			if(checkAck(in)!=0){
				return false;
			}
			File _lfile = new File(sourceFilePath);
			long filesize = _lfile.length();
			logger.info("Filesize " + Long.toString(filesize));
			command = "C0644 " + filesize + " ";
			if (sourceFilePath.lastIndexOf('/') > 0) {
				command += sourceFilePath.substring(sourceFilePath
						.lastIndexOf('/') + 1);
			} else {
				command += sourceFilePath;
			}
			command += "\n";
			out.write(command.getBytes());
			out.flush();
			if(checkAck(in)!=0){
				return false;
			}
			fis = new FileInputStream(_lfile);
			byte[] buf = new byte[1024];
			int byteCount = 0;
			while (true) {
				int len = fis.read(buf, 0, buf.length);
				if (len <= 0)
					break;
				byteCount++;
				out.write(buf, 0, len); 
				out.flush();
			}
			fis.close();
			fis = null;
			logger.info("Bytes sent = " + Integer.toString(byteCount * 1024));
			// send '\0'
			buf[0] = 0;
			out.write(buf, 0, 1);
			out.flush();
			if(checkAck(in)!=0){
				return false;
			}
			out.close();
			channel.disconnect();
			session.disconnect();
		} catch (IOException e) {
			logger.error("Error copying file", e);
			try {
				if (fis != null)
					fis.close();
			} catch (Exception ee) {
			}
			throw e;
		}
		return true;
	}
	
	public static void scpAllXmlFilesInDirectory(String sourceDir, String remoteDir,
			String pemFilePath, String user, String host) throws IOException,
			JSchException {
		File dir = new File(sourceDir);
		if(dir.isDirectory()){
		    File [] files = dir.listFiles(new FilenameFilter(){
		    	public boolean accept(File dir, String name) {
					return name.endsWith(".xml");
				}
		    });
		    if(files != null && files.length > 0){
		    	for(int x = 0; x < files.length; x++){
		    		File f = files[x];
		    		String filename = f.getName();
		    		String remoteFilePath = remoteDir + "/" + filename;
		    		//scpTo(f.getPath(), remoteFilePath, pemFilePath, user, host );
		    		tryScpXTimes(5,5000,f.getPath(), remoteFilePath, pemFilePath, user, host );
		    	}
		    }
		}
	}
	
	public static boolean tryScpXTimes(int numTimesToTry, 
			                        int sleepDurationMs, 
			                        String sourceFilePath, 
			                        String remoteFilePath,
			                        String pemFilePath, 
			                        String user, 
			                        String host   ) throws IOException, JSchException{
		int numTries = 0;
		boolean success = false;
		while(!success && numTries < numTimesToTry){
			if(scpTo(sourceFilePath,remoteFilePath, pemFilePath, user, host)){
				logger.info("File transferred successfully");
				return true;
			}
			numTries++;
			logger.info("scp failed " + Integer.toString(numTries) + " tries.");
			try {
				Thread.sleep(sleepDurationMs);
			} catch (InterruptedException e) {
				e.printStackTrace();
				logger.error("Sleep Error",e);
			}
		}
		//If we get to here the file transfer has failed!
		logger.error("Failed to transfer " + sourceFilePath + " after " + Integer.toString(numTries) + " tries.");
		return false;
	}

	static int checkAck(InputStream in) throws IOException {
		int b = in.read();
		// b may be 0 for success,
		// 1 for error,
		// 2 for fatal error,
		// -1
		if (b == 0) {
			return b;
		}
		if (b == -1) {
			return b;
		}

		if (b == 1 || b == 2) {
			StringBuffer sb = new StringBuffer();
			int c;
			do {
				c = in.read();
				sb.append((char) c);
			} while (c != '\n');
			if (b == 1) { // error
				logger.error(sb.toString());
			}
			if (b == 2) { // fatal error
				logger.error(sb.toString());
			}
		}
		return b;
	}
}
