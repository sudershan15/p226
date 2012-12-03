package drsy.indexing.inverted;


import java.io.File;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import drsy.indexing.stopwords.StopWords;
import drsy.indexing.stopwords.StopWordsFile;

public class StopWordsFileTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testIgnore() throws Exception {
		File swf = new File("resources/stopwords.txt");
		StopWords swords = new StopWordsFile(swf);
		Assert.assertTrue(swords.contains("the"));
		Assert.assertTrue(swords.contains("   "));
		Assert.assertFalse(swords.contains("dr pepper"));
	}

}
