package ccnt.client.service;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

public interface GetContentService extends RemoteService {
	/** 
	* @gwt.typeArgs <java.lang.String,java.util.List<java.lang.String>> 
	*/
	public List getConcepts(String word);
	public List getConcepts(String word, int fuzzyRange);
	public String getContents(String word, String queryString);
	
	/**
	 * Utility class for simplifying access to the instance of async service.
	 */
	public static class Util {
		private static GetContentServiceAsync instance;
		public static GetContentServiceAsync getInstance(){
			if (instance == null) {
				instance = (GetContentServiceAsync) GWT.create(GetContentService.class);
				ServiceDefTarget target = (ServiceDefTarget) instance;
				target.setServiceEntryPoint(GWT.getModuleBaseURL() + "GetContent.rpc");
			}
			return instance;
		}
	}
}