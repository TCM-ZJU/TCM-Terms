package ccnt.client;

import java.util.Date;
import java.util.List;

import ccnt.client.service.AsyncCallbackWithMask;
import ccnt.client.service.GetContentService;
import ccnt.client.view.*;

import com.gwtext.client.core.EventObject;
import com.gwtext.client.data.ArrayReader;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.PagingToolbar;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.event.GridRowListener;
import com.gwtextux.client.data.PagingMemoryProxy;  

public class ConceptPanel extends GridPanel {
	
	public static ConceptPanel getPanel(){
		if(panel == null){
			panel = new ConceptPanel();
		}
		return panel;
	}
	static ConceptPanel panel = null;

	private ConceptPanel() {
		recordDef = new RecordDef( new FieldDef[]{new StringFieldDef("concepts")} );
	  reader = new ArrayReader(recordDef);
		PagingMemoryProxy proxy = new PagingMemoryProxy(new Object[0][1]);
	  store = new Store(proxy, reader, true);  
	  columnModel = new ColumnModel(new ColumnConfig[]{
	  		new ColumnConfig("<b>概念词列表</b>", "concepts", 215, true, null, "concepts")
	  });
	  setColumnModel(columnModel);   

	  setStripeRows(true);
	  setAutoHeight(true);
	  setWidth(218);
		setHeight(550);
	  addGridRowListener(new GridRowListener() {

			public void onRowClick(GridPanel grid, int rowIndex, EventObject e) {
				onRowDblClick(grid, rowIndex, e);
			}

			public void onRowContextMenu(GridPanel grid, int rowIndex, EventObject e) {}

			public void onRowDblClick(GridPanel grid, int rowIndex, EventObject e) {
				String cell = grid.getView().getCell(rowIndex, 0).toString();
				/*例：<TD class="x-grid3-col x-grid3-cell x-grid3-td-concepts x-grid3-cell-first " style="WIDTH: 1000px" tabIndex=0>
				 * 		<DIV class="x-grid3-cell-inner x-grid3-col-concepts" id=ext-gen154 unselectable="on">123</DIV></TD> */
				int beginIndex = cell.lastIndexOf("\">")+2;
				int endIndex = cell.indexOf("</DIV>");
				concept = cell.substring(beginIndex, endIndex);
				
				ViewIterator.get().queryAll();
			}
		});
		setStore(store);
		
		pagingToolbar = new PagingToolbar(store);
		pagingToolbar.setPageSize(15);
		setBottomToolbar(pagingToolbar);
		store.load(0,15);
  }

	private void insertAll(List concepts){
		if(null != store)
			store.removeAll();
		int size = concepts.size();
		if(0 == size)
			MessageBox.alert("用户信息","您搜索的词没有对应的概念词，请查询其他词汇，或勾选模糊选项再搜索");
		Object[][] data = new Object[size][1];
	  for(int i = 0; i<size; i++){
	  	data[i][0] = "<img src='image/book_open.gif'/>" + concepts.get(i).toString();
	  }
	  PagingMemoryProxy proxy = new PagingMemoryProxy(data);
	  store = new Store(proxy, reader, true);
	  reconfigure(store, columnModel);
    pagingToolbar.setStore(store);
	  store.load(0,15);
	 }
	
	public void getConcepts(String word){
		concept = "";
		GetContentService.Util.getInstance().getConcepts(word, new AsyncCallbackWithMask("搜索概念词\""+word+"\""){		  
      protected void onSuccessInternal(Object result) {
      	long t = (new Date()).getTime();
				java.util.List concepts = (java.util.List) result;
			  insertAll(concepts); 
	      System.out.println("show time:"+((new Date()).getTime()-t));
		  }});
	}
	
	public void getFuzzyConcepts(String word, String fuzzyRange){
		concept = "";
		GetContentService.Util.getInstance().getConcepts(word, true, (new Integer(fuzzyRange)).intValue(), new AsyncCallbackWithMask("模糊搜索概念词\""+word+"\""){		  
      protected void onSuccessInternal(Object result) {
      	long t = (new Date()).getTime();
				java.util.List concepts = (java.util.List) result;
			  insertAll(concepts); 
	      System.out.println("show time:"+((new Date()).getTime()-t));
		  }});
	}
	
	public static String getConcept(){
		return concept;
	}
	
	private static String concept = null;
	private Store store = null;
	private ArrayReader reader = null;
	private RecordDef recordDef = null;
	private ColumnModel columnModel = null;
	private PagingToolbar pagingToolbar = null;
}
