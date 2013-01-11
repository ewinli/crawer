package lec.crawer.model;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import jodd.http.Http;
import jodd.http.HttpTransfer;
import jodd.util.StringUtil;
import lec.crawer.DownloadManager;

public class ImageItem extends UrlItem {

	private static final long serialVersionUID = 1L;

	private String contentType;
	
	private String imageType;
	
	private BufferedImage image;
	
	private static final String PNG="png";  
	
	private static final String JPG="jpg";  
	   
	private static final String BMP="bmp";  
	   
	private static final String GIF="gif";  
	
	private static final Map<String,String> map=new HashMap<String, String>();
	
	static{
		map.put("image/gif", "gif");
		map.put("image/x-icon", "icon");
		map.put("image/fax", "fax");
		map.put("image/jpeg", "jpg");
		map.put("image/pnetvue", "net");
		map.put("image/png", "png");
		map.put("image/vnd.rn-realpix", "rp");
		map.put("image/tiff", "tiff");
		map.put("image/vnd.wap.wbmp", "wbmp");
	}
	
	public ImageItem(String url) throws MalformedURLException {
		super(url);
	}
	
	public ImageItem get() throws IOException{
		HttpTransfer request = Http.createRequest("GET", getUrl());
		request.addHeader("User-Agent", DownloadManager.getUserAgent());	
		Socket socket = new Socket(request.getHost(), request.getPort());		
		OutputStream outputStream = socket.getOutputStream();
		request.send(outputStream);
		InputStream inputStream = socket.getInputStream();
		HttpTransfer response = Http.readResponse(inputStream);	
		byte[] array=response.getBody();
		InputStream buffin = new ByteArrayInputStream(array);
		image= ImageIO.read(buffin);					
		contentType= response.getHeader("Content-Type");
		if(!StringUtil.isEmpty(contentType)){
			imageType=map.get(contentType);
			if(StringUtil.isEmpty(imageType)){
				imageType=JPG;
			}
		}else{
			imageType=fastParseFileType(array);
		}
		return this;
	}
	
	
    public static String fastParseFileType(byte[] byte1) {  
        if ((byte1[0] == 71) && (byte1[1] == 73) && (byte1[2] == 70)  
                && (byte1[3] == 56) && ((byte1[4] == 55) || (byte1[4] == 57))  
                && (byte1[5] == 97)) {  
            return GIF;  
        }  
        if ((byte1[6] == 74) && (byte1[7] == 70) && (byte1[8] == 73)  
                && (byte1[9] == 70)) {  
            return JPG;  
        }  
        if ((byte1[0] == 66) && (byte1[1] == 77)) {  
            return BMP;  
        }  
        if ((byte1[1] == 80) && (byte1[2] == 78) && (byte1[3] == 71)) {  
            return PNG;  
        }  
        return JPG;  
    }; 

}
