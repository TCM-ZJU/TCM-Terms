package ccnt.server;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import ccnt.client.service.GetContentService;
import ccnt.client.view.ViewType;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class GetContentServlet extends RemoteServiceServlet 
	implements GetContentService {
	
	/***
	 * 精确查询概念词
	 * @param 查询词汇
	 * @return Concept List
	 */
	public List getConcepts(String word) {
		try {
      long t1,t2;  
      t1=(new Date()).getTime();
      System.out.println("call at " + t1);
      
			List<String> concepts = ConceptEngine.getConcept(word);
			
			t2=(new Date()).getTime();
      System.out.println("精确查询'"+word+"的时间：" + (t2-t1)+"毫秒");
      System.out.println(concepts.size()+"个结果");
			logger.info("精确查询'"+word+"的时间：" + (t2-t1)+"毫秒");
      return concepts;
		} catch (Exception e) {
      e.printStackTrace();
		}
		return null;
	}
	
	/***
	 * 模糊查询概念词
	 * @param 查询词汇，是否模糊查询
	 * @return Concept List
	 */
	public List getConcepts(String word, int fuzzyRange) {
		try {
      long t1,t2;  
      t1=(new Date()).getTime();
      System.out.println("call at " + t1);
      
			List<String> concepts = ConceptEngine.getConcept(word, fuzzyRange);
			
			t2=(new Date()).getTime();
      System.out.println("模糊查询'"+word+"的时间：" + (t2-t1)+"毫秒");
      System.out.println(concepts.size()+"个结果");
			logger.info("模糊查询'"+word+"的时间：" + (t2-t1)+"毫秒");
      return concepts;
		} catch (Exception e) {
      e.printStackTrace();
		}
		return null;
	}

	/***
	 * 获得内容
	 * @param 概念词，查询域xml，例如：<父类 type='2' tier='3'/>
	 * @return 结果xml，例如：<results><定义 db="国际十表分类法">哈哈</定义></results>
	 */
	@SuppressWarnings("unchecked")
  public String getContents(String concept, String queryString) {
		Document xmlDoc;
    try {
	    xmlDoc = DocumentHelper.parseText(queryString);
			Element root = xmlDoc.getRootElement();
			String property = root.getName();
			int type = Integer.parseInt(root.attributeValue("type"));
			if(type == ViewType.TREE){
				int tier = Integer.parseInt(root.attributeValue("tier"));
				return ConceptEngine.getContent(concept, property, tier).asXML();
			}
			else
				return ConceptEngine.getContent(concept, property).asXML();
    } catch (DocumentException e) {
    	logger.error(concept+" "+queryString);
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	    return "Document Exception";
    }    
  }

	private final static Logger logger = Logger.getLogger(GetContentServlet.class);

}
