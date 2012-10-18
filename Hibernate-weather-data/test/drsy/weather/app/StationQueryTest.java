package drsy.weather.app;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import drsy.weather.data.Network;
import drsy.weather.data.Station;
import drsy.weather.data.Wdata;
import drsy.weather.data.WdataKey;

public class StationQueryTest {

	@Before
	public void before() {
		System.out.println("\n-----------------------------------------------------");
	}
	
	@Test
	public void testCriteria() {
		testQuery ppl = new testQuery();
		String id = "059ZX";
		Station template = new Station();
		template.setId(id);
		List<Station> list = ppl.find(template);
		Assert.assertNotNull("List NULL!! :(", list);
		System.out.println("\nfound " + list.size() + " results");
		for (Station p : list)
			System.out.println(p);
	}
	
	@Test
	public void testQueryHQL() {
		testQuery ppl = new testQuery();
		Network list = ppl.find(10);
		Assert.assertNotNull(list);
		System.out.println(list.getName());
	}
	
	@Test
	@Ignore
	public void testWdata() {
		testQuery ppl = new testQuery();
		float id = -9999;
		List<Wdata> list = ppl.find(id);
		Assert.assertNotNull(list);
		System.out.println("\nfound " + list.size() + " results");
//		for (Wdata p : list)
//			System.out.println(p.getNetwork());
	}
	
	@Test
	public void testWdatakey() {
		WdataKey key = new WdataKey();
		testQuery ppl = new testQuery();
		int date = 20121005;
		String station = "DKRM8";
		int time = 1215;
		key.setDate(date);
		key.setStation(station);
		key.setTime(time);
		Wdata list = ppl.find(key);
		Assert.assertNotNull(list);
		System.out.println("\nfound results");
		System.out.println(list.getNetwork() + "    " + list.getStation());
	}
	
	@Test
	@Ignore
	public void testaverageTemp() {
		WdataKey key = new WdataKey();
		testQuery ppl = new testQuery();
		int date = 20121005;
		String station = "DKRM8";
		int time = 1215;
		key.setDate(date);
		key.setStation(station);
		key.setTime(time);
		ArrayList<Wdata> list = ppl.averageTemperature(key);
		Assert.assertNotNull(list);
		System.out.println("\nfound results" + list.size());
		
	}
	
	@Test
	public void testStation() {
		testQuery ppl = new testQuery();
		String station = "DKRM8";
		Station template = new Station();
		template.setId(station);
		List<Station> list = ppl.getStation(template);
		Assert.assertNotNull(list);
		System.out.println("Results Found ---> " + list.size());
	}
	
	@Test
	public void averagedataStation() {
		testQuery ppl = new testQuery();
		String station = "DKRM8";
		Station template = new Station();
		template.setId(station);
		List<Station> list = ppl.getStation(template);
		Assert.assertNotNull(list);
		System.out.println("Results Found ---> " + list.size());
		for(Station  l : list) {
			int count = 0;
			float temp = 0, avg = 0;
			Collection<Wdata> o = l.getWdatas();
			Iterator<Wdata> itr = o.iterator();
			while(itr.hasNext()) {
				Wdata u = itr.next();
				temp += u.getTmpf();
				count++;
			}
			avg = temp/count;
			System.out.println("----> AVERAGE TEMPERATURE:  " + avg + " AT STATION: " + station);
		}
	}
	
	@Test
	public void testdatathroughdate() {
		testQuery ppl = new testQuery();
		int date = 20121005;
		String state = "CA";
		float avg = 0;
		int count = 0;
		float temp = 0;
		List<Station> list = ppl.getDataonDate(date, state);
		Assert.assertNotNull(list);
		System.out.println("Results Found ---> " + list.size());
		for(Station  l : list) {
			Collection<Wdata> o = l.getWdatas();
			Iterator<Wdata> itr = o.iterator();
			while(itr.hasNext()) {
				Wdata u = itr.next();
				temp += u.getTmpf();
				count++;
			}
		}
		avg = temp/count;
		System.out.println("----> AVERAGE TEMPERATURE:  " + avg + " AT STATE: " + state);
	}
	
	@Test
	public void countStations() {
		testQuery ppl = new testQuery();
		String country = "US";
		List<Station> d = ppl.countStations(country);
		System.out.println("Number of stations other than " + country + " are: " + d.size());
	}
	
	@Test
	public void stationsRange() {
		testQuery ppl = new testQuery();
		float lat1 = 43;
		float lat2 = 45;
		float lon1 = -93;
		float lon2 = -83;
		List<Station> d = ppl.getStationsbetweenlatnlon(lat1, lat2, lon1, lon2);
		System.out.println("Number of stations between Latitudes(" + lat1 + ", " + lat2 + ") & Longitudes: (" + lon1 + ", " + lon2 + ") are: " + d.size());
		for(Station  l : d) {
			int count = 0;
			float temp = 0, avg = 0;
			Collection<Wdata> o = l.getWdatas();
			Iterator<Wdata> itr = o.iterator();
			while(itr.hasNext()) {
				Wdata u = itr.next();
				temp += u.getTmpf();
				count++;
			}
			avg = temp/count;
			System.out.println("----> AVERAGE TEMPERATURE:  " + avg + " AT STATION: " + l.getName());
		}
	}
	
	@Test
	public void testStationdatathroughdate() {
		testQuery ppl = new testQuery();
		int date = 20121005;
		float avg = 0;
		int count = 0;
		float temp = 0;
		List<Station> list = ppl.getStationsonDate(date);
		Assert.assertNotNull(list);
		System.out.println("Results Found ---> " + list.size());
//		for(Station  l : list) {
//			Collection<Wdata> o = l.getWdatas();
//			Iterator<Wdata> itr = o.iterator();
//			while(itr.hasNext()) {
//				Wdata u = itr.next();
//				temp += u.getTmpf();
//				count++;
//			}
//		}
//		avg = temp/count;
//		System.out.println("----> AVERAGE TEMPERATURE:  " + avg + " AT STATE: " + state);
	}
}
