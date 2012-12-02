package gash.indexing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.SlicePredicate;

import me.prettyprint.cassandra.model.IndexedSlicesQuery;
import me.prettyprint.cassandra.serializers.LongSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.serializers.UUIDSerializer;
import me.prettyprint.cassandra.service.CassandraHostConfigurator;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.OrderedRows;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.RangeSlicesQuery;

public class Insertion {

	public Insertion() {
		// TODO Auto-generated constructor stub
	}

	final static Cluster cluster = HFactory.createCluster("Test Cluster", new CassandraHostConfigurator("localhost:9160"));
	final static Keyspace keyspace = HFactory.createKeyspace("Project226", cluster);

	static String filename;
	private static StringSerializer ss = StringSerializer.get();

	public void insert(String key, String freq, String mean, String bukname,
			long totalKeys) {
		// TODO Auto-generated method stub
		Mutator<String> m1 = HFactory.createMutator(keyspace, ss);
		String mkey = key + "(" + bukname + ")";
		m1.addInsertion(mkey,
				"p226Books",
				HFactory.createStringColumn("Name", bukname))
				.addInsertion(mkey,
						"p226Books",
						HFactory.createStringColumn("Keyword", key))
						.addInsertion(mkey,
								"p226Books",
								HFactory.createStringColumn("Frequency", freq))
								.addInsertion(mkey,
										"p226Books",
										HFactory.createStringColumn("Mean", mean))
										.addInsertion(mkey,
												"p226Books",
												HFactory.createStringColumn("StdDev", "null"))
												.addInsertion(mkey,
														"p226Books",
														HFactory.createStringColumn("Percentage", String.valueOf((Double)Double.parseDouble(freq)/totalKeys * 100)))
														;
		m1.execute();

	}

	public void insert(String bukname, String frq, String mean, String stddev, String buk, long totalkeys){
		;
		if(totalkeys==0){
			totalkeys=1;
		}
		Mutator<String> m1 = HFactory.createMutator(keyspace, ss);
		String mkey = bukname + "(" + buk + ")";
		m1.addInsertion(mkey,
				"p226Books",
				HFactory.createStringColumn("Name", buk))
				.addInsertion(mkey,
						"p226Books",
						HFactory.createStringColumn("Keyword", bukname))
						.addInsertion(mkey,
								"p226Books",
								HFactory.createStringColumn("Frequency", frq))
								.addInsertion(mkey,
										"p226Books",
										HFactory.createStringColumn("Mean", mean))
										.addInsertion(mkey,
												"p226Books",
												HFactory.createStringColumn("StdDev", stddev))
												.addInsertion(mkey,
														"p226Books",
														HFactory.createStringColumn("Percentage", String.valueOf(Long.parseLong(frq)*100/totalkeys)))
														;
		m1.execute();
	}

	/*
	 *//**
	 * Get a string value.
	 * @return The string value; null if no value exists for the given key.
	 */
	public String get(final String key) throws Exception {
		StringBuilder searchresult = new StringBuilder();
		IndexedSlicesQuery<String, String, String> isq = HFactory.createIndexedSlicesQuery(keyspace, ss, ss, ss);
		isq.addEqualsExpression("Keyword", key);
		isq.setColumnNames("Name","Frequency", "Percentage");
		isq.setColumnFamily("p226Books");
		QueryResult<OrderedRows<String, String, String>> result = isq.execute();
		isq.setStartKey("");
		OrderedRows<String, String, String> rows = result.get();
		System.out.println("here");
		Iterator<Row<String, String, String>> rowsIterator =rows.iterator();
		while (rowsIterator.hasNext())
		{
			Row<String, String, String> row = rowsIterator.next();
			String row1 = row.getKey();
			String name = row.getColumnSlice().getColumnByName("Name").getValue();
			String frequency = row.getColumnSlice().getColumnByName("Frequency").getValue();
			String Percentage = row.getColumnSlice().getColumnByName("Percentage").getValue();
			searchresult.append(row1 + ", " + name + ", " + frequency + ", " + Percentage + "\n");//+", "+lon+")\n";
		}
		return searchresult.toString();
	}

	public String getAll() throws Exception {
		StringBuilder searchresult = new StringBuilder();
		// just look for one column for now
		RangeSlicesQuery<String, String, String> rangeSlicesQuery =
				HFactory.createRangeSlicesQuery(keyspace, ss, ss, ss);
				rangeSlicesQuery.setColumnFamily("p226BooksData");
				rangeSlicesQuery.setKeys(null, null);
				rangeSlicesQuery.setRange("Contents", "Title",  false, 10);
				QueryResult<OrderedRows<String, String, String>> result = rangeSlicesQuery.execute();
		//Map map = keyspace.getRangeSlice(cp, sp, "", "", 1000);
		searchresult.append(result.getQuery());
		searchresult.append("\n"+result.get().getList().toString());
		for(Row<String, String, String> r: result.get()) {
			while(!r.getColumnSlice().equals(null)){
				for(HColumn<String, String> q: r.getColumnSlice().getColumns()){
					System.out.println(q.getName() + ":" +q.getValue());
				}
				System.out.println();
			}
		}
		searchresult.append("\n"+result);
		return searchresult.toString();
	}
	
	public void insert(String s1, String v1, String bukname) {
		// TODO Auto-generated method stub
		Mutator<String> m1 = HFactory.createMutator(keyspace, ss);
		String mkey = bukname;
		m1.addInsertion(mkey,
				"p226BooksData",
				HFactory.createStringColumn(s1, v1));
		m1.execute();
	}

	public void insert(String producedby, String contents, String name,
			String title, String translator, String releasedate,
			String posteddate, String language, String author) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Delete a key from cassandra
	 *//*
	public void delete(final String key) throws Exception {
		execute(new Command(){
			public Void execute(final Keyspace ks) throws Exception {
				ks.remove(key, createColumnPath(COLUMN_NAME));
				return null;
			}
		});
	}
	  */
}