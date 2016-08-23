package au.edu.aekos.shared.upload;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import au.edu.aekos.shared.cache.image.CachedSubmissionImage;
import au.edu.aekos.shared.cache.image.SubmissionImageCache;
import au.edu.aekos.shared.questionnaire.DisplayQuestionnaire;
import au.edu.aekos.shared.questionnaire.ImageAnswer;
import au.edu.aekos.shared.questionnaire.ImageSeriesAnswer;
import au.edu.aekos.shared.web.controllers.NewSubmissionController;

@Controller
public class ImageController {

	private static final int MAX_THUMB_HEIGHT_PX = 160;
	private static final int MAX_THUMB_WIDTH_PX = 160;

	@Value("${shared.upload.tempimage.path}")
	private String imagePath;
	
	@Autowired
	private SubmissionImageCache submissionImageCache;
	
	private static final Logger logger = LoggerFactory.getLogger(ImageController.class);
	
	@RequestMapping(value = "/uploadImagePopup", method = RequestMethod.GET)
    public String imageUploadPopup(@RequestParam(required=true, value="questionId") String questionId, 
    		                       @RequestParam(required=true, value="responseType") String responseType, 
    		                       Model model) throws IOException {
		model.addAttribute("questionId", questionId);
		model.addAttribute("responseType", responseType);
		return "imageUploadPopup";
	}
	
	@RequestMapping(value = "/uploadImagePopupMulti", method = RequestMethod.GET)
    public String imageUploadPopupMulti(@RequestParam(required=true, value="questionId") String questionId, 
    		                       @RequestParam(required=true, value="responseType") String responseType,
    		                       @RequestParam(required=true, value="index") String index,
    		                       Model model) throws IOException {
		model.addAttribute("questionId", questionId);
		model.addAttribute("responseType", responseType);
		model.addAttribute("multiIndex", index);
		return "imageUploadPopup";
	}
	
	@RequestMapping(value = "/questionImageUpload", method = RequestMethod.POST)
    public String questionImageUploadAjax(@RequestParam("file") CommonsMultipartFile file, 
    		                  @RequestParam("description") String description,
    		                  @RequestParam("questionId") String questionId,
    		                  @RequestParam(required=false,value="multiIndex") Integer multiIndex,
    		                  Model model,
    		                  HttpSession session,
    		                  HttpServletResponse resp) throws IOException {
		resp.setContentType("application/json");
        if (!file.isEmpty() && SharedImageFileSupport.checkImageTypeSupported(file)) {
        	logger.info("processing " + file.getOriginalFilename() + " for " + questionId );
        	
        	String imageKey = UUID.randomUUID().toString();
        	String suffix = SharedImageFileSupport.getFileSuffix( file.getOriginalFilename() ).toLowerCase();
        	String thumbKey = imageKey + SharedImageFileSupport.THUMBNAIL_SFX;
        	String thumbImgFileName = thumbKey + "." + suffix;
        	String imgFileName  = imageKey + "." + suffix;
        	
        	logger.info("Creating thumbnail " + imagePath + thumbImgFileName );
        	SharedImageFileSupport.createThumbnailImage(file, thumbImgFileName, imagePath, suffix, MAX_THUMB_WIDTH_PX, MAX_THUMB_HEIGHT_PX);
        	logger.info("Creating image file " + imagePath + imgFileName );
        	SharedImageFileSupport.saveImage(file, imgFileName, imagePath);
        	
        	UploadImageJsonResponse jsonResponse 
        	    = new UploadImageJsonResponse("ok",imgFileName,file.getOriginalFilename(),thumbImgFileName, new Long(file.getBytes().length));
        	resp.getWriter().print(jsonResponse.getJsonString());        
            //Now try update the DisplayQuestionnairein the session
        	if(multiIndex == null){
        	    writeImageDetailsToSessionQuestionnaire(session,description,questionId, file.getOriginalFilename(), imgFileName, thumbImgFileName );
        	}else{
        		writeImageDetailsToSessionQuestionnaireMulti(session,description,questionId, multiIndex, file.getOriginalFilename(), imgFileName, thumbImgFileName );
        	}
       } else {
    	   UploadImageJsonResponse jsonResponse = new UploadImageJsonResponse("Image file upload error.");
    	   resp.getWriter().print(jsonResponse.getJsonString());
       }
       resp.flushBuffer();
       return null;
    }
	
	private void writeImageDetailsToSessionQuestionnaire(HttpSession session, String description, String questionId, String origFileName, String imageName, String imageThumbName ){
		DisplayQuestionnaire questionnaire =  (DisplayQuestionnaire) session.getAttribute( NewSubmissionController.SESSION_QUESTIONNAIRE );
		if(questionnaire != null){
			ImageAnswer imageAnswer = new ImageAnswer(questionId, description,
					origFileName, imageName, imageThumbName);
			questionnaire.getImageAnswerMap().put(questionId, imageAnswer);
		}
	}
	
	private void writeImageDetailsToSessionQuestionnaireMulti(HttpSession session, String description, String questionId, Integer multiIndex, String origFileName, String imageName, String imageThumbName ){
		DisplayQuestionnaire questionnaire =  (DisplayQuestionnaire) session.getAttribute( NewSubmissionController.SESSION_QUESTIONNAIRE );
		if(questionnaire != null){
			if( ! questionnaire.getImageSeriesAnswerMap().containsKey(questionId) ){
				questionnaire.getImageSeriesAnswerMap().put(questionId, new ImageSeriesAnswer());
			}
			ImageAnswer imageAnswer = new ImageAnswer(questionId, multiIndex, description, 
					origFileName, imageName, imageThumbName);
			if(questionnaire.getImageSeriesAnswerMap().get(questionId).getImageAnswerList().size() < multiIndex){
				//pad the multilist with null placeholders
				for(int x = questionnaire.getImageSeriesAnswerMap().get(questionId).getImageAnswerList().size(); x < multiIndex; x++){
					questionnaire.getImageSeriesAnswerMap().get(questionId).getImageAnswerList().add(null);				
				}				
			}
			questionnaire.getImageSeriesAnswerMap().get(questionId).getImageAnswerList().add(multiIndex, imageAnswer);
			
		}
	}
	
	@RequestMapping(value = "/getImage", method = RequestMethod.GET)
    public String getImage(@RequestParam(value="imageId",required=true) String imageId, HttpServletResponse resp) throws IOException {
		String sfx = SharedImageFileSupport.getFileSuffix(imageId);
		File imageFile = new File( imagePath +  imageId );
		if(! imageFile.exists() || ! imageFile.canRead() ){
			resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Image " + imageId + " not Found on this server.");
		}
		setResponseContentTypeBasedOnImageSuffix(sfx, resp );
		InputStream fileStream = new FileInputStream(imageFile);
		OutputStream os = resp.getOutputStream();
		IOUtils.copy(fileStream, os);
		resp.flushBuffer();
		fileStream.close();
		os.close();
		return null;
	}
	
	/**
	 * Do not change this request path 
	 * ( or if you do, update the addLinkToSubmissionSummaryRow method in au.edu.aekos.shared.service.integration.SubmissionInfoModelSummaryServiceImpl )
	 * ( and update ImageLinkWriter )
	 * @param submissionId
	 * @param questionId
	 * @param imageId
	 * @param resp
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/getSubmissionImage", method = RequestMethod.GET)
    public String getSubmissionImage(@RequestParam(value="submissionId",required=true) Long submissionId, 
    		                         @RequestParam(value="questionId",required=true) String questionId, 
    		                         @RequestParam(value="imageId",required=true) String imageId, 
    		                         HttpServletResponse resp) throws IOException {
		String sfx = SharedImageFileSupport.getFileSuffix(imageId);
		CachedSubmissionImage cachedImage = submissionImageCache.getImage(submissionId, questionId, imageId);
		if(cachedImage != null){
			setResponseContentTypeBasedOnImageSuffix(sfx, resp );
			InputStream imageByteStream = new ByteArrayInputStream(cachedImage.getImageBytes());
			OutputStream os = resp.getOutputStream();
			IOUtils.copy(imageByteStream, os);
			resp.flushBuffer();
			imageByteStream.close();
			os.close();
		}
		return null;
	}
	
	private void setResponseContentTypeBasedOnImageSuffix(String suffix, HttpServletResponse resp ){
		String lcSfx = suffix.toLowerCase();
		if("jpg".equals(lcSfx) || "jpeg".equals(lcSfx) ){
			resp.setContentType("image/jpeg");
		}else if("png".equals(lcSfx)){
			resp.setContentType("image/png");
		}else if("gif".equals(lcSfx)){
			resp.setContentType("image/gif");
		}else if("bmp".equals(lcSfx)){
			resp.setContentType("image/bmp");
		}
		
	}

}
