package ccnt.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import ccnt.connection.ConnectionPool;
import ccnt.connection.HierarchyOperator;
import ccnt.dartgrid.QueryEngine;
import ccnt.dartgrid.Result;

public class ConceptEngine {

	/**
	 * get concepts list with word as alias and concept
	 * @param word
	 * @return concepts list
	 */
	public static List<String> getConcept(String word){
		List<String> concepts = null;
		//在Alias里查询
		concepts = getConceptByAlias(word);
	  //在Concept里查询
		if(isConcept(word) && !concepts.contains(word))
			concepts.add(0, word);
		return concepts;
	}
	
	/**
	 * get concepts list with word as alias and concept
	 * @param word
	 * @param fuzzy 是否模糊查询
	 * @return concepts list
	 */
	public static List<String> getConcept(String word, int fuzzyRange){
		List<String> concepts = null;
	  //在Concept里查询
		concepts = getConceptByConcept(word, true, fuzzyRange);
		//在Alias里查询
		if(concepts.size() < fuzzyRange){
			List<String> conceptsByAlias = getConceptByAlias(word, true, fuzzyRange-concepts.size());
			for(int i=0; i<conceptsByAlias.size(); i++ )
				if(!concepts.contains(conceptsByAlias.get(i)) )
					concepts.add(conceptsByAlias.get(i)); 
		}
		return concepts;
	}
	
	public static List<String> getConceptByAlias(String word){
		return getConceptByAlias(word, false,0); 
	}
	
	public static List<String> getConceptByAlias(String word, boolean fuzzy, int fuzzyRange){    
		List<String> concepts = new ArrayList<String>();
		String getConcept =	"PREFIX ns: <http://ccnt.cn/mt#> " +
												"SELECT distinct ?concept " +
												"WHERE {	?x ns:hasAlias ?a." +
												"       	?a ns:name ?alias. "+
												"       	?x ns:concept ?concept.";
		getConcept += fuzzy?"					FILTER(regex( ?alias, '" + word + "')).}" +
												"					LIMIT " + fuzzyRange 
											 :"					FILTER( ?alias='" + word + "').}";
		Result r;
    try {
	    r = QueryEngine.sparql(getConcept);
    } catch (Exception e) {
	    e.printStackTrace();
	    return new ArrayList<String>();
    }
		for(int i=0; i<r.size(); i++ )
    	concepts.add(r.get(i));
    return concepts;	
	}
	
	public static boolean isConcept(String word){		
		return !getConceptByConcept(word, false, 0).isEmpty(); 
	}
	public static List<String> getConceptByConcept(String word, boolean fuzzy, int fuzzyRange){    
    List<String> concepts = new ArrayList<String>();
		String getConcept = "PREFIX ns: <http://ccnt.cn/mt#> " +
										  	"SELECT distinct ?concept " +
										  	"WHERE { 	?x ns:concept ?concept.";
    getConcept += fuzzy ?
    							"					FILTER(regex( ?concept, '" + word + "')).}" +
									"					LIMIT " + fuzzyRange 
								: "					FILTER( ?concept='" + word + "').}";
    Result r;
    try {
	    r = QueryEngine.sparql(getConcept);
    } catch (Exception e) {
	    e.printStackTrace();
	    return concepts;
    }
		for(int i=0; i<r.size(); i++ )
			concepts.add(r.get(i));
    return concepts;	
	}

	public static List<String> getAliasByConcept(String word){
		return getAliasByConcept(word, false); 
	}
	public static List<String> getAliasByConcept(String word, boolean fuzzy){    
    List<String> concepts = new ArrayList<String>();
    String sparql =	"PREFIX ns: <http://ccnt.cn/mt#> " +
										"SELECT distinct ?alias " +
										"WHERE {?x ns:hasAlias ?a." +
										"       ?a ns:name ?alias. "+
										"       ?x ns:concept ?concept.";
		sparql += fuzzy ?
    							"					FILTER(regex( ?concept, '" + word + "')).}" 
								: "					FILTER( ?concept='" + word + "').}";
    Result r;
    try {
	    r = QueryEngine.sparql(sparql);
    } catch (Exception e) {
	    e.printStackTrace();
	    return new ArrayList<String>();
    }
		for(int i=0; i<r.size(); i++ )
			if( !r.get(i).equals("") )
				concepts.add(r.get(i));
		
    return concepts;	
	}

	public static Element getContent(String concept, String property){
		return getContent(concept, property,1);		
	}
	
	public static Element getContent(String concept, String property, int tier){
    long t1=(new Date()).getTime();
    
		String sparql = getSparql(concept,property);
		Document reDoc = DocumentHelper.createDocument();
		Element root = reDoc.addElement("Results");
		Element item = null;
		Result r;
		String db = "";
    try {
	    r = QueryEngine.sparqlWithDB(sparql);

			List<String> values = r.getValues();			
			for(int i=0; i<r.size(); i++){
				item = root.addElement(property);
				item.setText(values.get(i));
				if(property.equals("父类")||property.equals("子类"))
					item.addAttribute("db", r.getDB(i));
				else
					item.addAttribute("db", ConnectionPool.getDB(r.getDB(i)));
			}
			
			if(property.equals("关联词")){
				List<String> relas = r.getValues(1);
				List elements = root.elements();
				for(int i=0; i<r.size(); i++)
					((Element)elements.get(i)).addAttribute("rela", relas.get(i));				
			}	else if(property.equals("父类")||property.equals("子类")){
				List elements = root.elements();
				for(int i=0; i<elements.size(); i++)
					HierarchyOperator.getChildren((Element)elements.get(i), property, tier-1);
				HierarchyOperator.getChildren(root, "ztc", property, concept, tier, true);
				HierarchyOperator.getChildren(root, "pharmic", property, concept,tier, true);				
				elements = root.elements();
				for(int i=0; i<elements.size(); i++){
					item = (Element)elements.get(i);
					db = item.attributeValue("db");
					item.addAttribute("db", ConnectionPool.getDB(db));
				}
		 	}

			long t2=(new Date()).getTime();
      System.out.println("查询'"+concept+"'("+property+")"+"的时间：" + (t2-t1)+"毫秒");
			logger.info("查询'"+concept+"'("+property+")"+"的时间：" + (t2-t1)+"毫秒");
			System.out.println(reDoc.asXML());
			
    } catch (Exception e) {
	    logger.error("Dart Grid: Query with DB("+db+") Error! --> " + concept + "(" + property +")");
	    e.printStackTrace();
    }
    return root;
	}
		
	private static String getSparql(String concept, String tabName){
		if(tabName.equals("定义")){
			return	"PREFIX ns: <http://ccnt.cn/mt#> " +
							"SELECT ?defination " +
							"WHERE {?x ns:hasDefination ?d." +
							"       ?d ns:content ?defination. "+
							"       ?x ns:concept ?concept." +
							"				FILTER( ?concept='" + concept + "'). }";
		}else if(tabName.equals("异名")){
			return 	"PREFIX ns: <http://ccnt.cn/mt#> " +
							"SELECT distinct ?alias " +
							"WHERE {?x ns:hasAlias ?a." +
							"       ?a ns:name ?alias. "+
							"       ?x ns:concept ?concept." +
							"				FILTER( ?concept='" + concept + "'). }";
		}else if(tabName.equals("父类")){
			return	"PREFIX ns: <http://ccnt.cn/mt#> " +
							"SELECT ?pconcept " +
							"WHERE {?x ns:parent ?p." +
							"       ?p ns:concept ?pconcept. "+
							"       ?x ns:concept ?concept." +
							"				FILTER( ?concept='" + concept + "'). }";
		}else if(tabName.equals("子类")){
			return	"PREFIX ns: <http://ccnt.cn/mt#> " +
							"SELECT ?cconcept " +
							"WHERE {?x ns:child ?c." +
							"       ?c ns:concept ?cconcept. "+
							"       ?x ns:concept ?concept." +
							"				FILTER( ?concept='" + concept + "'). }";
		}else if(tabName.equals("关联词")){
			return	"PREFIX ns: <http://ccnt.cn/mt#> " +
							"SELECT ?rconcept ?rn " +
							"WHERE {?x ns:hasBelonging ?b." +
							"       ?x ns:concept ?concept." +
							"       ?b ns:relaconcept ?rconcept. "+
							"       OPTIONAL {?b ns:hasRelation ?r." +
							"       					?r ns:relaname ?rn.}" +
							"				FILTER( ?concept='" + concept + "'). }";
		}else
			return "";
	}
	
	public static void main(String args[]){
		getContent("大黄","父类");
		List<String> concepts = getConcept("大黄");
		for(int i=0; i<concepts.size(); i++)
			System.out.println(concepts.get(i));
  }	
	
	private final static Logger logger = Logger.getLogger(ConceptEngine.class);
}
