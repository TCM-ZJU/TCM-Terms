package ccnt.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface GetContentServiceAsync {
	public void getConcepts(String word, AsyncCallback callback);
	public void getConcepts(String word, int fuzzyRange, AsyncCallback callback);
	public void getContents(String word, String queryString, AsyncCallback callback);
}
