package au.edu.aekos.shared.service.rifcs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.jcraft.jsch.JSchException;

import au.edu.aekos.shared.upload.ScpUtility;

@Service
public class RifcsFileServiceImpl implements RifcsFileService {
	
	private Logger logger = LoggerFactory.getLogger(RifcsFileServiceImpl.class);
	
	@Value("${rifcs.xmlFileOutputDir}")
	private String xmlFileOutputDir;
	
	@Value("${rifcs.overwriteExistingFiles}")
	private Boolean overwriteExistingFiles = Boolean.TRUE;
	
	@Value("${rifcs.oaiserver.ip}" )
	private String oaiserverIp;
	
	@Value("${rifcs.oaiserver.username}")
	private String oaiserverUsername;
			
	//Location of the pem file
	@Value("${rifcs.oaiserver.privatekey}")
	private String oaiserverPrivateKey;
			
	//Location of the document directory on the remote server
	@Value("${rifcs.oaiserver.docdir}")		
	private String oaiserverDocDir;
	
	public File writeXmlToLocalFile(String xmlString, String fileName) throws IOException{
		String filepath = xmlFileOutputDir + "/" + fileName;
		File f = new File(filepath);
		if(f.exists() ){
			if(overwriteExistingFiles){
				logger.info(f.getPath() + " exists, overwriting.");
			}else{
				logger.info(f.getPath() + " exists, skipping (aekos.rifcs.overwriteExistingFiles=false)");
				return f;
			}
		}
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF8"));
		writer.write(formatXml(xmlString));
		writer.flush();
		writer.close();
		return f;
	}
	
	public boolean scpToOaiServer(File localFileHandle) throws IOException, JSchException{
		String remoteFilePath = oaiserverDocDir + "/" + localFileHandle.getName();
		return ScpUtility.tryScpXTimes(5, 1000, localFileHandle.getPath(), remoteFilePath, oaiserverPrivateKey, oaiserverUsername, oaiserverIp);
	}
	
	private String formatXml(String unformattedXml){
		try {
	        Source xmlInput = new StreamSource(new StringReader(unformattedXml));
	        StringWriter stringWriter = new StringWriter();
	        StreamResult xmlOutput = new StreamResult(stringWriter);
	        TransformerFactory transformerFactory = TransformerFactory.newInstance();
	        transformerFactory.setAttribute("indent-number", 2);
	        Transformer transformer = transformerFactory.newTransformer(); 
	        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	        transformer.transform(xmlInput, xmlOutput);
	        String formattedXml = xmlOutput.getWriter().toString();
	        return formattedXml.replaceFirst("\\?>", "\\?>\n");
	    } catch (Exception e) {
	        throw new RuntimeException(e); // simple exception handling, please review it
	    }
	}
}
