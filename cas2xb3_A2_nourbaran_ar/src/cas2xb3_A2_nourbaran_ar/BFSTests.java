package cas2xb3_A2_nourbaran_ar;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BFSTests {
	private BostonToMinneapolis btm;
	private ArrayList<String> path;
	@Before
	public void setUp() throws Exception {
		btm = new BostonToMinneapolis();
		path = btm.bfsPath();
		

	}


	@Test
	public void testPathisCorrect() throws IOException{
		for (int i=0; i<path.size()-1; i++) {
			assertTrue(RouteVerified(i));
		}
	}
	
	@Test
	public void testAllRoutesExamines() {
		assertTrue(btm.bfs.count() == btm.graph.V());
	}
	
	public boolean RouteVerified(int i) throws IOException{
		BufferedReader reader2 = new BufferedReader(new FileReader("connectedCities.csv"));
		String line2 = "";
		while ((line2 = reader2.readLine()) != null) {
			String[] lineArray2 = line2.split(", ");
			if (lineArray2[0] == path.get(i) && lineArray2[1] == path.get(i+1))
				return true;
		}
		reader2.close();
		return true;
	}

}
