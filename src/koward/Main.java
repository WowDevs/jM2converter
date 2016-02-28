package koward;

import jm2lib.blizzard.io.BlizzardInputStream;
import jm2lib.blizzard.io.BlizzardOutputStream;
import jm2lib.blizzard.wow.M2;
import jm2lib.blizzard.wow.M2Format;
import jm2lib.blizzard.wow.MD21;
import jm2lib.io.Marshalable;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;

import java.io.File;
import java.util.ArrayList;
import java.util.Map.Entry;

/**
 * Java M2 Converter Main class.
 *
 * @author Koward
 *         <p>
 *         modified by Oppahansi
 */
public class Main {

  /**
   * Main method.
   *
   * @param args
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {
    System.out.println(Constants.AUTHOR_MESSAGE);

    HelpFormatter formatter = new HelpFormatter();
    CommandLineParser cmdParser = new DefaultParser();
    CommandLine cmd = cmdParser.parse(Constants.CMD_OPTIONS, args);

    if (!cmd.hasOption(Constants.OptionsValue.IN) || !cmd.hasOption(Constants.OptionsValue.OUT)) {
      System.err.println(Constants.ErrorMessages.ERR_IN_OUT);
      formatter.printHelp(Constants.HELP, Constants.CMD_OPTIONS);
      System.exit(1);
    }

    if (cmd.hasOption(Constants.OptionsValue.F)) {
      if (!folderExists(cmd.getOptionValue(Constants.OptionsValue.IN))) {
        System.err.println(Constants.ErrorMessages.ERR_NO_FOLDER + cmd.getOptionValue(Constants.OptionsValue.F));
        formatter.printHelp(Constants.HELP, Constants.CMD_OPTIONS);
        System.exit(1);
      }
      else {
        if (!folderExists(cmd.getOptionValue(Constants.OptionsValue.OUT))) {
          File outputFolder = new File(cmd.getOptionValue(Constants.OptionsValue.OUT));
          outputFolder.mkdir();
          System.out.println("Output folder created: " + outputFolder.getAbsolutePath());
        }
        convert(cmd, cmd.hasOption(Constants.OptionsValue.F));
      }
    }
    else {
      convert(cmd, cmd.hasOption(Constants.OptionsValue.F));
    }
  }

  private static void convert(CommandLine cmd, boolean isFolder) throws Exception {
    if (isFolder) {
      proccessFiles(cmd, getFileNamesFromFolder(cmd.getOptionValue(Constants.OptionsValue.IN)), true);
    }
    else {
      ArrayList<String> fileList = new ArrayList<>();
      fileList.add(cmd.getOptionValue(Constants.OptionsValue.IN));
      proccessFiles(cmd, fileList, false);
    }

  }

  private static void proccessFiles(CommandLine cmd, ArrayList<String> fileList, boolean isFolder) throws Exception {
    for (String currentFile : fileList) {

      BlizzardInputStream in = new BlizzardInputStream(
         (isFolder) ?
            cmd.getOptionValue(Constants.OptionsValue.IN) + currentFile
            :
            currentFile);

      Marshalable marshalableObject = (Marshalable) in.readObject();
      in.close();
      M2 m2Model;

      if (marshalableObject instanceof M2) {
        m2Model = ((M2) marshalableObject);
      }
      else if (marshalableObject instanceof MD21) {
        m2Model = ((MD21) marshalableObject).getM2();
      }
      else {
        throw new Exception("Unknown structure");
      }

      System.out.println(currentFile + " read.");

      int newVersion = convertModel(m2Model, cmd);

      System.out.println("Conversion completed.");

      if (!isFolder) {
        File newFile = new File(cmd.getOptionValue(Constants.OptionsValue.OUT));
        if (!newFile.exists()) {
          File newDir = new File(newFile.getParent());
          if (!newDir.exists()) {
            newDir.mkdir();
          }
        }
      }

      BlizzardOutputStream out = (isFolder) ?
         new BlizzardOutputStream(cmd.getOptionValue(Constants.OptionsValue.OUT) + currentFile)
         :
         new BlizzardOutputStream(cmd.getOptionValue(Constants.OptionsValue.OUT));

      if (newVersion == M2Format.LEGION) {
        //Pack the MD20 inside MD21 chunked format
        MD21 pack = new MD21();
        pack.setM2(m2Model);
        out.writeObject(pack);
      }
      else {
        out.writeObject(m2Model);
      }
      System.out.println(
         (isFolder) ?
            cmd.getOptionValue(Constants.OptionsValue.OUT) + currentFile + " written."
            :
            cmd.getOptionValue(Constants.OptionsValue.OUT) + " written.");
      out.close();
    }
  }

  private static int convertModel(M2 model, CommandLine cmd) throws Exception {
    boolean converted = false;
    int oldVersion = model.getVersion();
    int newVersion = oldVersion;
    for (Entry<String, Integer> entry : Constants.M2_FORMATS.entrySet()) {
      String option = entry.getKey();
      Integer version = entry.getValue();
      if (cmd.hasOption(option)) {
        model.convert(version);
        converted = true;
        newVersion = version;
      }
    }
    if (!converted) {
      System.err.println("Warning : no version specified. The model has not been converted.");
    }
    else if (oldVersion == newVersion) {
      System.err.println("Warning : original version and new version are identical.");
    }
    return newVersion;
  }

  private static ArrayList<String> getFileNamesFromFolder(String pathToFolder) {
    ArrayList<String> results = new ArrayList<>();

    File[] files = new File(pathToFolder).listFiles();

    if (files != null) {
      for (File file : files) {
        if (file.isFile() && file.getName().toLowerCase().contains(Constants.FILE_ENDING_M2)) {
          results.add(file.getName());
        }
      }
    }

    return results;
  }

  private static boolean folderExists(String pathToFolder) {
    return new File(pathToFolder).exists();
  }
}
