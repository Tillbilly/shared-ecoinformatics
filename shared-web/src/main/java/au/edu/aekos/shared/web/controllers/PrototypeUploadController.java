package au.edu.aekos.shared.web.controllers;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

@Controller
public class PrototypeUploadController {
	
	private static final Logger logger = LoggerFactory.getLogger(PrototypeUploadController.class);
	
	@RequestMapping(value = "/upload", method = RequestMethod.GET)
	public String uploadPage(){
		return "uploadPage";
	}
	
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String handleFormUpload(@RequestParam("name") String name, 
        @RequestParam("file") CommonsMultipartFile file, Model model) throws IOException {

        if (!file.isEmpty()) {
            byte[] bytes = file.getBytes();
            
            model.addAttribute("fileName", file.getOriginalFilename());
            model.addAttribute("fileSize", new Long( file.getSize()) );
            model.addAttribute("storageDescription", file.getStorageDescription()) ;
            // store the bytes somewhere
           return "uploadPage";
       } else {
           return "uploadPage";
       }
    }
	
	@RequestMapping(value = "/uploadStream", method = RequestMethod.GET)
	public String uploadPageStream(HttpSession session){
		session.setAttribute("session-test", "Hooray for Everything");
		
		
		return "uploadPageStream";
	}
	
	
	@RequestMapping(value = "/uploadStream", method = RequestMethod.POST)
    public String handleFormUploadStream(HttpServletRequest request, Model model) throws IOException, FileUploadException {

		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		
		// Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload();

		// Parse the request
		FileItemIterator iter = upload.getItemIterator(request);
		logger.info("I`m alive");
		while (iter.hasNext()) {
		    FileItemStream item = iter.next();
		    String name = item.getFieldName();
		    InputStream stream = item.openStream();
		    if (item.isFormField()) {
		    	logger.info("Form field " + name + " with value "
		            + Streams.asString(stream) + " detected.");
		    } else {
		    	logger.info("File field " + name + " with file name "
		            + item.getName() + " detected.");
		    }
		}
		
		
        
       return "uploadPageStream";
       
    }
	
	
	
	
	
	

}
