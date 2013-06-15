
public class Node {
	public Node(Node node){
		this.next = node;
	}
	public void set(int data){
		this.data = data;
	}
	private int data;
	Node next;
	
	public static void main(String[] args){
		Node head = null;
		Node cur = null;
		for(int i=10; i>0; i--){
			cur = new Node(head);
			cur.set(i);
			head = cur;
		}
		print(head);
		head =  reverse(head);
		print(head);
	}

	/* 算法：next存储当前节点的下一个元素，遍历过程中将当前节点的next元素值反转，并用next继续遍历 */
	public static Node reverse(Node head){
		Node pre, cur, next;

		if(null == head)
			return null;
		pre = head;
		cur = head.next;
		head.next = null;
		while(null != cur){
			next = cur.next;
			cur.next = pre;
			pre = cur;
			cur = next;
		}

		return pre;
	}
	
	public static void print(Node head){
		System.out.print("The nodelist is ");
		if(null == head){
			System.out.println("empty.");
			return;
		}
		while(null != head){
			System.out.print(head.data+" ");
			head = head.next;
		}
		System.out.println();
	}
}
