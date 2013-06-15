
import ccnt.server.CorrelativeServlet;
import ccnt.server.PropertyServlet;
import ccnt.server.ThesaurusServlet;

import com.meterware.httpunit.GetMethodWebRequest;   
import com.meterware.httpunit.WebRequest;   
import com.meterware.httpunit.WebResponse;   
import com.meterware.servletunit.ServletRunner;   
import com.meterware.servletunit.ServletUnitClient;  
import junit.framework.TestCase;   
    
public class ServletTst extends TestCase {   
    
	protected void setUp() throws Exception {   
	  super.setUp();   
	}   
    
	protected void tearDown() throws Exception {   
		super.tearDown();   
	}   
    
	public void testThesaurus(String word) {
		try {   
			ServletRunner sr = new ServletRunner();// 创建Servlet的运行环境			
			sr.registerServlet("Thesaurus", ThesaurusServlet.class.getName());// 向环境中注册Servlet          
			ServletUnitClient sc = sr.newClient(); // 创建访问Servlet的客户端   			
			WebRequest request = new GetMethodWebRequest("http://localhost/Thesaurus");// 发送请求
			
			System.out.println(word);
//			word = java.net.URLEncoder.encode(word,"ISO8859_1");//UTF-8
			request.setParameter("word", word);
			System.out.println(request.getParameter("word"));
			System.out.println(request.toString());			
  
//		import com.meterware.servletunit.InvocationContext;   
//		import junit.framework.Assert;   
			//InvocationContext ic = sc.newInvocation(request);  
			//ThesaurusServlet is = (ThesaurusServlet) ic.getServlet();     
			// 测试servlet的某个方法    
			//Assert.assertTrue(is.authenticate());    
			// 获得模拟服务器的信息   
			WebResponse response = sc.getResponse(request);   
			// 断言   
			//Assert.assertTrue(response.getText().equals("testuser:Hello World!"));
			System.out.println(response.getText());
    } catch (Exception e) {   
     e.printStackTrace();   
	  }
	}   
	
	public void testProperty(String word,String property) {
		try {   
			ServletRunner sr = new ServletRunner();   
			sr.registerServlet("Property", PropertyServlet.class.getName());   
			ServletUnitClient sc = sr.newClient();   
			WebRequest request = new GetMethodWebRequest("http://localhost/Property");
			
			word = java.net.URLEncoder.encode(word,"UTF-8");
			property = java.net.URLEncoder.encode(property,"UTF-8");
			request.setParameter("word", word);
			request.setParameter("property", property);
			System.out.println(request.toString()); 
			
			WebResponse response = sc.getResponse(request); 
			System.out.println("[Test Result]" + response.getText());
    } catch (Exception e) {
     e.printStackTrace();   
	  }
	}
	
	public void testCorrelative(String word) {
		try {   
			ServletRunner sr = new ServletRunner();   
			sr.registerServlet("Correlative", CorrelativeServlet.class.getName());   
			ServletUnitClient sc = sr.newClient();   
			WebRequest request = new GetMethodWebRequest("http://localhost/Correlative");
			
			word = java.net.URLEncoder.encode(word,"UTF-8");
			request.setParameter("word", word);
			System.out.println(request.toString()); 
			
			WebResponse response = sc.getResponse(request); 
			System.out.println("[Test Result]" + response.getText());
    } catch (Exception e) {
     e.printStackTrace();   
	  }
	}
 
	public static void main(String[] args){
		ServletTst tst = new ServletTst();
		tst.testThesaurus("大黄");
		//tst.testProperty("大黄", "父类");
		//tst.testCorrelative("大黄");
	}
}
