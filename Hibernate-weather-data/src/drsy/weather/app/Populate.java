package drsy.weather.app;


public class Populate extends testQuery {

	public static void main(String[] args) {
		System.out.println("Loading data to pplann");
		Populate p = new Populate();
		//p.bulkGenerate(5);
	}

	public Populate() {
		super();

		if (locator == null)
			throw new RuntimeException("Missing locator");
	}

	/*public void bulkGenerate(int count) {
		getppl gen = new getppl();
		for (int n = 0; n < count; n++) {
			Station p = gen.createStation();
			System.out.println("--> creating: " + p);
			Session session = locator.locate(p.getLastName());
			try {
				Transaction tx = session.beginTransaction();
				session.save(p);
				if (n % 100 == 0) {
					session.flush();
					session.clear();
				}

				tx.commit();
			} finally {
				// session.close();
			}
		}
	}*/
}
