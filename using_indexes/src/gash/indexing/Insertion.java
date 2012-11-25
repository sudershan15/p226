package gash.indexing;

import java.util.Iterator;

import me.prettyprint.cassandra.model.IndexedSlicesQuery;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.CassandraHostConfigurator;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.OrderedRows;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.QueryResult;

public class Insertion {

	public Insertion() {
		// TODO Auto-generated constructor stub
	}

	final static Cluster cluster = HFactory.createCluster("TestCluster", new CassandraHostConfigurator("localhost:9160"));
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