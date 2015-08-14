package src.filesys;

import java.util.Iterator;
import java.util.ListIterator;
import java.io.Serializable;

public class SortedList implements ShellList, Iterable<ShellFile> {

	private int length;
	private FLNode first;
	
	private static final String FILE_EXISTS = "Cannot add file: File already exists";
	static final long serialVersionUID = 42L;
	
	/**
	 * Constructor for SortedList
	 * Creates a new empty sorted list of length 0
	 */
	public SortedList() {
		length = 0;
		first = null;
	}
	
	/**
	 * Search for a file with specific name
	 * @param fileName the name of the file to be looked up
	 * @return a ShellFile with name fileName, null if no such file exists
	 */
	public ShellFile getFile(String fileName) {
		
		ShellFile result = null;
		FLNode fetched = getNode(fileName);
		
		if (fetched != null) {
			result = fetched.getFile();
		}
		
		return result;
	}
	
	/**
	 * Search and fetch the FLNode that contains a file or 
	 * directory with name fileName. If not found, return null
	 *
	 * @param fileName the name of the directory or file that the
	 * fetched FLNode must contain
	 * @return the FLNode that contains a directory or
	 * file with name fileName, or null if no such FLNode exists
	 */
	private FLNode getNode(String fileName) {
		
		FLNode nodeSearch = null;
		FLNode inspect = first;
		String inspectName;
		boolean found = false;
		
		while (inspect != null && !found) {
			inspectName = inspect.getName();
			if (inspectName.equals(fileName)) {
				nodeSearch = inspect;
				found = true;
			}
			
			inspect = inspect.getNext();
		}
		
		return nodeSearch;
	}

	/**
	 * Add a ShellFile to the filelist
	 * @param addMe the ShellFile to add to the list kept by this SortedList
	 * @exception SortedListExistsException if a file or directory by the same name already exists
	 */
	public void addFile(ShellFile addMe) throws SortedListExistsException {
		
		String addName = addMe.getName();
		if (hasFile(addName)) {
			throw new SortedListExistsException(FILE_EXISTS);
		}
		
		// Case 1: the list is empty
		if (length == 0) {
			FLNode newNode = new FLNode(addMe, null, null);
			first = newNode;
		}
		
		// Case 2: list is non-empty
		else {
			first = first.addBeforeSorted(addMe);
		}
		
		// Adjust length
		length++;
	}

	/**
	 * Remove a ShellFile to the filelist
	 * @param fileName remove the ShellFile with name fileName from the list
	 * if such a ShellFile does not exist, do nothing
	 */
	public void removeFile(String fileName) {
		
		FLNode nodeFound = getNode(fileName);
		
		// If node has been found
		if (nodeFound != null) {
			
			// Reset the file's parent folder to null
			ShellFile file = nodeFound.getFile();
			file.setParent(null);
			
			// Fetch adjacent nodes
			FLNode foundPrev = nodeFound.getPrev();
			FLNode foundNext = nodeFound.getNext();
			
			// Adjust list to remove node
			if (first == nodeFound) {
				first = foundNext;
			}
			
			if (foundPrev != null) {
				foundPrev.setNext(foundNext);
			}
			
			if (foundNext != null) {
				foundNext.setPrev(foundPrev);
			}
			
			length--;
		}
		
	}


	/**
	 * True iff there exists a file or directory with name fileName
	 *
	 * @param fileName the name to check whether a directory or file in the list
	 * has as its name
	 * @return boolean true iff there exists a file or directory with name fileName
	 */
	public boolean hasFile(String fileName) {
		return (getNode(fileName) != null);
	}

	public ShellFile[] toArray() {

		ShellFile[] result = new ShellFile[length];
		ShellFile fetched;
		FLNode inspect = first;

		int i;
		for (i = 0; i < length; i++) {
			fetched = inspect.getFile();
			result[i] = fetched;
			inspect = inspect.getNext();
		}

		return result;

	}
    
	public ListIterator<ShellFile> iterator() {
		return this.listIterator();
	}

	/**
	 * The list iterator, which iterates over ShellFiles (files and directories)
	 *
	 * @return Iterator that iterates over the lexicographically
	 * sorted list of ShellFiles
	 */
	public ListIterator<ShellFile> listIterator() {
		
		ListIterator<ShellFile> iterator = new ListIterator<ShellFile>() {
			
			ShellFile[] shellArray = toArray();
			int index = 0;
			int arrayLength = shellArray.length;

			public void add(ShellFile file) {
				throw new UnsupportedOperationException();
			}
			
            public boolean hasNext() {
                return (index < arrayLength);
            }

			public boolean hasPrevious() {
				return (index > 0);
			}

            public ShellFile next() {
            	return shellArray[index++];
            }

			public int nextIndex() {
				return index;
			}
			
			public ShellFile previous() {
				return shellArray[--index];
			}
			
			public int previousIndex() {
				return (index - 1);
			}

            public void remove() {
                throw new UnsupportedOperationException();
            }

			public void set(ShellFile file) {
                throw new UnsupportedOperationException();
            }
        
		};

		return iterator;
	}
	
	public boolean isEmpty() {
		return (length == 0);
	}

	/**
	 * Return an int of the number of items (files or directories)
	 * in the list
	 *
	 * @return the length of the list
	 */
	public int length() {
		return length;
	}

}

class FLNode implements Serializable {

	static final long serialVersionUID = 42L;

	private String name;
	private ShellFile file;
	private FLNode prev;
	private FLNode next;
	
	public FLNode(ShellFile file, FLNode prev, FLNode next) {
		this.file = file;
		this.prev = prev;
		this.next = next;
		this.name = file.getName();
	}
	
	public String getName() {
		return name;
	}
	
	public ShellFile getFile() {
		return file;
	}
	
	public FLNode getNext() {
		return next;
	}
	
	public FLNode getPrev() {
		return prev;
	}
	
	public void setNext(FLNode next) {
		this.next = next;
	}
	
	public void setPrev(FLNode prev) {
		this.prev = prev;
	}

	/**
	 * Add a ShellFile (file or directory) lexicographically
	 * in respect to the position of this FLNode
	 *
	 * @param fileAdd the ShellFile to add lexicographically positioned
	 * relative to this node
	 * @return FLNode the node that after execution is positioned
	 * at the (perhaps old) position of this FLNode
	 */
	public FLNode addBeforeSorted(ShellFile fileAdd) {
		
		String fileAddName = fileAdd.getName();
		FLNode newNode;
		FLNode result;
		
		// If fileAdd comes before this file
		if (fileAddName.compareToIgnoreCase(name) <= 0) {
			
			newNode = new FLNode(fileAdd, null, this);
			prev = newNode;
			result = newNode;
		}
		
		// If fileAdd comes after this file
		else {
			
			// If this node if the last
			if (next == null) {
				newNode = new FLNode(fileAdd, this, null);
				next = newNode;
			} else {
				next = next.addBeforeSorted(fileAdd);
			}
			
			result = this;
		}
		
		return result;
	}

}

class SortedListException extends ShellListException {
	
	static final long serialVersionUID = 42L;

	public SortedListException(String message) {
		super(message);
	}
	
}

class SortedListExistsException extends SortedListException {
	
	static final long serialVersionUID = 42L;
	
	public SortedListExistsException(String message) {
		super(message);
	}
	
}
