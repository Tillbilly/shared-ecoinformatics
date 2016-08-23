package au.edu.aekos.shared.service.rifcs;

import java.io.File;
import java.io.IOException;

import com.jcraft.jsch.JSchException;

public interface RifcsFileService {
	
	File writeXmlToLocalFile(String xmlString, String fileName) throws IOException;
	
	boolean scpToOaiServer(File localFileHandle) throws IOException, JSchException;

}
