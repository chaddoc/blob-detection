//
// Task 1. Set<T> class (10%)
// This is used in DisjointSets<T> to store actual data in the same sets
//

//You cannot import additonal items
import java.util.AbstractCollection;
import java.util.Iterator;
//You cannot import additonal items

//
//Hint: if you think really hard, you will realize this class Set<T> is in fact just a list
//      because DisjointSets<T> ensures that all values stored in Set<T> must be unique, 
//      but should it be dynamic array list or linked list??
//
/**A class that is used to create a Set
 * 
 * @author Carlos Haddock, CS310, Prof. Russel Section-002
 *
 * @param <T> A generic type T
 */
public class Set<T> extends AbstractCollection<T> {
	private Node<T> head; 
	private Node<T> end;
	private int size = 0;
	//O(1)
	/** Used to call the Set, just sets the head, and end to null.
	 * 
	 */
	public Set() {
		head = null;
		end = null;
	}
	//O(1)
	/**Adds an item to the current set
	 * @param item the given item of Type T to be added
	 * @return if the data was added succesfully
	 * 
	 */
	public boolean add(T item) {
		Node<T> n = new Node<T>(item);
		Node<T> current = head;
		if(head == null) {
			head = n;
			end = n;
			size++;
		}
		else{
			head = n;
			head.setNext(current);
			current.setPrev(head);
			size++;
		}

		return true;

	}
	//O(1)
	/**Adds the given set to this set.
	 * 
	 * @param other The given set that will be added to this set.
	 * @return whether we were able to successful add another set to this set.
	 */
	public boolean addAll(Set<T> other) {
		
		//head.setNext(other.head); // THIS KIND OF WORKS
		Node<T> current = other.end;
		
		current.setNext(head);
		head.setPrev(current);
		head = other.head;
		size += other.size();
/*		
		Node<T> itorig = head; // used for printing the original list
		while(itorig != null) {
			System.out.print(itorig.getValue() + " - > ");
			itorig = itorig.getNext();
		}
		System.out.println("End of LL");
		
		Node<T> itrev = end; //used for printing the list reversed
		while(itrev != null) {
			System.out.print(itrev.getValue() + " - > ");
			itrev = itrev.getPrev();
		}
		System.out.println("End of LL");
		*/
		return true;
	}
	//O(1)
	/** Clears the stack
	 * 
	 */
	public void clear() {
		head = null;
		end = null;
		size = 0 ;
	} 
	//O(1)
	/** Returns the number of items in the stack
	 * 
	 * @return Returns the size
	 */
	public int size() {
		return size;
	}
	//O(1) for next() and hasNext()
	/** Creates an anonymous class Iterator, this then returns the iterator.
	 *  
	 *  
	 */
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			Node<T> current = end;
			//O(1)
			public T next() {
				if(hasNext() == false) {
					throw new NullPointerException("Tried to call next() when there are no more items");
				}
				T temp =  current.getValue();
				current = current.getPrev();
				return temp;
			}
			//O(1)
			public boolean hasNext() {
				return (current != null);
			}
		};
	}
	/**Test Driver
	 * 
	 * @param args arguments for the driver
	 */
	public static void main(String[] args) {
		Set<String> a = new Set<String>();
		a.add("A");
		a.add("B");
		a.add("C");
		Set<String> b = new Set<String>();
		b.add("D");
		b.add("E");
		b.add("F");
		a.addAll(b);
		Set<String> c = new Set<String>();
		c.add("G");
		c.add("H");
		c.add("I");
		Set<String> d = new Set<String>();
		d.add("J");
		d.add("K");
		d.add("L");
		c.addAll(d);
		a.addAll(c);
		System.out.println(a.size());
		for(String item : a) {
				System.out.println(item);
		}
		
		
	}
	/** A nested class used to support a Doubly LinkedList data Structure within the Set Class, creates a Node of type T
	 * 
	 * @author Carlos Haddock, CS310, Prof. Russel Section-002
	 *
	 * @param <T> A generic type T
	 */
	class Node<T> {
		private T value;
		private Node<T> next;
		private Node<T> prev;
		/** Creates a node with the given value.
		 * 
		 * @param value The given value that the node will contain
		 */
		public Node(T value) {
			this.value = value;
		}
		/** Grabs the value in the node
		 * 
		 * @return Returns the value in the node
		 */
		public T getValue() {
			return value;
		}
		/** Sets the value in the node
		 * 
		 * @param value The given value that will be set
		 */
		public void setValue(T value) {
			this.value = value;
		}
		/** Grabs the next node
		 * 
		 * @return Returns the next node
		 */
		public Node<T> getNext() {
			return this.next;
		}
		/** Sets the next node to the given next.
		 * 
		 * @param next The given next that the next node will be set to.
		 */
		public void setNext(Node<T> next) {
			this.next = next;
		}
		/** Grabs the previous node
		 * 
		 * @return Returns the previous node.
		 */
		public Node<T> getPrev() {
			return this.prev;
		}
		/** Sets the previous node
		 * 
		 * @param prev The given prev that the previous node will be set to.
		 */
		public void setPrev(Node<T> prev) {
		this.prev = prev;
	}
}
}

