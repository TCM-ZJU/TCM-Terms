import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import ccnt.connection.ZtcOperator;
import ccnt.dartgrid.QueryEngine;
import ccnt.dartgrid.Result;

public class Test{
	public static void main(String[] args) throws Exception{
//		Result r = QueryEngine.getConcepts("Zhu mei");
//		System.out.print(r.get(0));
		//Result r = QueryEngine.get("吴少恒");
		/*String getConcept =	"PREFIX ns: <http://ccnt.cn/mt#> " +
		"SELECT distinct ?concept " +
		"WHERE {{	?x ns:hasAlias ?a." +
		"       	?a ns:name ?alias. "+
		"       	?x ns:concept ?concept." +
		"					FILTER( ?alias='1'). }" +
		"UNION 	{ ?x ns:hasAlias ?a." +
		"       	?a ns:name ?alias. "+
		"       	?x ns:concept ?concept." +
		"					Filter( ?concept ='1').}}";
		r = QueryEngine.sparql(getConcept);*/
		//System.out.print(r.get(0));

		//String s = "<Queries><Text>定义</Text><Link>异名</Link><Tree tier=\"3\">父类</Tree><Tree tier=\"3\">子类</Tree><TextLink>关联词</TextLink></Queries>"; 
		//System.out.print(getContents("一粒笑",s));
		//System.out.print(getConcepts("飞机",false));
		String[] nums = ZtcOperator.getNums("气囊");
		for(int i=0; i<nums.length; i++)
			System.out.println(i+" "+nums[i]);
	}

	public static List getConcepts(String word, boolean fuzzy) {
		try {
			List<String> concepts = new ArrayList<String>();
			//在Alias里查询
			String getConcept =	"PREFIX ns: <http://ccnt.cn/mt#> " +
													"SELECT distinct ?concept " +
													"WHERE {	?x ns:hasAlias ?a." +
													"       	?a ns:name ?alias. "+
													"       	?x ns:concept ?concept.";
			getConcept += fuzzy?"					FILTER(regex( ?alias, '" + word + "')).}" 
												 :"					FILTER( ?alias='" + word + "').}";
			Result r = QueryEngine.sparql(getConcept);
			for(int i=0; i<r.size(); i++ )
      	concepts.add(r.get(i));
      
      //在Concept里查询
      getConcept = 	"PREFIX ns: <http://ccnt.cn/mt#> " +
							  		"SELECT distinct ?concept " +
							  		"WHERE { 	?x ns:concept ?concept.";
      getConcept += fuzzy ?
      							"					FILTER(regex( ?concept, '" + word + "')).}" 
									: "					FILTER( ?concept='" + word + "').}";
			r = QueryEngine.sparql(getConcept);
			for(int i=0; i<r.size(); i++ )
				if(!concepts.contains(r.get(i)) )
					concepts.add(r.get(i));
      return concepts;
		} catch (Exception e) {
      e.printStackTrace();
		}
		return null;
	}
	
	public static String getContents(String concept, String string) {
		try {
			Document xmlDoc = DocumentHelper.parseText(string);
			Element root = xmlDoc.getRootElement();
			Element query = null;
			String text = null;
			String tag = null;
			
			Document reDoc = DocumentHelper.createDocument();
			Element results = reDoc.addElement("Results");
			Element e = null;
			Element item = null;
			String sparql = "";
			Result r = null;

			
			List Queries = root.elements();
			for(int i = 0; i < Queries.size(); i++){
				query = (Element)Queries.get(i);
				text = query.getText();
				tag = query.getName();
				
				if(text.equals("定义")){
					sparql =	"PREFIX ns: <http://ccnt.cn/mt#> " +
										"SELECT ?defination " +
										"WHERE {?x ns:hasDefination ?d." +
										"       ?d ns:content ?defination. "+
										"       ?x ns:concept ?concept." +
										"				FILTER( ?concept='" + concept + "'). }";
				}else if(text.equals("异名")){
					sparql =	"PREFIX ns: <http://ccnt.cn/mt#> " +
										"SELECT distinct ?alias " +
										"WHERE {?x ns:hasAlias ?a." +
										"       ?a ns:name ?alias. "+
										"       ?x ns:concept ?concept." +
										"				FILTER( ?concept='" + concept + "'). }";
				}else if(text.equals("关联词")){
					/*sparql = "PREFIX ns: <http://ccnt.cn/mt#> " +
					"SELECT distinct ?rn ?rconcept " +
					"WHERE {?x ns:hasBelonging ?b." +
					"       ?x ns:concept ?concept." +
					"       ?b ns:relaconcept ?rconcept. "+
					"       ?b ns:hasRelation ?r." +
					"       ?r ns:relaname ?rn." +
					"				FILTER( ?concept='" + concept + "'). }";*/
					sparql =	"PREFIX ns: <http://ccnt.cn/mt#> " +
										"SELECT ?rconcept " +
										"WHERE {?x ns:hasBelonging ?b." +
										"       ?b ns:relaconcept ?rconcept. "+
										"       ?x ns:concept ?concept." +
										"				FILTER( ?concept='" + concept + "'). }";
				}else
					continue;
				r = QueryEngine.sparql(sparql);				
				e = results.addElement(text);
			  for(int j=0; j<r.size(); j++ ){
			  	item = e.addElement(tag);
			  	item.setText(r.get(j));
			  }
			}
      return reDoc.asXML();
    } catch (Exception e) {
	    e.printStackTrace();
    }
	  return null;
  }
}
