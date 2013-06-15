package ccnt.connection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;

public class ZtcOperator {

	public static String[] getNums(String concept){
		Connection conn = ConnectionPool.getConn("ztc");
		String sql = "select mn from ztc0 where mhchi = '"+ concept +"'";
		String[] list = {""};
		//get element list from the DB
		try{
			String mn = "";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			if(rs.next())
		  	mn = rs.getString("mn");
			list = mn.split("[, ]|[;]");
			rs.close();
			stmt.close();
			ConnectionPool.releaseConn("ztc", conn);
		}catch(Exception exception){
			logger.error(exception);
		}
		return list;
	}

	private final static Logger logger = Logger.getLogger(ZtcOperator.class);
}
