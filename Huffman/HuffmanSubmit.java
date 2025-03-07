/* 
 * Elana Chen-Jones
 * Net ID: echenjon
 * Project 2
 */

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.PriorityQueue;

public class HuffmanSubmit implements Huffman {

	public void encode(String inputFile, String outputFile, String freqFile) {
		String s = "";
		BinaryIn bin = new BinaryIn(inputFile);
		while (!bin.isEmpty()) {
			s += bin.readChar();
		}
		HashMap<Character, Integer> charToFreq = new HashMap<>();
		char[] charArray = s.toCharArray();
		for (int i = 0; i < charArray.length; i++) {
			if (!charToFreq.containsKey(charArray[i])) {
				charToFreq.put(charArray[i], 1);
			}
			else {
				Integer value = charToFreq.get(charArray[i]);
				value++;
				charToFreq.put(charArray[i], value);
			}
		}
		
		PriorityQueue<HuffTree<Character>> q = new PriorityQueue<>();
		
		for (char key : charToFreq.keySet()) {
			HuffTree<Character> t = new HuffTree<Character>(key, charToFreq.get(key));
			q.add(t);
		}
		
		HuffTree<Character> h = buildTree(q);
		HashMap<Character, String> charToCode = new HashMap<>();
		treeToEncoding(h.root(), charToCode, "");
		
		try { // Create frequency file
			PrintWriter w = new PrintWriter(freqFile, "UTF-8");
			for (char k : charToFreq.keySet()) {
				String origBinString = Integer.toBinaryString(k);
				int extra0s = 8 - origBinString.length();
				String binString = "";
				for (int i = 0; i < extra0s; i++) {
					binString += "0";
				}
				binString += origBinString;
				w.println(binString + ":" + charToFreq.get(k));
			}
			w.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
		} catch (UnsupportedEncodingException e) {
			System.out.println("Unsupported encoding");
		}
		
		// Create output file
		BinaryOut out = new BinaryOut(outputFile);
		for (char c : charArray) {
			String code = charToCode.get(c);
			for (int i = 0; i < code.length(); i++) {
				if (code.charAt(i) == '0') {
					out.write(false);
				}
				else if (code.charAt(i) == '1') {
					out.write(true);
				}
			}
		}
		out.flush();
	}
	
	public void decode(String inputFile, String outputFile, String freqFile) {
		BinaryIn binFreq = new BinaryIn(freqFile);
		PriorityQueue<HuffTree<Character>> q = new PriorityQueue<>();
		while (!binFreq.isEmpty()) {
			String binString = "";
			for (int i = 0; i < 8; i++) {
				char c = binFreq.readChar();
				binString += c;
			}
			char colon = binFreq.readChar();
			if (colon != ':') {
				System.out.println("Invalid input");
				return;
			}
			String s = "";
			char next = binFreq.readChar();
			while(next != '\n') {
				s += Character.toString(next);
				next = binFreq.readChar();
			}
			int i = Integer.parseInt(s);
			HuffTree<Character> t = new HuffTree<Character>((char) Integer.parseInt(binString, 2), i);
			q.add(t);
		}
		
		HuffTree<Character> h = buildTree(q);
		printHuffTree(h.root(), 0);
		BinaryIn binInput = new BinaryIn(inputFile);
		String allChars = "";
		HuffBaseNode<Character> root = (HuffInternalNode<Character>) h.root();
		HuffBaseNode<Character> curr = root;
		HuffBaseNode<Character> nodeL = null;
		HuffBaseNode<Character> nodeR = null;
		while(!binInput.isEmpty()) {
			if (!curr.isLeaf()) {
				nodeL = ((HuffInternalNode<Character>) curr).left();
				nodeR = ((HuffInternalNode<Character>) curr).right();
				boolean b = binInput.readBoolean();
				if (b == false && nodeL != null) {
					curr = nodeL; // go left
				}
				else if (b == true && nodeR != null) {
					curr = nodeR; // go right
				}
				else {
					System.out.println("Not left or right");
				}
			}
			else {
				HuffLeafNode<Character> leaf = (HuffLeafNode<Character>) curr;
				char c = leaf.element();
				allChars += c;
				curr = root;
			}
		}
		
		BinaryOut binOut = new BinaryOut(outputFile);
		for (int i = 0; i < allChars.length(); i++) {
			binOut.write((byte) allChars.charAt(i));
		}
		binOut.close();
	}
	
	/** Prints the contents of an encoded file in readable form: 0 for false, 1 for true
	 * @param inputFile the file to be read and printed */
	public void printEncodedFile(String inputFile) {
		BinaryIn bin = new BinaryIn(inputFile);
		while (!bin.isEmpty()) {
			boolean b = bin.readBoolean();
			if (b == false) {
				System.out.print("0");
			}
			else {
				System.out.print("1");
			}
		}
		System.out.println();
	}
	
	/** Prints a rough visual representation of a HuffTree, with the number of spaces before a node representing
	 * the number of levels down it is from the root
	 * @param base in the first call, the root of a tree, in a recursive call, the left or right child of a node
	 * @param spaces the number of spaces (levels down) before a node is reached */
	public void printHuffTree(HuffBaseNode<Character> base, int spaces) {
		for (int i = 0; i < spaces; i++) {
			System.out.print(" ");
		}
		if (base.isLeaf()) {
			HuffLeafNode<Character> leaf = (HuffLeafNode<Character>) base;
			System.out.println(leaf.element() + ": " + leaf.weight());
		}
		else {
			System.out.println(base.weight());
			HuffInternalNode<Character> root = (HuffInternalNode<Character>) base;
			if (root.left() != null) {
				printHuffTree(root.left(), spaces + 1);
			}
			if (root.right() != null) {
				printHuffTree(root.right(), spaces + 1);
			}
		}
	}
	
	/** Traverses a Huffman tree of characters and encodes each character 
	 * @param r in the first call, the root node, in a recursive call, the left or right node
	 * @param hm a hash map that starts out empty, and ends with all of the tree's leaves and their codes
	 * @param code the binary code for each character, based on their position in the tree */
	public void treeToEncoding(HuffBaseNode<Character> r, HashMap<Character, String> hm, String code) {
		if (r.isLeaf()) {
			HuffLeafNode<Character> leaf = (HuffLeafNode<Character>) r;
			Object o = leaf.element();
			hm.put((Character) o, code);
		}
		else {
			HuffInternalNode<Character> node = (HuffInternalNode<Character>) r;
			treeToEncoding((HuffBaseNode<Character>) node.left(), hm, code + "0");
			treeToEncoding((HuffBaseNode<Character>) node.right(), hm, code + "1");
		}
	}
	
	/** Huffman tree node implementation: Base class */
	public interface HuffBaseNode<E> { // from the textbook
		public boolean isLeaf();
		public int weight();
	}

	/** Huffman tree node: Leaf class */
	public class HuffLeafNode<E> implements HuffBaseNode<E> { // from the textbook
		
		private E element; // Element for this node
		private int weight; // Weight for this node

		/** Constructor */
		public HuffLeafNode(E el, int wt) {
			element = el;
			weight = wt;
		}

		/** @return The element value */
		public E element() {
			return element;
		}

		/** @return The weight */
		public int weight() {
			return weight;
		}

		/** Return true */
		public boolean isLeaf() {
			return true;
		}
	}

	/** Huffman tree node: Internal class */
	public class HuffInternalNode<E> implements HuffBaseNode<E> { // from the textbook
		
		private int weight; // Weight (sum of children)
		private HuffBaseNode<E> left; // Pointer to left child
		private HuffBaseNode<E> right; // Pointer to right child

		/** Constructor */
		public HuffInternalNode(HuffBaseNode<E> l, HuffBaseNode<E> r, int wt) {
			left = l;
			right = r;
			weight = wt;
		}
		
		/** @return The left child */
		public HuffBaseNode<E> left() {
			return left;
		}
		
		/** @return The right child */
		public HuffBaseNode<E> right() {
			return right;
		}

		/** @return The weight */
		public int weight() {
			return weight;
		}

		/** Return false */
		public boolean isLeaf() {
			return false;
		}
	}

	/** A Huffman coding tree */
	public class HuffTree<E> implements Comparable<HuffTree<E>> { // from the textbook

		private HuffBaseNode<E> root; // Root of the tree

		/** Constructors */
		public HuffTree(E el, int wt) {
			root = new HuffLeafNode<E>(el, wt);
		}

		public HuffTree(HuffBaseNode<E> l, HuffBaseNode<E> r, int wt) {
			root = new HuffInternalNode<E>(l, r, wt);
		}

		public HuffBaseNode<E> root() {
			return root;
		}

		public int weight() { // Weight of tree is weight of root
			return root.weight();
		}

		public int compareTo(HuffTree<E> that) {
			if (root.weight() < that.weight()) {
				return -1;
			}
			else if (root.weight() == that.weight()) {
				return 0;
			}
			else {
				return 1;
			}
		}
	}
	
	/** Build a Huffman tree from list hufflist */
	public HuffTree<Character> buildTree(PriorityQueue<HuffTree<Character>> q) { // from the textbook
		
		HuffTree<Character> temp1, temp2, temp3 = null;
		
		while (q.size() > 1) { // While two items left
			temp1 = q.poll();
			temp2 = q.poll();
			temp3 = new HuffTree<Character> (temp1.root(), temp2.root(), temp1.weight() + temp2.weight());
			q.add(temp3); // Return new tree to heap
		}
		return temp3;
	}

	public static void main(String[] args) {
		Huffman huffman = new HuffmanSubmit();
		huffman.encode("alice30.txt", "alice.enc", "freq.txt");
		huffman.decode("alice.enc", "alice_final.txt", "freq.txt");
		huffman.encode("ur.jpg", "ur.enc", "freq.txt");
		huffman.decode("ur.enc", "ur_dec.jpg", "freq.txt");
   }
}
