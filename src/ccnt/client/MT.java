package ccnt.client;

import ccnt.client.view.SearchListener;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.HistoryListener;
import com.gwtext.client.core.RegionPosition;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Viewport;
import com.gwtext.client.widgets.layout.BorderLayout;
import com.gwtext.client.widgets.layout.BorderLayoutData;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class MT implements EntryPoint, HistoryListener{
  /**
   * This is the entry point method.
   */
  public void onModuleLoad(){
  	Panel panel = new Panel();
  	panel.setId("root-panel");
    panel.setLayout(new BorderLayout());
    
    panel.add(SearchPanel.getPanel(), new BorderLayoutData(RegionPosition.NORTH));
    
    BorderLayoutData westData = new BorderLayoutData(RegionPosition.WEST);
		westData.setSplit(true);
    westData.setMinSize(15);
    westData.setMaxSize(220);
    Panel westPanel = new Panel();
    westPanel.setWidth(220);
    westPanel.setAutoScroll(true);
    westPanel.add(ConceptPanel.getPanel());
    panel.add(westPanel, westData);

    panel.add(new ViewTab(), new BorderLayoutData(RegionPosition.CENTER));   
    
    new Viewport(panel);    

    //History listener
    History.addHistoryListener(this);
  }

  /**
   * This is the method for forward and backward button
   */
	public void onHistoryChanged(String historyToken) {
		SearchListener.search(historyToken);
  }

}
