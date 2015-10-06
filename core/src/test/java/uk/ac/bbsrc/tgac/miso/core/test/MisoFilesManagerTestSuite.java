package uk.ac.bbsrc.tgac.miso.core.test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.junit.Before;
import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.manager.MisoFilesManager;

public class MisoFilesManagerTestSuite {

   MisoFilesManager filesManager;
   @Before
   public void setUp() throws Exception {
      filesManager = new MisoFilesManager();
   }

   @Test(expected=java.io.IOException.class)
   public final void testDeleteFile() throws IOException {
      final String fileName = "test_file.txt";
      final String fileContent = "the content of my file.";
      final PrintWriter writer = new PrintWriter(fileName);
      writer.write(fileContent);
      writer.close();

      final File file = new File(fileName);
      filesManager.storeFile(Run.class, "stats", file);

      assertTrue(file.exists());
      filesManager.deleteFile(Run.class, "stats", fileName);

      filesManager.getFile(Run.class, "stats", fileName);
   }
}


