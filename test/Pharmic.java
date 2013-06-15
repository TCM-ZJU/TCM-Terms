import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import ccnt.connection.ConnectionPool;


public class Pharmic {
	public static void main(String[] args){
		Logger logger = Logger.getLogger(Pharmic.class.getName());
		Connection conn = ConnectionPool.getConn("pharmic");
		String createSql = 	"create table pharmic(" +
									" id				numeric(10)	not null," +
									"	num1			varchar2(50)," +
									"	category1	varchar2(100)," +
									"	num2			varchar2(50)," +
									"	category2	varchar2(100)," +
									"	num3			varchar2(50)," +
									"	concept		varchar2(100)," +
									"	english		varchar2(255)," +
									"	def				varchar2(1000)," +
									"	latin			varchar2(255)," +
									"	source		varchar2(255)," +
									"	CONSTRAINT pharmic_pk PRIMARY KEY (id))";
		String SeqSql = "create sequence PHARMIC_ID_SEQ " +
										"minvalue 1 " +
										"maxvalue 10000 " +
										"start with 1 " +
										"increment by 1 " +
										"cache 20";
		String TriggerSql =	"CREATE OR REPLACE TRIGGER pharmic_id_trigger " +
												"BEFORE INSERT ON pharmic REFERENCING OLD AS OLD NEW AS NEW FOR EACH ROW " +
												"BEGIN " +
												"SELECT pharmic_id_seq.NEXTVAL INTO :NEW.id FROM DUAL; " +
												"END;";
		String SelectSql = "SELECT * FROM 中医药学名词;";
		String Insert = "insert into pharmic(num1,category1,num2,category2,num3,concept,english,def,latin,source) values('";
		String InsertSql;
		String str;
		Statement stmt;
    try {
    	stmt = conn.createStatement();
			stmt.execute(createSql);
			stmt.execute(SeqSql);
			stmt.execute(TriggerSql);
			Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
		  String url = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)}; DBQ=DB\\中医药学名词.mdb";
		  Connection connAccess = DriverManager.getConnection(url,"","");
		  System.out.println("Access 连接成功!");
		  Statement stmtAccess = connAccess.createStatement();
		  ResultSet rs = stmtAccess.executeQuery(SelectSql);
		  int count = 0;
			while(rs.next()){
				InsertSql = Insert;
				for(int i=2;i<12;i++){
					str = rs.getString(i);
					if(null != str){
						str = str.replace("'","''");
						InsertSql += str;
					}
					InsertSql += "','";
				}
				InsertSql = ((String) InsertSql.subSequence(0, InsertSql.lastIndexOf(',')))+")";
				logger.info(InsertSql);
				stmt.execute(InsertSql);
				count++;
			}
			logger.info(count);
			rs.close();
			stmtAccess.close();
			connAccess.close();
			stmt.close();
    } catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
    } catch (ClassNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
    }
			ConnectionPool.releaseConn("pharmic", conn);
	}
}
