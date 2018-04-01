package cas2xb3_A2_nourbaran_ar;

import java.awt.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

public class BostonToMinneapolis {
	private HashMap<String, Integer> hm;
	private ArrayList<String> cityList;
	private ArrayList<String> latitudes;
	private ArrayList<String> longitudes;
	public Digraph graph;
	
	public DepthFirstSearch dfs;
	public BreadthFirstPaths bfs;
	
	private ArrayList<Double> mcdPrices = new ArrayList<Double>();;
	private ArrayList<Double> bkPrices = new ArrayList<Double>();;
	private ArrayList<Double> wenPrices = new ArrayList<Double>();;
	private ArrayList<String> mcdMeals = new ArrayList<String>();;
	private ArrayList<String> bkMeals = new ArrayList<String>();;
	private ArrayList<String> wenMeals = new ArrayList<String>();;
	private String previousMeal = "";
	private Double previousPrice = 0.0;
	private String previousRest = "";

	/**
	 * Initializes a directional graph and lists of each restaurant type's meal items.
	 * 
	 * @throws IOException
	 */
	public BostonToMinneapolis() throws IOException {
		hm = new HashMap<String, Integer>();	//helps keep track of city names by pairing them with an integer to use in graphing
		cityList = new ArrayList<String>();	
		latitudes = new ArrayList<String>();	//all latitudes of cities
		longitudes = new ArrayList<String>();	//all longitudes of cities

		//initialize hashmap of city names and integers, as well as latitudes/logitudes lists
		String line = "";
		BufferedReader reader = new BufferedReader(new FileReader("USCities.csv"));
		line = reader.readLine();
		int hmIndex = 0;
		while ((line = reader.readLine()) != null) {
			String[] lineArray = line.split(",");
			cityList.add(lineArray[3]);
			latitudes.add(lineArray[4]);
			longitudes.add(lineArray[5]);

			hm.put(lineArray[3], hmIndex);
			hmIndex++;
		}
		reader.close();
		graph = startDigraph();

		//initialize prices and meal names of restaurants
		String line3 = "";
		String[] lineArray3 = new String[4];
		BufferedReader reader3 = new BufferedReader(new FileReader("menu.csv"));
		line3 = reader3.readLine();
		while ((line3 = reader3.readLine()) != null) {
			lineArray3 = line3.split(",");
			Double price = Double.parseDouble(lineArray3[2].substring(1));
			if (lineArray3[0].startsWith("Mc")) {
				mcdPrices.add(price);
				mcdMeals.add(lineArray3[1]);
			} else if (lineArray3[0].startsWith("Bur")) {
				bkPrices.add(price);
				bkMeals.add(lineArray3[1]);
			} else {
				wenPrices.add(price);
				wenMeals.add(lineArray3[1]);
			}
		}
	}

	/*
	 * Initializes and maintains a graph with respective verticies and edges
	 */
	public Digraph startDigraph() throws IOException {
		Digraph graph = new Digraph(hm.size());

		BufferedReader reader2 = new BufferedReader(new FileReader("connectedCities.csv"));
		String line2 = "";
		while ((line2 = reader2.readLine()) != null) {
			String[] lineArray2 = line2.split(", ");
			int v = (int) (hm.get(lineArray2[0].toUpperCase()));
			int w = (int) (hm.get(lineArray2[1].toUpperCase()));
			graph.addEdge(v, w);

		}
		reader2.close();
		return graph;
	}

	/**
	 * Calls Depth first search algorithm to find a path from Boston to Minneapolis
	 * @return ArrayList of the path of cities to Minneapolis
	 */
	public ArrayList<String> dfsPath() {
		int minneapolis = hm.get("MINNEAPOLIS");
		DepthFirstSearch bostonToMinneapolis = new DepthFirstSearch(this.graph, 0);
		Iterable<Integer> path = bostonToMinneapolis.pathTo(minneapolis);
		dfs = bostonToMinneapolis;

		ArrayList<String> pathList = new ArrayList<String>();
		Iterator iterator = path.iterator();
		while (iterator.hasNext()) {
			int i = (int) iterator.next();
			pathList.add(cityList.get(i));
		}
		return pathList;
	}
	/**
	 * Calls Breadth first search algorithm to find a path from Boston to Minneapolis
	 * @return ArrayList of the path of cities to Minneapolis
	 */
	public ArrayList<String> bfsPath() {
		int minneapolis = hm.get("MINNEAPOLIS");
		BreadthFirstPaths bostonToMinneapolis = new BreadthFirstPaths(this.graph, 0);
		Iterable<Integer> path = bostonToMinneapolis.pathTo(minneapolis);

		ArrayList<String> pathList = new ArrayList<String>();
		Iterator iterator = path.iterator();
		while (iterator.hasNext()) {
			int i = (int) iterator.next();
			pathList.add(cityList.get(i));
		}
		Collections.reverse(pathList); 		//reverses path list so that Boston is first and Minneapolis is last.
		return pathList;
	}

	/**
	 * Finds the cheapest places to eat in each city in the given Path
	 * @param path ArrayList of cities from Boston to Minneapolis to find restaurants for
	 * @return an Array of strings for, each element consisting of the City, Meal, and Price
	 */
	public String[] restaurants(ArrayList<String> path) {
		int index;
		String[] allRests = new String[path.size()-1];
		boolean[] mcdonalds = new boolean[path.size() - 1];
		boolean[] bk = new boolean[path.size() - 1];
		boolean[] wendys = new boolean[path.size() - 1];
		for (int i = 1; i < path.size(); i++) {
			index = hm.get(path.get(i));
			Double latitude = Double.parseDouble(latitudes.get(index));
			Double longitude = Double.parseDouble(longitudes.get(index));
			if (mcdonaldsExists(latitude, longitude)) {
				mcdonalds[i - 1] = true;
			}
			if (burgerKingExists(latitude, longitude)) {
				bk[i - 1] = true;
			}
			if (wendysExists(latitude, longitude)) {
				wendys[i - 1] = true;
			}
			pickFoodIndex();
			String thisRest = String.format("%-20s %-35s %-10s", path.get(i), previousMeal, previousPrice);
			allRests[i-1] = thisRest;
		}
		return allRests;

	}
	/**
	 * Checks to see if mcdonalds is available in the city given
	 * @param lat latitude of city
	 * @param lon longitude of city
	 * @return True if mcdonalds exists in city, False otherwise
	 */
	public boolean mcdonaldsExists(Double lat, double lon) {

		try {
			String line = "";
			String[] lineArray = new String[4];
			BufferedReader reader = new BufferedReader(new FileReader("mcdonalds.csv"));
			while ((line = reader.readLine()) != null) {
				lineArray = line.split(",");
				Double thisLat = Double.parseDouble(lineArray[1]);
				Double thisLon = Double.parseDouble(lineArray[0]);
				if (Math.abs(thisLat - lat) <= 0.5 && Math.abs(thisLon - lon) <= 0.5) {
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * Checks to see if burger-king is available in the city given
	 * @param lat latitude of city
	 * @param lon longitude of city
	 * @return True if burger-king exists in city, False otherwise
	 */
	public boolean burgerKingExists(Double lat, double lon) {
		try {
			String line = "";
			String[] lineArray = new String[4];
			BufferedReader reader = new BufferedReader(new FileReader("burgerking.csv"));
			while ((line = reader.readLine()) != null) {
				lineArray = line.split(",");
				Double thisLat = Double.parseDouble(lineArray[1]);
				Double thisLon = Double.parseDouble(lineArray[0]);
				if (Math.abs(thisLat - lat) <= 0.5 && Math.abs(thisLon - lon) <= 0.5) {
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * Checks to see if wendys is available in the city given
	 * @param lat latitude of city
	 * @param lon longitude of city
	 * @return True if mwendys exists in city, False otherwise
	 */
	public boolean wendysExists(Double lat, double lon) {
		try {
			String line = "";
			String[] lineArray = new String[4];
			BufferedReader reader = new BufferedReader(new FileReader("wendys.csv"));
			while ((line = reader.readLine()) != null) {
				lineArray = line.split(",");
				Double thisLat = Double.parseDouble(lineArray[1]);
				Double thisLon = Double.parseDouble(lineArray[0]);
				if (Math.abs(thisLat - lat) <= 0.5 && Math.abs(thisLon - lon) <= 0.5) {
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Picks the cheapest food option in the given restuarants
	 */
	public void pickFoodIndex() {
		int minIndex = 0;
		String food;
		Double thisPrice;
		String thisRest;
		Double minMcd = Collections.min(mcdPrices);
		Double minBk = Collections.min(bkPrices);
		Double minWen = Collections.min(wenPrices);
		if (minMcd <= minWen	&& minMcd <= minBk) {
			minIndex = mcdPrices.indexOf(minMcd);
			food = mcdMeals.get(minIndex);
			mcdPrices.remove(minIndex);
			mcdMeals.remove(minIndex);
			thisPrice = minMcd;
			thisRest = "mcdonalds";
		} else if (minBk <= minWen	&& minBk <= minMcd) {
			minIndex = bkPrices.indexOf(minBk);
			food = bkMeals.get(minIndex);
			bkPrices.remove(minIndex);
			bkMeals.remove(minIndex);
			thisPrice = minBk;
			thisRest = "bk";
		} else {
			minIndex = wenPrices.indexOf(minWen);	
			food = wenMeals.get(minIndex);	
			wenPrices.remove(minIndex);
			wenMeals.remove(minIndex);
			thisPrice = minWen;	
			thisRest = "wendys";
		}
		
		switch (previousRest) {
		case "mcdonalds":
			mcdPrices.add(previousPrice);
			mcdMeals.add(previousMeal);
			break;
		case "bk":
			bkPrices.add(previousPrice);
			bkMeals.add(previousMeal);			
			break;
		case "wendys":
			wenPrices.add(previousPrice);
			wenMeals.add(previousMeal);			
			break;

		default:
			break;
		}
		previousMeal = food;
		previousPrice = thisPrice;
		previousRest = thisRest;


	}

	public static void main(String[] args) throws IOException {
		BostonToMinneapolis btm = new BostonToMinneapolis();
		PrintWriter writer = new PrintWriter("a2_out.txt");
		
		ArrayList<String> path1 = btm.bfsPath();
		writer.print("BFS: " + path1.get(0));
		for (int i=1; i<path1.size(); i++) {
			String sentence = ", " + path1.get(i);
			writer.print(sentence);
		}
		writer.println();
		
		ArrayList<String> path2 = btm.dfsPath();
		writer.print("DFS: " + path2.get(0));
		for (int i=1; i<path2.size(); i++) {
			String sentence = ", " + path2.get(i);
			writer.print(sentence);
		}
		writer.println();
		writer.println();
		
		String[] allRests = btm.restaurants(path1);

		String headers = String.format("%-20s %-35s %-10s", "City", "Meal Choice", "Cost of Meal");
		String headers2 = String.format("%-20s %-35s %-10s", "____", "___________", "____________");
		writer.println(headers);
		writer.println(headers2);
		for (int i=0; i<allRests.length; i++) {
			writer.println(allRests[i]);
		}
		
		writer.close();

	}

}
