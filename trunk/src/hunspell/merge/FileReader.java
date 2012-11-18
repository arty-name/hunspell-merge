// $Id$

package hunspell.merge;

import java.io.*;
import java.nio.charset.Charset;

public abstract class FileReader {

  private boolean abort = false;

  public void readFile(File in, Charset cs) {
    // Decode file and process each line
    try {
      FileInputStream inputStream = new FileInputStream(in);
      InputStreamReader inputReader;
      if (cs == null) {
        inputReader = new InputStreamReader(inputStream);
      } else {
        inputReader = new InputStreamReader(inputStream, cs);
      }

      BufferedReader bufferedReader = new BufferedReader(inputReader);
      start();
      String str;

      try {
        while ((str = bufferedReader.readLine()) != null) {
          readLine(str);
          if (abort) {
            break;
          }
        }
        bufferedReader.close();
      } catch (IOException ignored) {
      }
    } catch (FileNotFoundException ignored) {
    }

    end();
  }

  abstract protected void readLine(String str);

  protected void start() {
    setAbort(false);
  }

  protected void end() {

  }

  public void setAbort(boolean abort) {
    this.abort = abort;
  }
}
