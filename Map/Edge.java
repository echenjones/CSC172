/* 
 * Elana Chen-Jones
 * Net ID: echenjon
 * Project 3
*/

public class Edge { // represents an edge, aka a road

	Node vert1, vert2; 
	double length;
	
	public Edge(Node vert1, Node vert2) { // constructor, initializing length to -1
		this.vert1 = vert1;
		this.vert2 = vert2;
		this.length = -1;
	}
	
	/** @return vert1 */
	public Node getVertex1() {
		return vert1;
	}
	
	/** @return vert2 */
	public Node getVertex2() {
		return vert2;
	}
	
	/** Calculates the length between two coordinates 
	 * @return the length of a path */
	public double getLength() { // calculates length using Haversine formula
		double lat1 = vert1.latitude * (Math.PI/180);
		double lat2 = vert2.latitude * (Math.PI/180);
		double long1 = vert1.longitude * (Math.PI/180);
		double long2 = vert2.longitude * (Math.PI/180);
		if (length < 0) {
			// originally used haversine formula, but found the formula to be less accurate than the one below
			// length = haversine(lat2 - lat1) + (Math.cos(lat1) * Math.cos(lat2) * haversine(long2 - long1));
			// length *= 6371000;
			length = Math.sqrt((lat2 - lat1) * (lat2 - lat1) + Math.pow((long2 - long1) * (Math.cos((lat1 + lat2)/2)), 2));
			length *= 6371000/1609.344;
		}
		return length;
	}
	
	/** A helper method for the haversine formula 
	 * @return temp * temp */
	public double haversine(double t) {
		double temp = Math.sin(t/2);
		return temp * temp;
	}
}
