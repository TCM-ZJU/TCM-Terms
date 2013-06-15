package ccnt.client;

import com.gwtext.client.widgets.TabPanel;

import ccnt.client.view.ViewItem;
import ccnt.client.view.ViewIterator;

public class ViewTab extends TabPanel {

	public ViewTab() {	  
	  ViewIterator iter = ViewIterator.get();
	  for(iter.head();iter.hasNext();){
	  	ViewItem item = (ViewItem)iter.next();
	  	if(item.isAble()==true){
	  		add(item);
	  	}
	  }
  }
}