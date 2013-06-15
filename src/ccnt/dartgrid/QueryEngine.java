package ccnt.dartgrid;

import org.apache.log4j.Logger;

import cn.edu.zju.dart.core.engine.IEngine;
import cn.edu.zju.dart.core.engine.engine.SinglethreadEngine;
import cn.edu.zju.dart.core.exceptions.DartCoreException;
import cn.edu.zju.dart.core.query.sqlplan.result.IQueryResult;
import cn.edu.zju.dart.core.query.sqlplan.result.impl.ResultSetQueryResult;
import cn.edu.zju.dart.core.query.sqlplan.result.impl.UnionQueryResult;
import cn.edu.zju.dart.core.resregistry.ISparqlResRegistry;
import cn.edu.zju.dart.core.resregistry.impl.SparqlResRegistry;
import cn.edu.zju.dart.core.semregistry.ISparqlSemRegistry;
import cn.edu.zju.dart.core.semregistry.impl.SparqlSemRegistry;
import cn.edu.zju.dart.core.sparql.DartSparqlQuery;
import cn.edu.zju.dart.core.utils.SparqlCoreSettings;
import ccnt.dartgrid.Result;

public class QueryEngine {
	static final Logger log = Logger.getLogger(QueryEngine.class);
	
	protected static boolean inited = false;
	protected static SparqlCoreSettings cs = null;
	
	protected static synchronized void setup(){
		if(inited)
			return;
		cs = SparqlCoreSettings.getInstance();
		ISparqlResRegistry resreg = new SparqlResRegistry("etc/");
		resreg.init();
		cs.setSparqlResReg(resreg);
		ISparqlSemRegistry semreg = new SparqlSemRegistry("http://ccnt.cn/mt/","etc/");
		semreg.init();
		cs.setSparqlSemReg(semreg);
		inited = true;
	}

	public static synchronized Result sparql(String queryString) throws Exception{
		setup();

 		DartSparqlQuery dsq = new DartSparqlQuery(queryString);
 		IEngine engine = SinglethreadEngine.getInstance();

		try {
 			IQueryResult qr = dsq.execQueryActivity(engine);
			Result result = new Result();
			result.setColumnCount(qr.getMetaData().getColumnCount());//
      while(qr.next()){
				result.add(qr.getDataItem());
			}
			result.valid = true;
			return result;
		} catch (DartCoreException e) {
			throw new Exception(e);
		} finally {
			dsq.release(engine);
		}
	}
	
	public static synchronized Result sparqlWithDB(String queryString) throws Exception{
		setup();		
		
		DartSparqlQuery dsq = new DartSparqlQuery(queryString);
		IEngine engine = SinglethreadEngine.getInstance();

		try {
			IQueryResult qr = dsq.execQueryActivity(engine);
			IQueryResult[] qrs = ((UnionQueryResult)qr).getUnionSqlQueryResults();
			ResultSetQueryResult rsqr = null;
			Result result = new Result();
	    queryString = queryString.substring(queryString.indexOf('?'), queryString.indexOf('{'));//
	  	int columnCount = queryString.split(" ").length-1;//qr.getMetaData().getColumnCount();
	  	result.setColumnCount(columnCount);
			for(int k=0;k<qrs.length;k++)
			{
				rsqr = (ResultSetQueryResult)qrs[k];
				
				//例：{http://ccnt.cn/mt}tcmls.TCM 转化为 tcmls
				String db = rsqr.getMetaData().getDataSources()[0].toString();
				int a = db.indexOf('}') + 1;
				int b = db.lastIndexOf('.');
				db = db.substring(a, b);
				
				while(rsqr.next())
					result.add(rsqr.getDataItem(), db);
			}
			result.valid = true;
			return result;
		} catch (DartCoreException e) {
			System.out.println(queryString);
			throw new Exception(e);
		} finally {
			dsq.release(engine);
		}
	}

}
