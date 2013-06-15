package ccnt.client.view;

import ccnt.client.*;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;

public class SearchListener implements ClickListener{
	public void onClick(Widget sender) {
		String title = sender.getTitle();
		History.newItem(title);
	}
	
	// call by onHistoryChanged in History
	public static void search(String text){
		ViewIterator.clearAll();
		int split = text.indexOf('&');
		if(split == -1){
			SearchPanel.setWord(text);
			ConceptPanel.getPanel().getConcepts(text);
		}
		else{
			String word = text.substring(0, split);
			String fuzzyRange = text.substring(split+1);
			System.out.println(word+ " " + fuzzyRange);
			ConceptPanel.getPanel().getFuzzyConcepts(word,fuzzyRange);
		}
	}
}
