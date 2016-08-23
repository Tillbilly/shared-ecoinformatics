package au.edu.aekos.shared.cache.image;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import au.edu.aekos.shared.data.dao.AnswerImageDao;
import au.edu.aekos.shared.data.entity.AnswerImage;
import au.edu.aekos.shared.service.submission.SubmissionAnswerService;
import au.edu.aekos.shared.upload.SharedImageFileSupport;


/**
 * Old school style image cache.  If image not found in the cache it is loaded ( along with any other images for the submission )
 * from the submission management db ( postgres ) -- for now.  Might ultimately be backed by an object store.
 * 
 * Added support for images from multiselect image answers
 * 
 * @author btill
 *
 */
@Component
public class SubmissionImageCache {

	private static final String CACHE_NAME= "submission_image_cache";
	
	private Cache imageCache = null;
	
	@Autowired
	private AnswerImageDao answerImageDao;
	
	@Autowired
	private SubmissionAnswerService submissionAnswerService;
	
	private Cache getCache(){
		if(imageCache == null){
		    CacheManager cacheManager = CacheManager.getInstance();
		    imageCache = cacheManager.getCache(CACHE_NAME);
		}
		return imageCache;
	}
	
	public CachedSubmissionImage getImage(Long submissionId, String questionId, String imageNameId){
		ImageCacheKey key = new ImageCacheKey(submissionId,questionId, imageNameId);
		Element el = getCache().get(key);
		if(el != null){
		    return (CachedSubmissionImage) el.getObjectValue();
		}else{
			AnswerImage answerImage = answerImageDao.getAnswerImage(submissionId, questionId);
			if(answerImage == null){
				//check if we are dealing with a multiselect image answer
				answerImage = submissionAnswerService.retrieveAnswerImageForMultiselectImageAnswer(submissionId, questionId, imageNameId );
			}
			if(answerImage != null && answerImage.containsImageId(imageNameId)){
				//May as well add thumb and full size images to the cache while we have the entity.
				CachedSubmissionImage cachedImageObject = addImageToCache(submissionId, questionId, answerImage.getImageObjectId(), 
						                                                  SharedImageFileSupport.getFileSuffix(answerImage.getImageName()), answerImage.getImage());
				CachedSubmissionImage cachedImageThumb = addImageToCache(submissionId, questionId, answerImage.getImageThumbnailId(), 
                        SharedImageFileSupport.getFileSuffix(answerImage.getImageName()), answerImage.getImageThumbnail());
				return imageNameId.equals(answerImage.getImageObjectId() ) ? cachedImageObject : cachedImageThumb ;
			}
		}
		return null;
	}
	
	public CachedSubmissionImage addImageToCache(Long submissionId, String questionId, String imageName, String imageType, byte[] imageBytes){
		ImageCacheKey key = new ImageCacheKey(submissionId,questionId, imageName);
		CachedSubmissionImage csi = new CachedSubmissionImage(imageBytes, imageType);
		Element el = new Element(key, csi);
		getCache().put(el);
		return csi;//(CachedSubmissionImage) getCache().get(key).getObjectValue();
	}
	
}
