
import java.util.ArrayList;


/** disjoint sets class, using union by size and path compression.
 * 
 * @author Carlos Haddock, CS310, Prof. Russel Section-002
 *
 * @param <T>
 */
public class DisjointSets<T>
{

	private int[] s; //the sets
	private ArrayList<Set<T>> sets; //the actual data for the sets
	private Set<T> a;
	private Set<T> b;

	/** Constructor for Disjoint Sets
	 * 
	 * @param data The ArrayList that the will be passing through to the Disjoint sets. 
	 */
	public DisjointSets(ArrayList<T> data) {
		//your code here
	
		Set<T> a;
		s = new int[data.size()];
		sets = new ArrayList<Set<T>>();
		for(int i = 0 ; i < data.size(); i++) {
			//System.out.println(data.get(i));
			s[i] = -1;
			a = new Set<T>();
			T tempItem = data.get(i);
			//System.out.println(tempItem);
			a.add(tempItem);
			//System.out.println("Size: "+a.size());
			sets.add(i, a);;

		}
		//System.out.println(sets.toString());
		//System.out.println("End of Initialization \n");
		
		
	}
	/**Asserting that what's being passed through is infact a root
	 * 
	 * @param root the root being passed through
	 */
	private void assertIsRoot(int root) {
		assertIsItem(root);
		if(s[root] >= 0){
			//System.out.println("THE S[ROOT] IS " + s[root] + ", root = " + root);
			throw new IllegalArgumentException();
		}
	}
	/**Asserting that what's being passed through is infact a item
	 * 
	 * @param x the item being passed through
	 */
	private void assertIsItem(int x) {
		if(x < 0 || x>= s.length) {
			//System.out.println(s.length);
			throw new IllegalArgumentException();
		}
	}
	/** Union two sets, in both the s[] , and sets data
	 * 
	 * @param root1 the first root that will be unioned
	 * @param root2 the seconds root that will be unioned
	 * @return the new root
	 */
	public int union(int root1, int root2) {

		assertIsRoot(root1);
		assertIsRoot(root2);

		
		if (root2 == root1) {
			System.out.println("Roots were both the same");
			throw new IllegalArgumentException();
			}
		
		if (Math.abs(s[root2]) > Math.abs(s[root1])) {
			int oldRoot1 = s[root1];
			int oldRoot2 = s[root2];
			s[root1] = root2;
			s[root2] = oldRoot1 + oldRoot2;
			a = sets.get(root1);
			b = sets.get(root2);
			sets.set(root1, new Set<T>());
			a.addAll(b);
			sets.set(root2, a);
			return root2;
			}
		
		else{
			int oldRoot1 = s[root1];
			int oldRoot2 = s[root2];
			s[root2] = root1;
			s[root1] = oldRoot1 + oldRoot2;
			a = sets.get(root1);
			b = sets.get(root2);
			sets.set(root2, new Set<T>());
			a.addAll(b);
			sets.set(root1, a);
			return root1;
		}
	}

	/** find the root of the given item.
	 * 
	 * @param x the item that will have it's root found
	 * @return the root associated with the given item
	 */
	public int find(int x) {
		assertIsItem(x);
		if( s[x] < 0 ) {
			return x;
		}
		else {
			return s[x] = find(s[x]);
		}
	}

	/** get the data that's in the set associated with the root given
	 * 
	 * @param root the given root that has a data located in it
	 * @return the data in the set
	 */
	public Set<T> get(int root) {

		return sets.get(root);
	}
	
	/**Test Driver
	 * 
	 * @param args arguments for the driver
	 */
	public static void main(String[] args) {
		ArrayList<Integer> arr = new ArrayList<>();
		for(int i = 0; i < 10; i++)
			arr.add(i);
		
		DisjointSets<Integer> ds = new DisjointSets<>(arr);
		
		System.out.println(ds.find(0)); //should be 0
		System.out.println(ds.find(1)); //should be 1
		System.out.println(ds.union(0, 1)); //should be 0
		System.out.println(ds.find(0)); //should be 0
		System.out.println(ds.find(1)); //should be 0
		System.out.println("-----");
		
		
		System.out.println(ds.find(0)); //should be 0
		System.out.println(ds.find(2)); //should be 2
		System.out.println(ds.union(0, 2)); //should be 0
		System.out.println(ds.find(0)); //should be 0
		System.out.println(ds.find(2)); //should be 0
		System.out.println("-----");
		

/*		System.out.println(ds.union(5, 6)); //should be 5
		System.out.println(ds.union(7, 8)); //should be 7
		System.out.println(ds.union(5, 7)); //should be 5
		System.out.println(ds.union(3, 4)); //should be 3
		System.out.println(ds.union(9, 3)); //should be 3
		System.out.println(ds.union(0, 3)); //should be 0
		for(int i = 0 ; i < ds.s.length; i++) {
			ds.find(i);
		}
		System.out.println(ds.union(0, 5)); //should be 0
		System.out.println("-----");
		*/

		//Note: AbstractCollection provides toString() method using the iterator
		//see: https://docs.oracle.com/javase/8/docs/api/java/util/AbstractCollection.html#toString--
		//so your iterator in Set needs to work for this to print out correctly
		System.out.println(ds.get(0)); //should be [0, 1, 2]
		System.out.println(ds.get(1)); //should be []
		System.out.println(ds.get(3)); //should be [3]
		
		
	}
}
