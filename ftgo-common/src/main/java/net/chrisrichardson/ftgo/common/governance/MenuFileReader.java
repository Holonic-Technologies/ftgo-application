package net.chrisrichardson.ftgo.common.governance;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MenuFileReader {

  public String firstMenuItem(Path menuPath) throws IOException {
    BufferedReader reader = Files.newBufferedReader(menuPath);
    return reader.readLine();
  }
}
