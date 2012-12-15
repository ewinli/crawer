package demo1;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class DemoV8 {
    
	
	public static void main(String[] args) {
	   	ScriptEngineManager script=new ScriptEngineManager();
	   	ScriptEngine sn= script.getEngineByName("jav8");
	   	try {
			sn.eval("print('hello world');");
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	
}
