package gash.hibernate.app;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class LocationHashTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testLocate() {
		LocationHash h = new LocationHash(2);
		String list = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-=";
		for (int n = 0, N = list.length(); n < N; n++) {
			System.out.println(list.charAt(n) + " = " + h.locate("" + list.charAt(n)));
		}
	}

}
