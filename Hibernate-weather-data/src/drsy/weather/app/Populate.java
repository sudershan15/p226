package drsy.weather.app;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.hibernate.Session;
import org.hibernate.Transaction;

import drsy.weather.data.Provider;
import drsy.weather.data.Station;
import drsy.weather.data.StationProvider;
import drsy.weather.data.StationState;
import drsy.weather.data.Wdata;
import drsy.weather.util.Unzip;


public class Populate extends testQuery {

	static ArrayList<String> paths = new ArrayList<String>();
	private ArrayList<String> files = new ArrayList<String>();
	public static void main(String[] args) throws IOException {
		System.out.println("Loading data to database");
		Populate p = new Populate();
		Unzip z = new Unzip();

		String path = "C:/Users/smalpani/Desktop/Sep25.zip";
		String[] path1 = path.split("/");
		String destinationPath = "";
		String resultantFolder = "";
		String resultFolder = "";
		for(String loc : path1) {
			if (!loc.contains(".zip") && loc != null) {
				destinationPath += loc;
				destinationPath += "/";
			} else if (loc.endsWith(".zip")) {
				System.out.println(loc  + "   " +loc.split(".") + "    ");
				int i = 0;
				while (loc.charAt(i) != '.' && i != loc.length()){
					resultantFolder += loc.charAt(i);
					++i;
				}
			}

		}
		resultFolder = destinationPath + resultantFolder;
		z.unzipFolder(path );
		/*while (entries.hasMoreElements()) {
			String nameFile = entries.nextElement().toString();
			String filePath = destinationPath + nameFile;
			if (filePath != resultFolder && !filePath.contains(".DS_Store")){
				paths.add(filePath);
				System.out.println(filePath);
			}


		}
		 */		
		p.bulkGenerate(resultFolder);
	}





	public void bulkGenerate(String path) throws IOException {
		
		final File folder = new File(path);
		listFilesForFolder(folder);
		for (String f: files) {
			if(f.contains("mesowest.out") ) {
				String[] parmOrder = null;
				FileInputStream fsStream = new FileInputStream(f);
				DataInputStream diStream = new DataInputStream(fsStream);
				BufferedReader biStream = new BufferedReader(new InputStreamReader(diStream));
				String strLine;
				while((strLine = biStream.readLine()) != null)
				{
					if (strLine.trim().startsWith("PARM"))
					{
						String[] master = strLine.trim().split("=");
						parmOrder = master[1].trim().split(";");
					}
					else if (strLine.trim().isEmpty())
					{
						System.out.println("spaces");
					}
					else if (strLine.trim().startsWith("STN "))
					{
						System.out.println("Ignore");
					}
					else
					{
						if (parmOrder == null)
						{
							String master = "MNET;SLAT;SLON;SELV;TMPF;SKNT;DRCT;GUST;PMSL;ALTI;DWPF;RELH;WTHR;P24I";
							parmOrder = master.split(";");
						}
						strLine = strLine.replaceAll("\\s+", " ");
						final StringTokenizer token = new StringTokenizer(strLine, " ");
						final List<String> col = new ArrayList<String>();
						Wdata s = new Wdata();
						s.setStation(token.nextToken().trim());
						String d = token.nextToken();
						String[] p11 = d.split("/");
						System.out.println("   " + p11[0] + "   "  + p11[1]);
						s.setDate(Integer.parseInt(p11[0]));
						s.setTime(Integer.parseInt(p11[1]));
						String mnet = token.nextToken();
						String lat = token.nextToken();
						String lon = token.nextToken();
						String elevation = token.nextToken();
						s.setTmpf(Float.parseFloat(token.nextToken().trim()));
						s.setSknt(Float.parseFloat(token.nextToken().trim()));
						s.setDrct(Float.parseFloat(token.nextToken().trim()));
						s.setGust(Float.parseFloat(token.nextToken().trim()));
						s.setPmsl(Float.parseFloat(token.nextToken().trim()));
						s.setAlti(Float.parseFloat(token.nextToken().trim()));
						s.setDwpf(Float.parseFloat(token.nextToken().trim()));
						s.setRelh(Float.parseFloat(token.nextToken().trim()));
						s.setWthr(Float.parseFloat(token.nextToken().trim()));
						s.setP24i(Float.parseFloat(token.nextToken().trim()));

						System.out.println("-------------------------------");
					}
				}

				biStream.close();
			}
			else if (f.contains("mesowest.tbl")) {
				System.out.println("Data Redundant not needed already contained in the mesowest_csv");
			} else if (f.contains("_csv.csv")) {
				
				Station s = new Station();
				Session session = locator.locate(s.getName());
				Transaction tx = session.beginTransaction();
				File file = new File(f);
				BufferedReader bufRdr  = new BufferedReader(new FileReader(file));
				String line = null;
				int row = 0;
				int col = 0;

				//read each line of text file
				while((line = bufRdr.readLine()) != null)
				{
					if(line.trim().startsWith("primaryid")) {
						System.out.println("IGNORE");
					} else if (line.trim().isEmpty()) {
						System.out.println("IGNORE EMPTY");
					} else {
						StringTokenizer st = new StringTokenizer(line,",");
						String stname = st.nextToken().trim();
						s.setId(stname );
						String secid = st.nextToken().trim();
						if(secid != null || secid == "999999") 
							s.setSec_id(secid );
						else
							s.setSec_id("WASNULL");
						s.setName(st.nextToken().trim());
						StationState sk = new StationState();
						sk.setState(st.nextToken().trim());
						sk.setCountry(st.nextToken().trim());
						s.setSskey(sk);
						String lat = st.nextToken().trim();
						if(lat != null || !lat.isEmpty())
							s.setLatitude(Float.parseFloat(lat));

						String lon = st.nextToken().trim();
						if(lon != null || !lon.isEmpty())
							s.setLongitude(Float.parseFloat(lon));
						s.setElevation(Float.parseFloat(st.nextToken().trim()));
						s.setNetwork(Integer.parseInt(st.nextToken().trim()));
						String networkname = st.nextToken().trim();
						s.setStatus(st.nextToken().trim());
						Provider p1 = new Provider();
						StationProvider spk = new StationProvider();
						String pprovider = st.nextToken().trim();
						String pprovidername = st.nextToken();
						if (!pprovider.isEmpty()) {
							spk.setStation(stname);
							spk.setProvider(Integer.parseInt(pprovider));
							spk.setType(1);
							p1.setId(Integer.parseInt(pprovider));
							p1.setName(pprovidername);
						}
						String sprovider = st.nextToken().trim();
						String sprovidername = st.nextToken().trim();
						if (!sprovider.isEmpty()) {
							spk.setStation(stname);
							spk.setProvider(Integer.parseInt(sprovider));
							spk.setType(2);
							p1.setId(Integer.parseInt(sprovider));
							p1.setName(sprovidername);
						}
						String tprovider = st.nextToken().trim();
						String tprovidername = st.nextToken().trim();
						if (!tprovider.isEmpty()) {
							spk.setStation(stname);
							spk.setProvider(Integer.parseInt(tprovider));
							spk.setType(3);
							p1.setId(Integer.parseInt(tprovider));
							p1.setName(tprovidername);
						}

						System.out.println(st.nextToken().trim());
					}

					//close the file
					bufRdr.close();
				}
				session.save(s);
				session.flush();
				session.clear();


				tx.commit();
			}

			/*Scanner scanner = 
				new Scanner(new File(f)).useDelimiter("\\Z");
		String contents = scanner.next();

		System.out.println(contents);
		scanner.close();*/
		}
		
	}

	public void listFilesForFolder(final File folder) {
		System.out.println(folder);
		for (final File fileEntry : folder.listFiles()) {
			if(!fileEntry.getName().contains(".DS_Store")){
				if (fileEntry.isDirectory()) {
					listFilesForFolder(fileEntry);
				} else {
					System.out.println(fileEntry.getName());
					files.add(folder + "/" + fileEntry.getName());
				}
			}
		}
	}


	public Populate() {
		super();

		if (locator == null)
			throw new RuntimeException("Missing locator");
	}
}
