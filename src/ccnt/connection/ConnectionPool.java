/**
 * 浙江大学CCNT网格实验室
 * @author zxg "zhang0925@gmail.com"
 * 2007-11-27
 * To read configuration about the total ontology, concerning slots & clses. 
 */

package ccnt.connection;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.sql.*;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class ConnectionPool {
	static Logger logger = Logger.getLogger(ConnectionPool.class.getName());
	private String driver;
	private String url;
	private String user;
	private String pwd;
	private int maxConn;
	private int minConn;
	
	//The time left too release the connection
	private int timeCount;
	
	//The time intervel to release the connection
	private int idleTime;
	
	//The Timer used to release unused connection
	private Timer timer = new Timer(false);
	
	//The conn number in connection pool(include the conn has been distributed)
	private int connNum;

	private String name;
	private LinkedList<Connection> connPool = new LinkedList<Connection>();
	
	/**
	 * 利用数据库名构造对应链接池
	 * @param dbname the name of database, refer to etc/server.xml
	 */
	public ConnectionPool(String dbname){
		SAXReader saxReader = new SAXReader();
	  try {
	    Document document = saxReader.read(ConnectionPool.class.getClassLoader().getResourceAsStream("server.xml"));
	    Element databases = document.getRootElement();
	    Element database = databases.element(dbname);
	    this.url = database.element("url").getText();
	    this.user = database.element("user").getText();
	    this.pwd = database.element("password").getText();
	    this.driver = database.element("driver").getText();
	    this.maxConn = new Integer(database.element("max").getText()).intValue();
	    this.minConn = new Integer(database.element("min").getText()).intValue();
	    this.idleTime = new Integer(database.element("idle").getText()).intValue();
	    this.name = database.element("name").getText();

	  } catch (DocumentException e) {
	  	logger.error("Cannot find the document: "+ "server.xml");
	    e.printStackTrace();
	  }
	  init();
	
	}
	
	public ConnectionPool(String url, String userName, String pwd, String jdbcDriver, int max, int min, int idle){
		this.url = url;
		this.user = userName;
		this.pwd = pwd;
		this.maxConn = max;
		this.minConn = min;
		this.idleTime = idle;
		this.timeCount = idleTime;
		this.driver = jdbcDriver;
		
		init();
	}
	
	private void init(){
		this.connNum = 0;
		
		timer.schedule(new TimerTask(){
			
			public void run() {
				timeCount--;
				
				if(timeCount <= 0){
					//Reset the Timer
					timeCount = idleTime;
					
					if(connPool.size() > minConn){
						try {
							connPool.poll().close();
							connNum--;
						} catch (SQLException e) {
							e.printStackTrace();
							connNum--;
						}
					}
				}
			}
		}, 0, 1000);
	
	}
	
	public synchronized Connection getConn(){
		//Reset the timer
		timeCount = idleTime;
		
		//If the connectionPool is full and all connection have been used, return null.
		if(connNum >= maxConn){
			return null;
		}
	
		if(!connPool.isEmpty()){
			return connPool.poll();
		}
		else{
			try {
				Class.forName(driver).newInstance();
				Connection conn = DriverManager.getConnection(url, user, pwd);
				
				connNum++;
				return conn;
			} catch (InstantiationException e) {
				logger.info(e.getStackTrace());
				logger.info(e);
			} catch (IllegalAccessException e) {
				logger.info(e.getStackTrace());
				logger.info(e);
			} catch (ClassNotFoundException e) {
				logger.info(e.getStackTrace());
				logger.info(e);
			} catch (SQLException e) {
				logger.info(e.getStackTrace());
				logger.info(e);
			}	
		}
		
		return null;
	}
	
//get connection for the DB
	public static Connection getConn(String db){
		ConnectionPool cp = null;
		if(ConnDic.containsKey(db)){
			cp = ConnDic.get(db);
		}
		else{
			cp = new ConnectionPool(db);
			ConnDic.put(db, cp);
		}
		return cp.getConn();
	}
	
	public synchronized boolean releaseConn(Connection conn){
		
		try {
			if(conn.isClosed()){
				connNum--;
				return false;
			}
			else{
				connPool.offer(conn);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public static boolean releaseConn(String db,Connection conn){
		return ConnDic.get(db).releaseConn(conn);
	}
	
	public void finalize(){
		Iterator iterator = connPool.iterator();
		
		while(iterator.hasNext()){
			try {
				((Connection)iterator.next()).close();
			} catch (SQLException e) {
				e.printStackTrace();
				logger.info(e);
			}
		}
	}

	public static String getDB(String db){
		ConnectionPool cp = null;
		if(ConnDic.containsKey(db)){
			cp = ConnDic.get(db);
		}
		else{
			cp = new ConnectionPool(db);
			ConnDic.put(db, cp);
		}
		return cp.name;
	}
	private final static Map<String, ConnectionPool> ConnDic = new HashMap<String, ConnectionPool>();
}


