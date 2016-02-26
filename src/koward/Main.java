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
import org.apache.commons.cli.Options;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Java M2 Converter Main class.
 *
 * @author Koward
 */
public class Main {

  private static final String HELP = "(example with Frog.m2 to Classic for one file:)"
     + "\njava -jar jm2converter.jar -in Frog.m2 -out FrogConverted.m2 -cl\n"
     + "for a whole folder:"
     + "\njava -jar jm2converter.jar -in folder/path/ -out folder/outputpath/ -f -cl\n"
    + "the last '/' or '\\' is important! \n\n";
  private static final Options options;
  private static final Map<String, Integer> map;
  private static final String FILE_ENDING_M2 = ".m2";

  static {
    options = new Options();
    options.addOption("in", "input", true, "path to input file");
    options.addOption("out", "output", true, "path to output file");
    options.addOption("cl", "classic", false, "convert to Classic");
    options.addOption("bc", "burningcrusade", false, "convert to The Burning Crusade");
    options.addOption("lbc", "lateburningcrusade", false, "convert to The Burning Crusade (late versions), better for particles");
    options.addOption("lk", "lichking", false, "convert to Wrath of the Lich King");
    options.addOption("cata", "cataclysm", false, "convert to Cataclysm");
    options.addOption("mop", "pandaria", false, "convert to Mists of Pandaria");
    options.addOption("wod", "draenor", false, "convert to Warlords of Draenor");
    options.addOption("leg", "legion", false, "convert to Legion (Build 20810)");
    options.addOption("f", "folder", false, "path to input folder");

    map = new HashMap<>();
    map.put("classic", M2Format.CLASSIC);
    map.put("burningcrusade", M2Format.BURNING_CRUSADE);
    map.put("lateburningcrusade", M2Format.BURNING_CRUSADE + 3);
    map.put("lichking", M2Format.LICH_KING);
    map.put("cataclysm", M2Format.CATACLYSM);
    map.put("pandaria", M2Format.PANDARIA);
    map.put("draenor", M2Format.DRAENOR);
    map.put("legion", M2Format.LEGION);
  }

  /**
   * Main method.
   *
   * @param args
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {
    System.out.println("[[ Java M2 Converter by Koward v1.0.8b-beta (oppahansi's version)]]");
    HelpFormatter formatter = new HelpFormatter();
    CommandLineParser parser = new DefaultParser();
    CommandLine cmd = parser.parse(options, args);

    if (!cmd.hasOption("input") || !cmd.hasOption("output")) {
      System.err.println("Error : No input or/and output specified.");
      formatter.printHelp(HELP, options);
      System.exit(1);
    }

    if (cmd.hasOption("folder")) {
      if (!folderExists(cmd.getOptionValue("input"))) {
        System.err.println("Error : Folder does not exist: " + cmd.getOptionValue("folder"));
        formatter.printHelp(HELP, options);
        System.exit(1);
      }
      else {
        if (!folderExists(cmd.getOptionValue("output"))) {
          File outputFolder = new File(cmd.getOptionValue("output"));
          outputFolder.mkdir();
          System.out.println("Output folder created: " + outputFolder.getAbsolutePath());
        }
        convert(cmd, cmd.hasOption("folder"));
      }
    }
    else {
      convert(cmd, cmd.hasOption("folder"));
    }
  }

  private static void convert(CommandLine cmd, boolean isFolder) throws Exception {
    if (isFolder) {
      proccessFiles(cmd, getFileNamesFromFolder(cmd.getOptionValue("input")), isFolder);
    }
    else {
      ArrayList<String> fileList = new ArrayList<>();
      fileList.add(cmd.getOptionValue("input"));
      proccessFiles(cmd, fileList, isFolder);
    }

  }

  private static void proccessFiles(CommandLine cmd, ArrayList<String> fileList, boolean isFolder) throws Exception {
    for (String currentFile : fileList) {

      BlizzardInputStream in = new BlizzardInputStream((isFolder) ? cmd.getOptionValue("input") + currentFile : currentFile);
      Marshalable obj = (Marshalable) in.readObject();
      in.close();
      M2 model;

      if (obj instanceof M2) {
        model = ((M2) obj);
      }
      else if (obj instanceof MD21) {
        model = ((MD21) obj).getM2();
      }
      else {
        throw new Exception("Unknown structure");
      }
      System.out.println(currentFile + " read.");

      int newVersion = convertModel(model, cmd);

      System.out.println("Conversion completed.");

      if (!isFolder) {
        File newFile = new File(cmd.getOptionValue("output"));
        if (!newFile.exists()) {
          File newDir = new File(newFile.getParent());
          if (!newDir.exists()) {
            newDir.mkdir();
          }
        }
      }

      BlizzardOutputStream out = (isFolder) ? new BlizzardOutputStream(cmd.getOptionValue("output") + currentFile) : new BlizzardOutputStream(cmd.getOptionValue("output")) ;
      if (newVersion == M2Format.LEGION) {
        //Pack the MD20 inside MD21 chunked format
        MD21 pack = new MD21();
        pack.setM2(model);
        out.writeObject(pack);
      }
      else {
        out.writeObject(model);
      }
      System.out.println((isFolder) ? cmd.getOptionValue("output") + currentFile  + " written." : cmd.getOptionValue("output") + " written.");
      out.close();
    }
  }

  private static int convertModel(M2 model, CommandLine cmd) throws Exception {
    boolean converted = false;
    int oldVersion = model.getVersion();
    int newVersion = oldVersion;
    for (Entry<String, Integer> entry : map.entrySet()) {
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
        if (file.isFile() && file.getName().toLowerCase().contains(FILE_ENDING_M2)) {
          results.add(file.getName());
        }
      }
    }

    return results;
  }

  private static boolean folderExists(String pathToFolder) {
    File testDir = new File(pathToFolder);

    return testDir.exists();
  }
}
