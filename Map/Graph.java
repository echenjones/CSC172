/* 
 * Elana Chen-Jones
 * Net ID: echenjon
 * Project 3
*/

import java.util.ArrayList;
import java.util.HashMap;

public class Graph { // Graph class ADT
	
	ArrayList<Node> adjList = new ArrayList<Node>();
	HashMap<String, Node> vert = new HashMap<String, Node>();
	HashMap<String, Edge> edge = new HashMap<String, Edge>();
	
	public Graph(ArrayList<Node> adjList) { // constructor
		this.adjList = adjList;
	}
	
	public Graph(HashMap<String, Node> vert, HashMap<String, Edge> edge) { // constructor
		this.vert = vert;
		this.edge = edge;
	}
	
	/** @return a list of all nodes */
	public ArrayList<Node> getNodes() {
		return adjList;
	}
}
