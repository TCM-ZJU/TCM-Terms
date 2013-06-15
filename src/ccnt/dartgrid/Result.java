package ccnt.dartgrid;

import java.util.ArrayList;
import java.util.List;
import cn.edu.zju.dart.core.query.sqlplan.result.IResultDataItem;

public class Result {
	private List<List<String>> itemTable;
	private List<String> dbs;
	private int columnCount = 0;
	private int rowCount = 0;
	public boolean valid = false;
	
	Result(){
		itemTable = new ArrayList<List<String>>();
		dbs = new ArrayList<String>();
	}
	
	public List<String> getValues(){
		return itemTable.get(0);
	}
	
	public List<String> getValues(int columnIndex){
		return itemTable.get(columnIndex);
	}	
	
	public String getDB(int index){
		return dbs.get(index);
	}
	
	public boolean add(IResultDataItem item){
		boolean isAllEmpty = true;
		String value;
		for(int i=0; i<columnCount; i++){
			value = item.getData(i+1).toString().trim();//IResultDataItem计数从1开始
			if(isAllEmpty && !value.equals(""))
				isAllEmpty = false;
			itemTable.get(i).add(value.replace(';', '；'));
		}
		if(isAllEmpty){
			for(int i=0; i<columnCount; i++){
				itemTable.get(i).remove(rowCount);
			}
			return false;
		}
		else{
			rowCount++;
			return true;
		}
	}
	
	public void add(IResultDataItem item, String db){
		if(add(item))
			dbs.add(db);
	}
	
	public String get(int index){
		return itemTable.get(0).get(index);
	}
	
	public void setColumnCount(int columnCount){
		this.columnCount = columnCount;
		for(int i=0; i<columnCount; i++)
			itemTable.add(new ArrayList<String>());
	}
	
	public int size(){
		return rowCount;
	}

}
