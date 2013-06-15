package ccnt.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PropertyServlet extends HttpServlet {
	/**
   * Dart Search 使用的Web Service, 获得概念词的属性注释
   */
  private static final long serialVersionUID = 653822359297302788L;	

  public void doGet(HttpServletRequest req, HttpServletResponse resp) {
  	String word = req.getParameter("word");
  	String property = req.getParameter("property");
    try {
	    word = java.net.URLDecoder.decode(req.getParameter("word"),"UTF-8");
	    property = java.net.URLDecoder.decode(req.getParameter("property"),"UTF-8");
    } catch (UnsupportedEncodingException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
    }
    System.out.println("get " + property + " of " + word);
		String html = ConceptEngine.getContent(word,property).asXML();
		
		resp.setContentType("text/html;	charset=GBK");
		PrintWriter ou;
		try {
			ou = new PrintWriter(resp.getOutputStream());
			ou.write(html);
			ou.flush();
			ou.close();
		} catch (IOException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
	}
  
	public static void main(String args[]){
		ConceptEngine.getContent("山大黄","定义").asXML();    
		ConceptEngine.getContent("山大黄","关联词").asXML();
	}

}
