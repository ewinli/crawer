package lec.crawer.algo.rdab.ext;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;



import jodd.bean.BeanUtil;
import jodd.jerry.Jerry;
import jodd.util.ClassLoaderUtil;

public class ExtManager {
	 public static Class[] PluginList=new Class[]{
		 
	 };
	 
	 public static Map<String,IParseExt> instanceMap=new HashMap<String,IParseExt>();
	 
     public static <T> IParseExt<T> getParseExt(Class<T> clazz,Jerry document,String name) {
    	 IParseExt<T> ext=instanceMap.get(name);
    	 if(ext!=null){
    		 return ext;
    	 }
    	 for(Class plugin:PluginList){
    	    if(plugin.isInstance(IParseExt.class)){
    	    	if(plugin.isAnnotationPresent(Ext.class)){
    	    	   Ext ann= (Ext)plugin.getAnnotation(Ext.class);
    	    	   if(ann.name().equals(name)){
    	    		   Method method;
					try {
						method = plugin.getMethod("getInstance", Jerry.class);
						instanceMap.put(name,(IParseExt) method.invoke(plugin, document));
					} catch (SecurityException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (NoSuchMethodException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}  	    		   
    	    		  catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    	    	   }
    	    	}    	    	
    	    }	 
    	 }
    	 return instanceMap.get(name);
     } 
     

     
}
