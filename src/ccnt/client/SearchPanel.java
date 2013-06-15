package ccnt.client;

import ccnt.client.view.ViewDialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ImageBundle;
import com.google.gwt.user.client.ui.KeyboardListenerAdapter;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.Widget;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.layout.ColumnLayout;
import com.gwtext.client.widgets.layout.ColumnLayoutData;

public class SearchPanel extends Panel {

	private SearchPanel() {
  }
	
  public interface Images extends ImageBundle {
    AbstractImagePrototype colorbook();
  }
  
	public static Panel getPanel(){
		/*------关键字框--------*/
		name.setVisibleLength(50);
		name.addKeyboardListener(new KeyboardListenerAdapter(){
		   public void onKeyPress(Widget sender, char keyCode, int modifiers) {
		      // Check for Enter key
		      if ((keyCode == 13) && (modifiers == 0)) {
		         validateAndSubmit();
		      }
		   }      
		});
		
		/*------模糊查询勾选框--------*/
		fuzzy.addClickListener(new ClickListener(){
			public void onClick(Widget arg0) {
	      fuzzyRange.setVisible(fuzzy.isChecked());	      
      }			
		});
		fuzzyRange.setVisibleLength(3);
		fuzzyRange.setText("50");
		fuzzyRange.setTextAlignment(TextBoxBase.ALIGN_RIGHT);
		fuzzyRange.addKeyboardListener(new KeyboardListenerAdapter(){
			public void onKeyPress(Widget sender, char keyCode, int modifiers) {
	      // Check for Number key
				if ((!Character.isDigit(keyCode)) && (keyCode != (char) KEY_TAB)
	        && (keyCode != (char) KEY_BACKSPACE)
	        && (keyCode != (char) KEY_DELETE) && (keyCode != (char) KEY_ENTER) 
	        && (keyCode != (char) KEY_HOME) && (keyCode != (char) KEY_END)
	        && (keyCode != (char) KEY_LEFT) && (keyCode != (char) KEY_UP)
	        && (keyCode != (char) KEY_RIGHT) && (keyCode != (char) KEY_DOWN)) {
	        // TextBox.cancelKey() suppresses the current keyboard event.
	        ((TextBox)sender).cancelKey();
	      }
	    }
		});
	  fuzzyRange.setVisible(false);
		
	  /*------搜索按钮--------*/
		Button search = new Button("搜索",new ButtonListenerAdapter() {
		  public void onClick(Button button, EventObject e) {
		  	validateAndSubmit();
		  }
	  });
		search.setIcon("image/page_find.gif");
		search.focus();
	  
		HorizontalPanel hp = new HorizontalPanel();
		hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hp.setSize("450px","100px");
		hp.add(name);
	  hp.add(fuzzy);
	  hp.add(fuzzyRange);
	  hp.add(search);
		
		Toolbar toolbar = new Toolbar();
		ToolbarButton range = new ToolbarButton("设定展示区域");
		ToolbarButton help 	= new ToolbarButton("浏览在线帮助");
	  toolbar.addButton(range);
	  toolbar.addSeparator();
	  toolbar.addButton(help);
	  
		Images image = (Images) GWT.create(Images.class);
		
	  searchPanel.setLayout(new ColumnLayout());
	  searchPanel.setBorder(false);
	  searchPanel.setHeight("125px");
		searchPanel.add(image.colorbook().createImage(), new ColumnLayoutData(.5));
	  searchPanel.add(hp);
	  //searchPanel.add(new Panel(), new ColumnLayoutData(.5));
	  searchPanel.setTopToolbar(toolbar);
	  
	  range.addListener(new ButtonListenerAdapter() {
		  public void onClick(Button button, EventObject e) {
		   	ViewDialog dlg = ViewDialog.getDialog();
		    dlg.show();
		  }
	  });
	  
	  help.addListener(new ButtonListenerAdapter() {
		  public void onClick(Button button, EventObject e) {
		  	MessageBox.alert("帮助信息",helpMessage());
		  }
	  });
		return searchPanel;
	}

	public static void setWord(String word){
		name.setText(word);
	}
	
	private static void validateAndSubmit(){
		String text = name.getText();
		if(text == null || text.equals(""))
			MessageBox.alert("用户信息","请输入要搜索的内容再提交");
   	else if(fuzzy.isChecked()){
  		String range = fuzzyRange.getText();
  		boolean isNumber = true;
  		for(int i=0; isNumber && i<range.length(); i++){
  			if(range.charAt(i)>'0'+9 || range.charAt(i)<'0')
  				isNumber = false;
  		}
  		if(isNumber)
  			History.newItem(text+"&"+range);
  		else
				MessageBox.alert("用户信息","请在范围框中输入数字");
   	}
   	else
   		History.newItem(text);
	}
	
	final static TextBox name = new TextBox();
	final static CheckBox fuzzy = new CheckBox("模糊");
	final static TextBox fuzzyRange = new TextBox();
  private static Panel searchPanel	= new Panel();
  
  static private String helpMessage(){
  	return "<p> 帮助信息，开发中 </p>";
  }
}