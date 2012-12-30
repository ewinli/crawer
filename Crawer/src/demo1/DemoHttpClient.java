package demo1;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Scanner;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;

import org.apache.http.impl.client.DefaultHttpClient;


public class DemoHttpClient {
	

	
	
  public static void main(String args[]){
	  HttpClient client=new DefaultHttpClient();
	  HttpGet get=new HttpGet("http://news.163.com/12/1228/08/8JQ37HLJ00014JB6.html");
     try {
		HttpResponse response= client.execute(get);
	    HttpEntity entity=response.getEntity();
	    Header header=entity.getContentType();
	    String encode="UTF-8";
	    if(header!=null){
	    	int index= header.getValue().lastIndexOf("=");
	    	encode=header.getValue().substring(index+1);
	    }
	    
	    InputStream inputStream= entity.getContent();
	    BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream,encode));
	    String line= reader.readLine();
	    while (line!=null) {
			System.out.println(line);		
			 line= reader.readLine();
		}
	  
	    inputStream.close();
	    
	    
	} catch (ClientProtocolException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

  }
}
