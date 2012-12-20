package gash.hibernate.app;

import gash.hibernate.data.Person;

import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

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

	// @Test
	public void testCacheCard() {
		People ppl = new People();
		final int N = 100;
		double count = 0, fcount = 0;
		double fst = System.currentTimeMillis();
		double st = 0, fet = 0;
		for (int n = 0; n < N; n++) {
			List<Person> list = ppl.findEmailByRole("Friend");
			if (n == 0) {
				fet = System.currentTimeMillis();
				st = fet;
				fcount = list.size();
			} else
				count += list.size();
		}
		double et = System.currentTimeMillis();

		System.out
				.println("\nemail query performance test (new Card, no cache), " + (count + fcount) + " records in " + (et - fst) + " msec");
		System.out.println("first: " + (fet - fst) + " msec, " + (fcount / (fet - fst)) + " rec/msec");
		System.out.println("after: " + ((et - st) / (N - 1)) + " msec, " + (count / (et - st)) + " rec/msec\n");
	}

	@Test
	public void testCacheFind() {
		People ppl = new People();
		final int N = 100;
		double count = 0, fcount = 0;
		double fst = System.currentTimeMillis();
		double st = 0, fet = 0;
		for (int n = 0; n < N; n++) {
			Person template = new Person();
			template.setRole("Friend");
			List<Person> list = ppl.find(template);
			if (n == 0) {
				fet = System.currentTimeMillis();
				st = fet;
				fcount = list.size();
			} else
				count += list.size();
		}
		double et = System.currentTimeMillis();

		System.out.println("\nfind performance test (criteria, no cache), " + (count + fcount) + " records in " + (et - fst) + " msec");
		System.out.println("    first: " + (fet - fst) + " msec, " + (fcount / (fet - fst)) + " rec/msec");
		System.out.println("    after: " + ((et - st) / (N - 1)) + " msec, " + (count / (et - st)) + " rec/msec\n\n");
		
		ppl.showStats();
	}
}
