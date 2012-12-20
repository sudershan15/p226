package gash.proto.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LockTest {
	File t = new File("/tmp/HiImAFile");

	@Before
	public void createTestFile() throws Exception {
		FileOutputStream fos = null;
		fos = new FileOutputStream(t);
		fos.write("hello".getBytes());
		fos.flush();
		fos.close();
	}

	@After
	public void removeTestFile() throws Exception {
		if (t.exists())
			t.delete();
	}

	@Test
	public void testLockOnExistingFile() throws Exception {
		FileLock fileLock = null;
		try {
			FileChannel channel = new RandomAccessFile(t, "rw").getChannel();

			fileLock = channel.tryLock();
			Assert.assertNotNull("The file should not be locked!", fileLock);

			getAnotherLock();
		} catch (Exception ex) {
		} finally {
			if (fileLock != null) {
				fileLock.release();
			}
		}
	}

	@Test
	public void testLockOnNewFile() throws Exception {
		
		File t2 = new File("/tmp/alsdfkjaslfajsflskj");
		FileLock fileLock = null;
		try {
			FileChannel channel = new RandomAccessFile(t2, "rw").getChannel();

			fileLock = channel.tryLock();
			Assert.assertNotNull("The file should not be locked!", fileLock);

			getAnotherLock();
		} catch (Exception ex) {
		} finally {
			if (fileLock != null) {
				fileLock.release();
			}
		}
	}

	/**
	 * 
	 * @throws Exception
	 */
	private void getAnotherLock() throws Exception {
		FileLock fileLock = null;
		try {
			FileChannel channel = new RandomAccessFile(t, "rw").getChannel();

			fileLock = channel.tryLock();
			Assert.assertNotNull("The file should be locked!", fileLock);
		} catch (Exception ex) {
			// good!
		} finally {
			if (fileLock != null) {
				fileLock.release();
			}
		}
	}
}
