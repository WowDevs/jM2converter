package koward;

import jm2lib.blizzard.wow.M2Format;
import org.apache.commons.cli.Options;

import java.util.HashMap;
import java.util.Map;

public class Constants {

  public static final String AUTHOR_MESSAGE = "[[ Java M2 Converter by Koward v1.0.8b-beta (oppahansi's version) ]]";

  public static final String HELP = "(Example with Frog.m2 to Classic for one file:)"
     + "\njava -jar jm2converter.jar -in Frog.m2 -out FrogConverted.m2 -cl\n"
     + "\n(Example for a whole folder:)\n"
     + "\njava -jar jm2converter.jar -in folder/path/ -out folder/outputpath/ -f -cl\n"
     + "The last '/' or '\\' is important! \n\n";

  public static final String FILE_ENDING_M2 = ".m2";

  public static class OptionsCMD {
    public static final String IN = "in";
    public static final String OUT = "out";
    public static final String CL = "cl";
    public static final String BC = "bc";
    public static final String LBC = "lbc";
    public static final String LK = "lk";
    public static final String CATA = "cata";
    public static final String MOP = "mop";
    public static final String WOD = "wod";
    public static final String LEG = "leg";
    public static final String F = "f";

  }

  public static class OptionsValue {
    public static final String IN = "input";
    public static final String OUT = "output";
    public static final String CL = "classic";
    public static final String BC = "burningcrusade";
    public static final String LBC = "lateburningcrusade";
    public static final String LK = "lichking";
    public static final String CATA = "cataclysm";
    public static final String MOP = "pandaria";
    public static final String WOD = "draenor";
    public static final String LEG = "legion";
    public static final String F = "folder";

  }

  public static final Options CMD_OPTIONS;
  public static final Map<String, Integer> M2_FORMATS;

  static {
    CMD_OPTIONS = new Options();
    CMD_OPTIONS.addOption(OptionsCMD.IN, OptionsValue.IN, true, "path to input file");
    CMD_OPTIONS.addOption(OptionsCMD.OUT, OptionsValue.OUT, true, "path to output file");
    CMD_OPTIONS.addOption(OptionsCMD.CL, OptionsValue.CL, false, "convert to Classic");
    CMD_OPTIONS.addOption(OptionsCMD.BC, OptionsValue.BC, false, "convert to The Burning Crusade");
    CMD_OPTIONS
       .addOption(OptionsCMD.LBC, OptionsValue.LBC, false,
          "convert to The Burning Crusade (late versions), better for " + "particles");
    CMD_OPTIONS.addOption(OptionsCMD.LK, OptionsValue.LK, false, "convert to Wrath of the Lich King");
    CMD_OPTIONS.addOption(OptionsCMD.CATA, OptionsValue.CATA, false, "convert to Cataclysm");
    CMD_OPTIONS.addOption(OptionsCMD.MOP, OptionsValue.MOP, false, "convert to Mists of Pandaria");
    CMD_OPTIONS.addOption(OptionsCMD.WOD, OptionsValue.WOD, false, "convert to Warlords of Draenor");
    CMD_OPTIONS.addOption(OptionsCMD.LEG, OptionsValue.LEG, false, "convert to Legion (Build 20810)");
    CMD_OPTIONS.addOption(OptionsCMD.F, OptionsValue.F, false, "path to input folder");

    M2_FORMATS = new HashMap<>();
    M2_FORMATS.put(OptionsValue.CL, M2Format.CLASSIC);
    M2_FORMATS.put(OptionsValue.BC, M2Format.BURNING_CRUSADE);
    M2_FORMATS.put(OptionsValue.LBC, M2Format.BURNING_CRUSADE + 3);
    M2_FORMATS.put(OptionsValue.LK, M2Format.LICH_KING);
    M2_FORMATS.put(OptionsValue.CATA, M2Format.CATACLYSM);
    M2_FORMATS.put(OptionsValue.MOP, M2Format.PANDARIA);
    M2_FORMATS.put(OptionsValue.WOD, M2Format.DRAENOR);
    M2_FORMATS.put(OptionsValue.LEG, M2Format.LEGION);
  }

  public static class ErrorMessages {
    public static final String ERR_IN_OUT = "Error : No input or/and output specified.";
    public static final String ERR_NO_FOLDER = "Error : Folder does not exist: ";
  }


}
