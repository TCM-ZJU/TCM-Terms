package ccnt.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class CorrelativeServlet extends HttpServlet {

	/**
   * Dart Query 使用的Web Service, 获得相关词，即由词获得以此词为概念词或异名的所有术语的所有域
   */
  private static final long serialVersionUID = 653822359297302766L;	

  public void doGet(HttpServletRequest req, HttpServletResponse resp) {
  	String word = req.getParameter("word");
  	try {
	    word = java.net.URLDecoder.decode(req.getParameter("word"),"UTF-8");
    } catch (UnsupportedEncodingException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
    }
    System.out.println("get correlatives of " + word);
		String html = getCorrelative(word);
		
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

  /**
   * 获得相关词，即由词获得以此词为概念词或异名的所有术语的所有域
   * @param word
   * @return 相关词XML字符串
   * 例如：搜索大黄的同义词，返回XML：
   * <correlatives>
   * 	<result>
   * 		<概念词>大黄</概念词>
   * 		<定义s>
   * 			<定义 db="中医临床术语集">为蓼科植物掌叶大黄，唐古特大黄或药用大黄的根茎。具有攻下、泻火的功效</定义>
   * 			<定义 db="中医药学名词">蓼科植物掌叶大黄、唐古特大黄或药用大黄的干燥根及根茎。</定义>
   * 		</定义s>
   * 		<异名s>
   * 			<异名 db="中医临床术语集">Dheum Officinale</异名>
   * 			<异名 db="中医临床术语集">肤如</异名>
   * 			<异名 db="中医临床术语集">黄良</异名>
   * 			<异名 db="中医临床术语集">da huang</异名>
   * 			<异名 db="中医临床术语集">牛舌大黄</异名>
   * 			<异名 db="中医临床术语集">川军</异名>
   * 			<异名 db="中医临床术语集">蜀大黄</异名>
   * 			<异名 db="中医临床术语集">锦纹</异名>
   * 			<异名 db="中医临床术语集">生军</异名>
   * 			<异名 db="中医临床术语集">火参</异名>
   * 			<异名 db="中医临床术语集">将军</异名>
   * 			<异名 db="中医临床术语集">锦纹大黄</异名>
   * 			<异名 db="中医药学主题词表">RHEUM OFFICINALE</异名>
   * 			<异名 db="中医药学名词">Rheum palmatum L.；Rheum tanguticum Maxim.ex Balf.；Rheum officinale Baill.</异名>
   * 			<异名 db="中医药学名词">Radix et Rhizoma Rhei（拉）；rhubarb root and rhizome</异名>
   * 			<异名 db="中医药学主题词表">川军</异名>
   * 			<异名 db="中医药学主题词表">锦纹</异名>
   * 		</异名s>
   * 		<父类s>
   * 			<父类 db="中医临床术语集">攻下药</父类>
   * 			<父类 db="中医临床术语集">大黄</父类>
   * 			<Tree db="中医药学名词">中药<Tree>中药学</Tree></Tree>
   * 		</父类s>
   * 		<子类s>
   * 			<子类 db="中医临床术语集">大黄</子类>
   * 		</子类s>
   * 		<关联词s>
   * 			<关联词 db="中医临床术语集" rela="上位词">攻下药</关联词>
   * 			<关联词 db="中医临床术语集" rela="…的特性">攻下</关联词>
   * 			<关联词 db="中医临床术语集" rela="…的特性">泻火</关联词>
   * 			<关联词 db="中医临床术语集" rela="…的部分">蓼科</关联词>
   * 		</关联词s>
   * 	</result>
   * 	<result>
   * 		<概念词>水黄</概念词>
   * 		<定义s>
   * 			<定义 db="中医临床术语集">为蓼科植物苞叶大黄的根，具有清热解毒、化瘀止血、泻下的功效</定义>
   * 		</定义s>
   * 		<异名s>
   * 			<异名 db="中医临床术语集">Rhizome And Root Of Alexander Rhubarb</异名>
   * 			<异名 db="中医临床术语集">shui huang</异名>
   * 			<异名 db="中医临床术语集">大黄</异名>
   * 			<异名 db="中医临床术语集">葛叶大黄</异名>
   * 		</异名s>
   * 		<父类s>
   * 			<父类 db="中医临床术语集">化瘀止血药</父类>
   * 			<父类 db="中医临床术语集">水黄</父类>
   * 		</父类s>
   * 		<子类s>
   * 			<子类 db="中医临床术语集">水黄</子类>
   * 		</子类s>
   * 		<关联词s>
   * 			<关联词 db="中医临床术语集" rela="…的部分">蓼科</关联词>
   * 			<关联词 db="中医临床术语集" rela="…的特性">清热解毒</关联词>
   * 			<关联词 db="中医临床术语集" rela="…的特性">泻下</关联词>
   * 			<关联词 db="中医临床术语集" rela="上位词">化瘀止血药</关联词>
   * 			<关联词 db="中医临床术语集" rela="…的特性">化瘀止血</关联词>
   * 		</关联词s>
   * 	</result>
   * 	<result>
   * 		<概念词>小大黄</概念词>
   * 		<定义s>
   * 			<定义 db="中医临床术语集">为蓼科植物矮大黄的全草。具有泻热、消消痈散结、泻热、破积的功效</定义>
   * 		</定义s>
   * 		<异名s>
   * 			<异名 db="中医临床术语集">Radix et Rhizoma Rhei</异名>
   * 			<异名 db="中医临床术语集">Root and Rhizome of Forrest Rhubarb</异名>
   * 			<异名 db="中医临床术语集">大黄</异名>
   * 			<异名 db="中医临床术语集">xiao da huang</异名>
   * 			<异名 db="中医临床术语集">白大黄</异名>
   * 			<异名 db="中医临床术语集">次大黄</异名>
   * 		</异名s>
   * 		<父类s>
   * 			<父类 db="中医临床术语集">泻热药</父类>
   * 			<父类 db="中医临床术语集">小大黄</父类>
   * 		</父类s>
   * 		<子类s>
   * 			<子类 db="中医临床术语集">小大黄</子类>
   * 		</子类s>
   * 		<关联词s>
   * 			<关联词 db="中医临床术语集" rela="…的特性">消痈散结</关联词>
   * 			<关联词 db="中医临床术语集" rela="上位词">泻热药</关联词>
   * 			<关联词 db="中医临床术语集" rela="…的特性">化瘀</关联词>
   * 			<关联词 db="中医临床术语集" rela="…的特性">破积</关联词>
   * 			<关联词 db="中医临床术语集" rela="…的部分">蓼科</关联词>
   * 			<关联词 db="中医临床术语集" rela="…的特性">泻热</关联词>
   * 		</关联词s>
   * 	</result>
   * 	<result>
   * 		<概念词>山大黄</概念词>
   * 		<定义s>
   * 			<定义 db="中医临床术语集">为蓼科植物波叶大黄的根及根茎，具有解毒、凉血、泄热的功效</定义>
   * 			<定义 db="中医药学语言系统">反了贝</定义>
   * 			<定义 db="中医药学语言系统">采用肌肉终末神经刺激的实验，证实可以引起顺向及逆向性两种冲动，逆向冲动可以兴奋终末神经侧枝另一转向肌纤维的神经，此即运动单位的轴突反射。</定义>
   * 		</定义s>
   * 		<异名s>
   * 			<异名 db="中医临床术语集">Radix et Rhizoma Rhei Franzenbachii</异名>
   * 			<异名 db="中医临床术语集">Root And Rhizome Of Franzenbach Rhubarb</异名>
   * 			<异名 db="中医临床术语集">黄古卵子</异名>
   * 			<异名 db="中医临床术语集">唐大黄</异名>
   * 			<异名 db="中医临床术语集">华北大黄</异名>
   * 			<异名 db="中医临床术语集">台黄</异名>
   * 			<异名 db="中医临床术语集">shan da huang</异名>
   * 			<异名 db="中医临床术语集">野大黄</异名>
   * 			<异名 db="中医临床术语集">祁黄</异名>
   * 			<异名 db="中医临床术语集">土大黄</异名>
   * 			<异名 db="中医临床术语集">大黄</异名>
   * 			<异名 db="中医临床术语集">酸酸草</异名>
   * 			<异名 db="中医临床术语集">峪黄</异名>
   * 			<异名 db="中医临床术语集">格西古呐？</异名>
   * 			<异名 db="中医临床术语集">籽黄</异名>
   * 			<异名 db="中医临床术语集">庄黄</异名>
   * 			<异名 db="中医临床术语集">北大黄</异名>
   * 			<异名 db="中医临床术语集">苦大黄</异名>
   * 			<异名 db="中医药学语言系统">Axon Reflex</异名>
   * 			<异名 db="中医药学语言系统">fan</异名>
   * 			<异名 db="中医药学语言系统">轴突反射</异名>
   * 		</异名s>
   * 		<父类s>
   * 			<父类 db="中医临床术语集">解毒药P-T</父类>
   * 			<父类 db="中医临床术语集">山大黄</父类>
   * 		</父类s>
   * 		<子类s>
   * 			<子类 db="中医临床术语集">山大黄</子类>
   * 		</子类s>
   * 		<关联词s>
   * 			<关联词 db="中医临床术语集" rela="…的部分">蓼科</关联词>
   * 			<关联词 db="中医临床术语集" rela="…的特性">解毒</关联词>
   * 			<关联词 db="中医临床术语集" rela="…的特性">泄热</关联词>
   * 			<关联词 db="中医临床术语集" rela="…的特性">凉血</关联词>
   * 			<关联词 db="中医临床术语集" rela="上位词">解毒药</关联词>
   * 			<关联词 db="中医药学语言系统" rela="上位词">反射</关联词>
   * 			<关联词 db="中医药学语言系统" rela="有---的方法">肌肉终末神经刺激实验</关联词>
   * 			<关联词 db="中医药学语言系统" rela="包含">顺向冲动</关联词>
   * 			<关联词 db="中医药学语言系统" rela="包含">逆向冲动</关联词>
   * 		</关联词s>
   * 	</result>
   * </correlatives>
   */
	private static String getCorrelative(String word){
	  Document doc = DocumentHelper.createDocument();
	  Element root = doc.addElement("correlatives");
	  List<String> concepts = ConceptEngine.getConcept(word);
  	for(int i=0; i<concepts.size(); i++){
	  	String concept = concepts.get(i);
	  	Element result = root.addElement("result");
	  	result.addElement("概念词").addText(concept);
			for(int j=0; j<propertyList.length; j++){
				String property = propertyList[j];
				Element content = ConceptEngine.getContent(concept, property);
				content.setName(property + "s");
		  	result.add(content);
			}			  
	  }	  	
		return root.asXML();
	}
	
	public static String[] propertyList = { "定义","异名","父类","子类","关联词"};
	
	public static void main(String args[]){
    long t1,t2;   
    t1=(new Date()).getTime();
		String html = getCorrelative("大黄");
		System.out.println(html);
		t2=(new Date()).getTime();
    System.out.println("查询‘大黄’的相关词：" + (t2-t1)+"毫秒");
    System.out.println(html);
    
		html = getCorrelative("山大黄");
		System.out.println(html);
		t1=(new Date()).getTime();
    System.out.println("查询‘山大黄’的相关词：" + (t1-t2)+"毫秒");
    System.out.println(html);
	}
}
