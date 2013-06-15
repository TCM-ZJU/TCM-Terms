package ccnt.connection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Element;

public class HierarchyOperator {
	
	public static void getChildren(Element root, String property, int tier){
		getChildren(root, root.attributeValue("db"), property, root.getText(), tier, false);
	}
	
	public static void getChildren(Element root, String db, String property, String parent, int tier, boolean containDB){
		//System.out.println("<getChildren> root: "+ root.asXML());
		//System.out.println("<getChildren> db: "+db);
		//System.out.println("<getChildren> property: "+property);
		//System.out.println("<getChildren> parent: "+parent);
		//System.out.println("<getChildren> tier: "+tier);
		String sql = null;
		if(parent.equals(""))
			parent = root.getText();
		
		if(tier == 0 || parent.equals(""))
			return;
		
		if(db.equals("tcmls") || db.equals("tcmct") ){
			if(property.equals("父类"))
				sql = " select concept from tcm where id in ( " +
							"(select parentid from tcm where concept='"+ parent +"')" +
							" union " +
							"(select ClsID from ClsInstance where InsID in (select id from tcm where concept='"+ parent +"'))" +
							")";
			else if(property.equals("子类"))
				sql = " select concept from tcm where id in (" +
							"(select id from tcm where parentid in (select id from tcm where concept='"+ parent +"'))" +
							" union " +
							"(select InsID from ClsInstance where ClsID in (select id from tcm where concept='"+ parent +"'))" +
							")";
		}
		else if(db.equals("ztc")){
			String[] num = ZtcOperator.getNums(parent);
			if(property.equals("父类")){
				sql = "select mhchi from ztc0 where regexp_like(mn,'";
				for(int j=0; j<num.length; j++){
					if(num[j].contains("."))
						sql += "((;|(, )|^)" + num[j].substring(0, num[j].lastIndexOf('.')) + "(;|(, )|$))|";
					else
						continue;
				}
			}else if(property.equals("子类")){
				sql = "select mhchi from ztc0 where regexp_like(mn,'";
				for(int j=0; j<num.length; j++){
					if(!num[j].equals(""))
						sql += "((;|(, )|^)" + num[j] + ".[0-9]+(;|(, )|$))|";
					else
						continue;
				}
			}
			if(sql.contains("|"))
				sql = sql.substring(0, sql.length()-1);
			sql += "')";			
		}
		else if(db.equals("pharmic")){
			if(property.equals("父类"))
				sql = "select category2, category1 from pharmic where concept='"+ parent +"'";
			else if(property.equals("子类"))
				return;
		}
		List<Element> list = getList(sql,db,root,"Tree",containDB);
		for(int i = 0; i < list.size(); i++){
			getChildren(list.get(i), db, property, list.get(i).getText(), tier-1, false);
		}
	}
		
	private static List<Element> getList(String sql, String db, Element root, String tag, boolean containDB){
		//System.out.println("<getList> sql: "+sql);
		//System.out.println("<getList> db: "+db);
		//System.out.println("<getList> root: "+ root.asXML());
		Connection conn = ConnectionPool.getConn(db);
		
		//get element list from the DB
		String concept = null;
		String content = null;
		Element e = null;
		List<Element> list = new ArrayList<Element>();
		if(db.equals("tcmls") || db.equals("tcmct"))
			concept = "concept";
		else if(db.equals("ztc"))
			concept = "mhchi";
		else if(db.equals("pharmic"))
			concept = "category2";
		if(tag.equals(""))
			tag = root.getName();
		try{
			Statement stmt = conn.createStatement();		
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()){
				e = root.addElement(tag);
				content = rs.getString(concept);
		  	if(db.equals("pharmic")){
		  		if((null == content) || content.equals("")){
		  			content = rs.getString("category1");
				  	e.setText(content);
		  		}
		  		else{
				  	e.setText(content);
		  			Element e1 = e.addElement(tag);
		  			e1.setText(rs.getString("category1"));
		  		}
		  	}
		  	else{
		  		list.add(e);
		  		e.setText(content);
		  	}
		  	if(containDB)
			  	e.addAttribute("db", db);
			}
			rs.close();
			stmt.close();
			ConnectionPool.releaseConn(db, conn);
			return list;
		}catch(Exception exception){
			logger.error(exception);
			return new ArrayList<Element>();
		}
	}	
	
	private final static Logger logger = Logger.getLogger(HierarchyOperator.class);
}
