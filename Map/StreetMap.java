import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Stack;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class StreetMap extends Canvas implements ActionListener {

	String ID;
	double latitude, longitude;
	static Graph g;
	double width, height;
	double scaleX, scaleY;
	double minLat, maxLat, minLong, maxLong;
	static LinkedList<Node> list;

	Graphics gr = getGraphics();
	Node n1 = new Node();
	Node n2 = new Node();
	
	JLabel label1 = addLabel("   Start:", 45, 30);
	JTextField text1 = addText(150, 30);
	JLabel label2 = addLabel("End:", 30, 30);
	JTextField text2 = addText(150, 30);
	JButton button = addButton("Calculate route", 150, 30);
	
	String startVert, endVert;
	boolean hasStartVert = false;
	boolean hasEndVert = false;
	
	/** Reads a file of intersections and roads and adds them to hash maps
	 * @param filename the name of the file to be read */
	public void readFile(String filename) {
		HashMap<String, Node> vert = new HashMap<String, Node>();
		HashMap<String, Edge> edge = new HashMap<String, Edge>();
		try {
			File file = new File(filename);
			Scanner scan = new Scanner(file);
			while(scan.hasNext()) {
				String a = scan.next();
				String b = scan.next();
				if (a.equals("i")) {
					String lt = scan.next();
					latitude = Double.parseDouble(lt);
					String lg = scan.next();
					longitude = Double.parseDouble(lg);
					Node v = new Node(b, latitude, longitude);
					vert.put(b,v);
				}
				else {
					String int1 = scan.next();
					Node i1 = vert.get(int1);
					String int2 = scan.next();
					Node i2 = vert.get(int2);
					Edge e = new Edge(i1, i2);
					edge.put(b, e);
					i1.adjList.add(i2);
					i2.adjList.add(i1);
				}
			}
			scan.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		g = new Graph(vert, edge);
	}
	
	/** Implementation of Dijkstra's algorithm using the pseudocode from lecture
	 * in the shortest path between two nodes
	 * @param start the beginning intersection
	 * @param end the ending intersection
	 * @return a linked list of nodes */
	public LinkedList<Node> Dijkstra(Node start, Node end) {
		for (String st : g.vert.keySet()) {
			Node v = g.vert.get(st);
			v.distance = Double.MAX_VALUE;
			v.prev = null;
			v.visited = false;
		}
		start.distance = 0;
		LinkedList<Node> L = new LinkedList<Node>();
		PriorityQueue<Node> Q = new PriorityQueue<Node>();
		Q.add(start);
		while (!Q.isEmpty()) {
			Node n = Q.poll();
			if(!n.visited) {
				for (Node o : n.adjList) {
					Edge e = new Edge(n, o);
					if (o.distance > n.distance + e.getLength()) {
						Q.remove(o);
						o.distance = n.distance + e.getLength();
						o.prev = n;
						Q.add(o);
					}
				}
			}
			n.visited = true;
		}
		Stack<Node> s = new Stack<Node>();
		if (end.distance < Double.MAX_VALUE) {
			s.add(end);
			while (s.peek() != null && s.peek() != start) {
				s.push(s.peek().prev);
			}
			double totalDistance = end.distance;
			System.out.println("Total distance: " + totalDistance + " miles");
		}
		else {
			System.out.println("No path found");
		}
		while (!s.isEmpty()) {
			L.add(s.pop());
		}
		
		return L;
	}
	
	/** Scales the map to fit the window */
	public void scale() {
		HashMap<String, Node> vert = g.vert;
		maxLat = -1 * Double.MAX_VALUE;
		maxLong = -1 * Double.MAX_VALUE;
		minLat = Double.MAX_VALUE;
		minLong = Double.MAX_VALUE;
		for (Node n : vert.values()) {
			if (n.latitude > maxLat) {
				maxLat = n.latitude;
			}
			if (n.latitude < minLat) {
				minLat = n.latitude;
			}
			if (n.longitude > maxLong) {
				maxLong = n.longitude;
			}
			if (n.longitude < minLong) {
				minLong = n.longitude;
			}
		}
		
		double latDist = maxLat - minLat;
		double longDist = (maxLong - minLong) * Math.cos((maxLat + minLat) * Math.PI/360);
		double angleToPixels = Math.min(this.getHeight()/latDist, this.getWidth()/longDist);
		scaleX = Math.cos((maxLat + minLat) * Math.PI/360) * angleToPixels;
		scaleY = angleToPixels;
	}
	
	/** Draws an edge of the map 
	 * @param v1 the starting intersection
	 * @param v2 the ending intersection */
	public void drawEdge(Node v1, Node v2) {
		double vert1Lat = (v1.latitude - minLat) * scaleY;
		double vert1Long = (v1.longitude - minLong) * scaleX;
		double vert2Lat = (v2.latitude - minLat) * scaleY;
		double vert2Long = (v2.longitude - minLong) * scaleX;
		gr.drawLine((int)vert1Long, this.getHeight() - (int)vert1Lat, (int)vert2Long, this.getHeight() - (int)vert2Lat);
	}
	
	/** Draws the map using drawEdge on a hash map of edges
	 * @param edge a hash map of all the edges */
	public void drawMap(HashMap<String, Edge> edge) {
		for (Edge e : edge.values()) {
			drawEdge(e.vert1, e.vert2);
		}
	}
	
	/** Draws a single shortest path between two nodes in a linked list 
	 * @param c the color the path is to be drawn in */
	public void drawPath(Color c) {
		gr.setColor(c);
		Graphics2D gr2 = (Graphics2D) gr;
		gr2.setStroke(new BasicStroke(3));
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size() - 1; i++) {
				drawEdge(list.get(i), list.get(i+1));
			}
		}
	}
	
	/** Creates and returns a JLabel 
	 * @param name what to name the label
	 * @param x the x dimension of the label
	 * @param y the y dimension of the label
	 * @return the new JLabel */
	public JLabel addLabel(String name, int x, int y) {
		JLabel label = new JLabel(name);
		label.setPreferredSize(new Dimension(x, y));
		return label;
	}
	
	/** Creates and returns a JTextField
	 * @param x the x dimension of the text field
	 * @param y the y dimension of the text field
	 * @return the new JTextField */
	public JTextField addText(int x, int y) {
		JTextField text = new JTextField();
		text.setPreferredSize(new Dimension(x, y));
		text.addActionListener(this);
		return text;
	}
	
	/** Creates and returns a JButton 
	 * @param name what to name the button
	 * @param x the x dimension of the button
	 * @param y the y dimension of the button
	 * @return the new JButton */
	public JButton addButton(String name, int x, int y) {
		JButton button = new JButton(name);
		button.setPreferredSize(new Dimension(x, y));
		button.addActionListener(this);
		return button;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(text1)) { // gets the first intersection
			startVert = text1.getText();
			if (g.vert.containsKey(startVert)) {
				n1 = g.vert.get(startVert);
				hasStartVert = true;
				System.out.println(startVert);
			}
			else {
				System.out.println("Invalid intersection");
			}
		}
		else if (e.getSource().equals(text2)) { // gets the second intersection
			endVert = text2.getText();
			if (g.vert.containsKey(endVert)) {
				n2 = g.vert.get(endVert);
				hasEndVert = true;
				System.out.println(endVert);
			}
			else {
				System.out.println("Invalid intersection");
			}
		}
		else if (e.getSource().equals(button)) { // draws the shortest path between the two intersections
			if (hasStartVert && hasEndVert) { // if there is a start and an end intersection
				drawPath(Color.BLACK); // draw black path over where current red path is
				list = Dijkstra(n1, n2);
				drawPath(Color.RED); // draw red path from n1 to n2
				hasStartVert = false;
				hasEndVert = false;
				repaint();
			}
			else {
				System.out.println("One or more invalid intersection(s)");
			}
		}
	}
	
	@Override
	public void paint(Graphics graphics) {
		this.gr = graphics;
		gr.setColor(Color.BLACK);
		scale();
		HashMap<String, Edge> edge = g.edge;
		drawMap(edge);
		drawPath(Color.RED);
	}
	
	public static void main(String[] args) {
		StreetMap s = new StreetMap();
		JFrame frame = new JFrame();
		JPanel panel = new JPanel();
		if (args.length > 0) {
			s.readFile(args[0]);
			for (int i = 0; i < args.length; i++) {
				if (args[i].equals("--show")) {
					panel.setLayout(new GridBagLayout());
					GridBagConstraints gbc = new GridBagConstraints();
					
					gbc.gridx = 0;
					gbc.gridy = 0;
					panel.add(s.label1, gbc);
					
					gbc.gridx = 1;
					gbc.gridy = 0;
					panel.add(s.text1, gbc);
					
					gbc.gridx = 2;
					gbc.gridy = 0;
					panel.add(s.label2, gbc);

					gbc.gridx = 3;
					gbc.gridy = 0;
					panel.add(s.text2, gbc);
					
					gbc.gridx = 4;
					gbc.gridy = 0;
					panel.add(s.button, gbc);
					
					gbc.gridx = 0;
					gbc.gridy = 1;
					gbc.anchor = GridBagConstraints.PAGE_END;
					gbc.gridwidth = 5;
					gbc.weighty = 0;
					panel.add(s, gbc);

					s.setSize(600, 600);
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					frame.add(panel);
					frame.pack();
					frame.setVisible(true);
				}
				if (args[i].equals("--directions")) {
					String i1 = args[i+1]; // assumes that intersection 1 is the item directly following --directions
					s.text1.setText(i1);
					String i2 = args[i+2]; // and that intersection 2 is the item directly following intersection 1
					s.text2.setText(i2);
					s.n1 = g.vert.get(i1);
					s.n2 = g.vert.get(i2);
					list = s.Dijkstra(s.n1, s.n2);
				}
			}
		}
	}
}
