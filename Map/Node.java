/* 
 * Elana Chen-Jones
 * Net ID: echenjon
 * Project 3
*/

import java.util.ArrayList;

public class Node implements Comparable<Node> { // represents a node, aka an intersection

	String ID;
	double latitude, longitude;
	ArrayList<Node> adjList = new ArrayList<Node>(10);
	double distance;
	Node prev;
	boolean visited = false;
	
	public Node() { // empty constructor
		
	}
	
	public Node(String ID, double latitude, double longitude) { // constructor
		this.ID = ID;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	/** @return the ID of a node */
	public String getID() {
		return ID;
	}
	
	/** @return the latitude of a node */
	public double getLatitude() {
		return latitude;
	}
	
	/** @return the longitude of a node */
	public double getLongitude() {
		return longitude;
	}

	/** Compares the distances of two nodes from a starting node
	 * @param the second node to compare */
	public int compareTo(Node n) {
		if (this.distance > n.distance) {
			return 1;
		}
		else if (this.distance < n.distance) {
			return -1;
		}
		else {
			return 0;
		}
	}
	
}
