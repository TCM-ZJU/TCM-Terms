package ccnt.client.view;

import ccnt.client.ConceptPanel;
import ccnt.client.service.AsyncCallbackWithMask;
import ccnt.client.service.GetContentService;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;
import com.google.gwt.xml.client.impl.DOMParseException;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.Panel;


public class ViewItem extends Panel{
	public ViewItem(String name, int type){
		this.name = name;
		this.type = type;
		this.able = true;
    setIconCls("view-list");
		setTitle(name);	  
    setAutoScroll(true);
		panel = new VerticalPanel();
		panel.setSpacing(5);
		add(panel);
	}

	public void queryWithLoading(){
		clear();
		final String concept = ConceptPanel.getConcept();
		panel.add(new HTML("<div class=text style='padding:2 8px'>正在获取概念词\""+concept+"\"的"+name+"...</div>"));
		GetContentService.Util.getInstance().getContents(concept, createXMLDoc(), new AsyncCallback(){
			public void onFailure(Throwable arg0) {
				MessageBox.alert("用户信息",name+"页查询失败，请刷新页面");
      }

			public void onSuccess(Object response) {
				if(ConceptPanel.getConcept().equals(concept)){
					clear();
					insertAll((String) response);
				}
      }
		});
	}
	
	public void queryWithMask() {
		clear();
		String concept = ConceptPanel.getConcept();
		GetContentService.Util.getInstance().getContents(concept, createXMLDoc(), new AsyncCallbackWithMask("获取概念词\""+concept+"\"的"+name){
			protected void onSuccessInternal(Object response) {
				insertAll((String) response);
			}
		});
  }
	
	private void insertAll(String responseText) {
		try{
			Document xmlDoc = XMLParser.parse(responseText);
			XMLParser.removeWhitespace(xmlDoc);
			Element root = xmlDoc.getDocumentElement();
			
//		<定义 db="...">...</定义><定义 db="...">...</定义>...
			NodeList contents = root.getChildNodes();
//		<定义 db="...">...</定义>
			int size = contents.getLength();
			if(0 == size)
				empty();
			for(int j = 0; j < size; j++){
				Element content = (Element)contents.item(j);
			  insert(content.toString(), content.getAttribute("db"));
			}
		} catch(DOMParseException e){
			MessageBox.alert("用户信息","返回信息中含有无法解析的字符，请联系管理员检查后端数据："+e.getContents());
		}
	}
	
	public void setAble(boolean able){
		this.able = able;
		if(able)
	    enable();
		else
			disable();
	}
	
	public boolean isAble(){
		return able;
	}
	
	public int getType(){
		return type;
	}
	
	public String getName(){
		return name;
	}
	
	public int getTier(){
		return -1;
	}
	
	public int getAbleTier(){
		return -1;
	}
	
	public void clear(){
		panel.clear();
	}
	
	/**
	 * 在显示单元里插入内容
	 * @param s xml形式的内容
	 * @param db 来源数据库
	 */
	private void insert(String s, String db){
		Document xmlDoc = XMLParser.parse(s);
		Element e = xmlDoc.getDocumentElement();
		String content = e.getFirstChild().getNodeValue();
		switch(type){
		case ViewType.TEXT:
			panel.add(
					new Content(
							new HTML("<div class=text style='padding:2 8px'><p>"+content+"</p></div>"), db));
	    break;
		case ViewType.LINK:
			Hyperlink link = new Hyperlink(content,content);
			link.setTitle(content);
			link.addClickListener(new SearchListener());
			panel.add(new Content(link, db));
			break;
		case ViewType.TEXTLINK:
			link = new Hyperlink(content,content);
			link.setTitle(content);
			link.addClickListener(new SearchListener());
			panel.add(new Content(new HTML("<div class=text style='padding:2 8px'><p>"+e.getAttribute("rela")+"</p></div>"),link, db));
			break;
		case ViewType.TREE:
			((TreeViewItem) this).insert(s,db);
			break;
		default:
			return;
		}
	}
	
	protected String name;
	private int type;
	protected boolean able;
	protected VerticalPanel panel;
	public void empty() {
	  panel.add(new HTML("<div class=text style='padding:2 8px'>" +
					"<p><i>概念词在此字段没有您要找的内容</i></p></div>"));	  
  }
	/**
	 * 生成查询XML字符串
	 * @return 查询XML字符串
	 * 例如：
	 * <父类 type='2' tier='3'/>
	 */
	private String createXMLDoc(){
		if(queryXml != null)
			return queryXml;
		Document xmlDoc = XMLParser.createDocument();
		Element root = xmlDoc.createElement(name);
		root.setAttribute("type", ""+type);
		if(type == ViewType.TREE)
			root.setAttribute("tier", ""+getAbleTier());
		queryXml = root.toString();
		return queryXml;
	}
	
	String queryXml = null;
}