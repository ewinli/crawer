package demo1;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import jodd.http.Http;
import jodd.http.HttpTransfer;
import jodd.jerry.Jerry;
import jodd.jerry.Jerry.JerryParser;
import jodd.lagarto.dom.Node;

public class DemoJerry {
	
   public static void main(String[] args) {
	  JerryParser jerryParser=Jerry.jerry();
	  jerryParser.getDOMBuilder().setIgnoreComments(true);
	  try {
		HttpTransfer request=Http.createRequest("GET", "http://www.cnbeta.com");
	    
		Socket socket=new Socket(request.getHost(),request.getPort());
		OutputStream outputStream=socket.getOutputStream();
		request.send(outputStream);
		InputStream inputStream=socket.getInputStream();	     
		 HttpTransfer getRequest= Http.readResponse(inputStream);		
		 String content= new String(getRequest.getBody(),"utf-8");

		socket.close();
         Jerry jerry= jerryParser.parse(content);
         Jerry jerry2= jerry.$("a");
         Node[] nodes=jerry2.get();
         
         for (int i = 0; i < nodes.length; i++) {
          if(nodes[i].getTextContent()!=null)
			System.out.println(nodes[i].getAttribute("href"));
		}
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	  
   }
}
