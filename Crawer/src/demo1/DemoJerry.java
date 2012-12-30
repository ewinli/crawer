package demo1;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.Security;

import sun.misc.Signal;
import sun.security.provider.MD5;

import jodd.Jodd;
import jodd.http.Http;
import jodd.http.HttpTransfer;
import jodd.jerry.Jerry;
import jodd.jerry.Jerry.JerryParser;
import jodd.lagarto.dom.Node;
import jodd.petite.meta.PetiteBean;
import jodd.petite.scope.DefaultScope;
import jodd.petite.scope.SingletonScope;


@PetiteBean()
public class DemoJerry {
	
   
	
   public static void main(String[] args) {
	  JerryParser jerryParser=Jerry.jerry();
	  jerryParser.getDOMBuilder().setIgnoreComments(true);
	  try {
		HttpTransfer request=Http.createRequest("GET", "http://news.163.com/12/1228/08/8JQ37HLJ00014JB6.html");
	    
		Socket socket=new Socket(request.getHost(),request.getPort());
		OutputStream outputStream=socket.getOutputStream();
		request.send(outputStream);
		InputStream inputStream=socket.getInputStream();	     
		 HttpTransfer getRequest= Http.readResponse(inputStream);	

		 String content= new String(getRequest.getBody(),"gbk");
		 socket.close();
         Jerry jerry= jerryParser.parse(content);
         Jerry jerry2= jerry.$("meta");
         Node[] nodes=jerry2.get();
         for(Node node : nodes){
        	 System.out.println(node.getAttribute("content"));
         }
      
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	  
   }
}
