package cas2xb3_A2_nourbaran_ar;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import edu.princeton.cs.algs4.Stack;

public class DepthFirstSearch {
	private boolean[] marked;
	private int count;
	private int[] edgeTo;
	private final int s;

	public DepthFirstSearch(Digraph G, int s) {
		marked = new boolean[G.V()];
		edgeTo = new int[G.V()];
		this.s = s;
		dfs(G, s);
	}

	private void dfs(Digraph G, int v) {
		marked[v] = true;
		count++;
		for (int w : G.adj(v)) {
			if (!marked[w]) {
				edgeTo[w] = v;
				dfs(G, w);
			}
		}
	}
	public boolean hasPathTo(int v) {
		return marked[v];
	}
	public Iterable<Integer> pathTo(int v) {
		if (!hasPathTo(v)) return null;
		
		Stack<Integer> path = new Stack<Integer>();
		for (int x = v; x != s; x = edgeTo[x]) {
			path.push(x);
		}
		path.push(s);
		return path;
	}
	public int count() {
		return count;
	}
	public static void main(String[] args) throws IOException {
		String line = "";
		HashMap<String, Integer > hm = new HashMap<String, Integer>();
		ArrayList<String> list = new ArrayList<String>(); 
		BufferedReader reader = new BufferedReader(new FileReader("USCities.csv"));
		line = reader.readLine();
		
		int hmIndex = 0;
		while ((line = reader.readLine()) != null) {
			String[] lineArray = line.split(",");
			list.add(lineArray[3]);
			hm.put(lineArray[3], hmIndex);
			hmIndex++;
		}
		reader.close();
		Digraph graph = new Digraph(hm.size());
		
		BufferedReader reader2 = new BufferedReader(new FileReader("connectedCities.csv"));
		String line2 = "";
		while ((line2 = reader2.readLine()) != null) {
			String[] lineArray2 = line2.split(", ");
			int v = (int) (hm.get(lineArray2[0].toUpperCase()));
			int w = (int) (hm.get(lineArray2[1].toUpperCase()));
			graph.addEdge(v,w);
			
		}
		reader2.close();
		int minneapolis = hm.get("MINNEAPOLIS");
		DepthFirstSearch bostonToMinneapolis = new DepthFirstSearch(graph, 0);
		Iterable<Integer> path = bostonToMinneapolis.pathTo(minneapolis);
		Iterator iterator = path.iterator();
		while (iterator.hasNext()) {
			int i = (int) iterator.next();
			System.out.println(list.get(i));
		}
	}
}
