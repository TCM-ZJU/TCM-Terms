package ccnt.client.view;

import java.util.Iterator;

public class ViewIterator implements Iterator {

	public static ViewIterator get(){
		if(iterator==null){
			iterator = new ViewIterator();
		}
		return iterator;
	}
	
	public boolean hasNext() {
		return position < list.length;
	}

	public Object next() {		
		return list[position++];
	}	

	public void remove() {}
	
	public void remove(String name) {
		int index = indexOf(name);
		if(index >= 0)
			list[index].setAble(false);
	}
	
	public void add(String name){
		int index = indexOf(name);
		if(index >= 0)
			list[index].setAble(true);
	}
	
	public void setTier(String label, int iters){
		int i = 0;
		for(; i < list.length; i++){
			if(list[i].getType()==ViewType.TREE && ((TreeViewItem)list[i]).getLabel().equalsIgnoreCase(label))
				break;
		}
		if(i<list.length && list[i].getType() == ViewType.TREE)
			((TreeViewItem)list[i]).setTier(iters);
	}
	
	public void head(){
		position = 0;
	}

	private int indexOf(String name){
		for(int i = 0; i<list.length;i++)
			if(name.equalsIgnoreCase(list[i].getName()))
				return i;
		return -1;
	}
	
	public static void clearAll(){
		for(iterator.head();iterator.hasNext();){
				ViewItem item = (ViewItem)iterator.next();
				item.clear();
		}
	}
	
	public static void queryAll(){
	  for(iterator.head();iterator.hasNext();){
	  	ViewItem item = (ViewItem)iterator.next();
	  	if(item.isAble())
	  		item.queryWithLoading();
	  	/*if(item.isVisible()==true)
	  		item.queryWithMask();
	  	else
	  		item.queryWithLoading();*/
	  }
	}
	
	private static ViewIterator iterator = null;
	private ViewItem[] list = { new ViewItem("定义",ViewType.TEXT),
															new ViewItem("异名",ViewType.LINK),
															new TreeViewItem("父类","上位词"),
															new TreeViewItem("子类","下位词"),
															new ViewItem("关联词",ViewType.TEXTLINK)};
	private int position = 0;	
}