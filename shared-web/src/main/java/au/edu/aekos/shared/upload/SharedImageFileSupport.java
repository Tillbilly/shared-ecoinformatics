package au.edu.aekos.shared.upload;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import net.coobird.thumbnailator.Thumbnails;

import org.apache.commons.io.IOUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class SharedImageFileSupport {

	public static final String THUMBNAIL_SFX = "_tn";
	
	public static void createThumbnailImage(CommonsMultipartFile file, String imageFileName, String imagePath, String outputFormat, int sizeX, int sizeY) throws IOException{
		byte[] bytes = file.getBytes();
		BufferedImage buf = ImageIO.read(new ByteArrayInputStream(bytes));
		Thumbnails.of(buf)
		          .size(sizeX, sizeY)
		          .outputFormat(outputFormat)
		          .toFile(new File(imagePath + imageFileName) );
	}
	
	public static void saveImage(CommonsMultipartFile file, String imageFileName, String imagePath ) throws IOException{
		File f = new File(imagePath + imageFileName);
		OutputStream os = new FileOutputStream(f);
		IOUtils.copy(new ByteArrayInputStream(file.getBytes()), os);
		os.close();
	}
	
	public static boolean checkImageTypeSupported(CommonsMultipartFile file){
		return checkImageTypeSupported(file.getOriginalFilename());
	}
	
	public static boolean checkImageTypeSupported(String filename){
	    String sfx = SharedImageFileSupport.getFileSuffix(filename);
	    if(! StringUtils.hasLength(sfx)){
	    	return false;
	    }
	    sfx = sfx.toLowerCase();
	    for(String readerSfx : ImageIO.getReaderFileSuffixes()) {
	    	if(readerSfx.equals(sfx)){
	    		return true;
	    	}
	    }
	    return false;
	}
	
	public static String getFileSuffix(String filename){
		if(! StringUtils.hasLength(filename)){
			return "";
		}
		int indx = filename.lastIndexOf('.');
		if(indx != -1 && indx != filename.length() - 1 ){
			return filename.substring( indx + 1 );
		}
		return "";
	}
	
	public static byte[] readImageFileFromDiskIntoByteArray(String fileName, String fspath) throws IOException{
		File f = new File(fspath + fileName);
		InputStream is = new FileInputStream(f);
	    byte[] imageBytes = IOUtils.toByteArray(is);
	    is.close();
	    return imageBytes;
	}
	
}
