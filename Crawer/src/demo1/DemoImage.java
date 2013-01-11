package demo1;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.imageio.ImageIO;

import jodd.http.Http;
import jodd.http.HttpTransfer;
import jodd.io.FileUtil;
import lec.crawer.DownloadManager;

public class DemoImage {
    public static void main(String[] args) {
    	HttpTransfer request;
		try {
			request = Http.createRequest("GET", "http://ww4.sinaimg.cn/bmiddle/7143d82djw1e0k0vthe8aj.jpg");
			request.addHeader("User-Agent", DownloadManager.getUserAgent());	
			Socket socket = new Socket(request.getHost(), request.getPort());		
			OutputStream outputStream = socket.getOutputStream();
			request.send(outputStream);
			InputStream inputStream = socket.getInputStream();
			HttpTransfer response = Http.readResponse(inputStream);	
			byte[] array=response.getBody();
			InputStream buffin = new ByteArrayInputStream(array);
			BufferedImage image= ImageIO.read(buffin);			
			String contentType= response.getHeader("Content-Type");
			FileUtil.writeBytes("d:\\test.jpg", array);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
    }
}
