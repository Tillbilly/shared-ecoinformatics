package au.edu.aekos.shared.service.file;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import au.edu.aekos.shared.questionnaire.ImageAnswer;
import au.edu.aekos.shared.web.util.SharedFileUtils;

@Service
public class ImageService {
    
	private static final Logger logger = LoggerFactory.getLogger(ImageService.class);
	
	@Value("${shared.upload.tempimage.path}")
	private String imagePath;
	
	public void deleteImageAnswerFiles(ImageAnswer imageAnswer){
		if(StringUtils.hasLength( imageAnswer.getImageObjectName() ) ){
			try {
				SharedFileUtils.deleteFile(imageAnswer.getImageObjectName(), imagePath);
			} catch (IOException e) {
				logger.error("Attempt to delete " + imagePath + imageAnswer.getImageObjectName() + " failed");
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		}
		if(StringUtils.hasLength( imageAnswer.getImageThumbnailName() ) ){
			try {
				SharedFileUtils.deleteFile(imageAnswer.getImageThumbnailName(), imagePath);
			} catch (IOException e) {
				logger.error("Attempt to delete " + imagePath + imageAnswer.getImageThumbnailName() + " failed");
				logger.error(e.getMessage());
				e.printStackTrace();
			}
			
		}
	}
}
