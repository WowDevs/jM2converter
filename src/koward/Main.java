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
import org.apache.commons.cli.ParseException;

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

  private static HelpFormatter HELP_FORMATTER;
  private static CommandLine CMD;

  /**
   * Main method.
   *
   * @param args
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {
    init(args);

    validateArguments();

    convert();
  }

  private static void init(String[] args) {
    System.out.println(Constants.AUTHOR_MESSAGE);

    HELP_FORMATTER = new HelpFormatter();
    CommandLineParser CMD_PARSSER = new DefaultParser();

    try {
      CMD = CMD_PARSSER.parse(Constants.CMD_OPTIONS, args);
      System.out.println(Constants.INITIALISED);
    }
    catch (ParseException e) {
      System.err.println(Constants.ErrorMessages.ERR_INIT_FAILED + "\n");
      e.printStackTrace();
      HELP_FORMATTER.printHelp("\n" + Constants.HELP, Constants.CMD_OPTIONS);
    }
  }

  private static void validateArguments() {
    if (!CMD.hasOption(Constants.OptionsValue.IN) || !CMD.hasOption(Constants.OptionsValue.OUT)) {
      System.err.println(Constants.ErrorMessages.ERR_VALID_FAILED + "\n" + Constants.ErrorMessages.ERR_IN_OUT + "\n");
      printHelpAndExit();
    }

    if (!CMD.hasOption(Constants.OptionsValue.CL)) {
      System.err.println(Constants.ErrorMessages.ERR_VALID_FAILED + "\n" + Constants.ErrorMessages.ERR_NO_VERSION + "\n");
      printHelpAndExit();
    }

    if (CMD.hasOption(Constants.OptionsValue.F)) {
      if (!folderExists(CMD.getOptionValue(Constants.OptionsValue.IN))) {
        System.err.println(Constants.ErrorMessages.ERR_VALID_FAILED + "\n" + Constants.ErrorMessages.ERR_NO_FOLDER + CMD.getOptionValue(Constants.OptionsValue.F) + "\n");
        printHelpAndExit();
      }
    }

    if (!CMD.hasOption(Constants.OptionsValue.F)) {
      if (!CMD.getOptionValue(Constants.OptionsValue.IN).contains(".m2") || !CMD.getOptionValue(Constants.OptionsValue.OUT).contains(".m2")) {
        System.err.println(Constants.ErrorMessages.ERR_VALID_FAILED + "\n" + Constants.ErrorMessages.ERR_NO_M2_FILE + "\n");
        printHelpAndExit();
      }
    }

    System.out.println(Constants.VALIDATED);
  }

  private static void convert() throws Exception {
    System.out.println(Constants.CONVERTING + "\n");

    if (CMD.hasOption(Constants.OptionsValue.F)) {
      if (!folderExists(CMD.getOptionValue(Constants.OptionsValue.OUT))) {
        File outputFolder = new File(CMD.getOptionValue(Constants.OptionsValue.OUT));
        outputFolder.mkdir();
        System.out.println(Constants.OUT_FOLDER_CREATED + outputFolder.getAbsolutePath());
      }
      proccessFiles(getFileNamesFromFolder(CMD.getOptionValue(Constants.OptionsValue.IN)), true);
    }
    else {
      ArrayList<String> fileList = new ArrayList<>();
      fileList.add(CMD.getOptionValue(Constants.OptionsValue.IN));
      proccessFiles(fileList, false);
    }
  }

  private static void proccessFiles(ArrayList<String> fileList, boolean isFolder) throws Exception {
    for (String currentFile : fileList) {
      BlizzardInputStream in = new BlizzardInputStream(
        (isFolder) ?
          CMD.getOptionValue(Constants.OptionsValue.IN) + currentFile
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

      int newVersion = convertModel(m2Model);

      System.out.println("Conversion completed.");

      if (!isFolder) {
        File newFile = new File(CMD.getOptionValue(Constants.OptionsValue.OUT));
        if (!newFile.exists()) {
          File newDir = new File(newFile.getParent());
          if (!newDir.exists()) {
            newDir.mkdir();
          }
        }
      }

      BlizzardOutputStream out = (isFolder) ?
        new BlizzardOutputStream(CMD.getOptionValue(Constants.OptionsValue.OUT) + currentFile)
        :
        new BlizzardOutputStream(CMD.getOptionValue(Constants.OptionsValue.OUT));

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
          CMD.getOptionValue(Constants.OptionsValue.OUT) + currentFile + " written."
          :
          CMD.getOptionValue(Constants.OptionsValue.OUT) + " written.");

      out.close();
    }
  }

  private static int convertModel(M2 model) throws Exception {
    int oldVersion = model.getVersion();
    int newVersion = oldVersion;
    boolean converted = false;

    for (Entry<String, Integer> entry : Constants.M2_FORMATS.entrySet()) {
      String option = entry.getKey();
      Integer version = entry.getValue();
      if (CMD.hasOption(option)) {
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

  private static void printHelpAndExit() {
    HELP_FORMATTER.printHelp(Constants.HELP, Constants.CMD_OPTIONS);
    System.exit(1);
  }
}
