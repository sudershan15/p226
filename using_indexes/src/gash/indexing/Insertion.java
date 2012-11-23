package gash.indexing;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.cassandra.thrift.NotFoundException;
import org.apache.thrift.TException;

import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.CassandraHostConfigurator;
import me.prettyprint.cassandra.service.ThriftKsDef;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;
import me.prettyprint.hector.api.ddl.ComparatorType;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;

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

	public static void main(String[] args) throws IOException, InvalidRequestException, FileNotFoundException, TException{
		Insertion sample = new Insertion();
		//System.out.println(cluster.describeClusterName());
		int replicationFactor = 10;

		/*Cluster myCluster = HFactory.getOrCreateCluster("test-cluster-226","localhost:9160");
		ColumnFamilyDefinition cfDef = HFactory.createColumnFamilyDefinition("Project226_Thrift",
                "p226Books",
                ComparatorType.BYTESTYPE);
		KeyspaceDefinition def = HFactory.createKeyspaceDefinition("Project226_Thrift",
                ThriftKsDef.DEF_STRATEGY_CLASS,
                replicationFactor,
                Arrays.asList(cfDef));;
		myCluster.addKeyspace(def );*/
		//System.out.println(cluster.describeKeyspace("Project226_Thrift")+ " " + keyspace.getKeyspaceName());
		//DataImportExample sample = new DataImportExample();
		//TableModel t = CSVParser.parse(new File("C:/Users/Sudershan/Desktop/csvoneclmn.csv"));

		// Print all the columns of the table, followed by a new line.
		/*for (int x = 0; x < t.getColumnCount(); x++) {
System.out.println(t.getColumnName(x) + " ");
}*/
		//System.out.println();
		//int v=t.getRowCount();
		//int u=t.getColumnCount();
		//System.out.println(u+ " "+v);
		//int y=0;
		// Print all the data from the table.
		//for (int x = 0; x < v+1; x++) {
		//for (int y = 0; y < t.getColumnCount(); y++)
		//while (y!=u)
		{
			//System.out.print(t.getValueAt(x,y ) + " "+t.getValueAt(x, y+1)+" "+t.getValueAt(x, y+2)+" "+t.getValueAt(x, y+3)+"\n");
			//Tweet tweet = new Tweet(UUID.randomUUID(),"","", "","");
			//sample.insert1();
			//y=u;
		}
		String key = "Name";
		// COLUMN_NAME;
		//String COLUMN_NAME = "";
		//System.out.println(string(keyspace.getColumn(key, createColumnPath(COLUMN_NAME)).getValue()));
		// y=0;
		//}



	}


}
/*
 *//**
 * Get a string value.
 * @return The string value; null if no value exists for the given key.
 *//*
	public String get(final String key) throws Exception {
		return execute(new Command(){
			public String execute(final Keyspace ks) throws Exception {
				try {
					return string(ks.getColumn(key, createColumnPath(COLUMN_NAME)).getValue());
				} catch (NotFoundException e) {
					return null;
				}
			}
		});
	}

  *//**
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