package drsy.weather.app;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import drsy.weather.data.Station;

/**
 * NOTE: only allow ONE test to be defined or the cache will interfere with
 * timing results
 * 
 * @author gash
 * 
 */
public class CacheQueryTest {

	@Before
	public void before() {
		System.out.println("\n-----------------------------------------------------");
	}

	@Test
	public void testCacheFind() {
		testQuery ppl = new testQuery();
		final int N = 100;
		double count = 0, fcount = 0;
		double fst = System.currentTimeMillis();
		double st = 0, fet = 0;
		for (int n = 0; n < N; n++) {
			Station template = new Station();
			template.setNetwork(170);
			List<Station> list = ppl.findNetworks(template);
			if (n == 0) {
				fet = System.currentTimeMillis();
				st = fet;
				fcount = list.size();
			} else
				count += list.size();
		}
		double et = System.currentTimeMillis();

		System.out.println("\nquery performance test while fetching wdata (Criteria, no cache), " + (count + fcount) + " records in " + (et - fst) + " msec");
		System.out.println("    first: " + (fet - fst) + " msec, " + (fcount / (fet - fst)) + " rec/msec");
		System.out.println("    after: " + ((et - st) / (N - 1)) + " msec, " + (count / (et - st)) + " rec/msec\n\n");
		
	}
	
	@Test
	public void testCacheCard() {
		testQuery ppl = new testQuery();
		final int N = 100;
		double count = 0, fcount = 0;
		double fst = System.currentTimeMillis();
		double st = 0, fet = 0;
		for (int n = 0; n < N; n++) {
			int network = 170;
			List<Station> list = ppl.findNetworks(network);
			if (n == 0) {
				fet = System.currentTimeMillis();
				st = fet;
				fcount = list.size();
			} else
				count += list.size();
		}
		double et = System.currentTimeMillis();

		System.out
				.println("\nquery performance test Station without fetching wdata (new Criteria, no cache), " + (count + fcount) + " records in " + (et - fst) + " msec");
		System.out.println("first: " + (fet - fst) + " msec, " + (fcount / (fet - fst)) + " rec/msec");
		System.out.println("after: " + ((et - st) / (N - 1)) + " msec, " + (count / (et - st)) + " rec/msec\n");
	}
}
