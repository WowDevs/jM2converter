package koward;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import jm2lib.blizzard.files.M2;
import jm2lib.blizzard.files.M2Format;
import jm2lib.blizzard.files.MD21;
import jm2lib.blizzard.io.BlizzardFile;
import jm2lib.blizzard.io.BlizzardInputStream;
import jm2lib.blizzard.io.BlizzardOutputStream;

/**
 * Java M2 Converter Main class.
 * 
 * @author Koward
 *
 */
public class Main {
	private static final String HELP = "java -jar jm2converter [OPTIONS]\n";

	/**
	 * Main method.
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		System.out.println("[[ Java M2 Converter by Koward v0.9.1-alpha ]]");
		HelpFormatter formatter = new HelpFormatter();
		Options options = new Options();
		options.addOption("in", "input", true, "path to input file");
		options.addOption("out", "output", true, "path to output file");
		options.addOption("cl", "classic", false, "convert to Classic");
		options.addOption("bc", "burningcrusade", false, "convert to The Burning Crusade");
		options.addOption("lbc", "lateburningcrusade", false, "convert to The Burning Crusade (late versions)");
		options.addOption("lk", "lichking", false, "convert to Wrath of the Lich King");
		options.addOption("cata", "cataclysm", false, "convert to Cataclysm");
		options.addOption("mop", "pandaria", false, "convert to Mists of Pandaria");
		options.addOption("wod", "draenor", false, "convert to Warlords of Draenor");
		options.addOption("leg", "legion", false, "convert to Legion (Beta)");
		options.addOption("about", false, "show credits");
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = parser.parse(options, args);

		if (!cmd.hasOption("input") || !cmd.hasOption("output")) {
			System.err.println("Error : No input or/and output specified.");
			formatter.printHelp(HELP, options);
			System.exit(1);
		}

		BlizzardInputStream in = new BlizzardInputStream(cmd.getOptionValue("input"));
		BlizzardFile obj = (BlizzardFile) in.readObject();
		in.close();
		M2 model;
		if (obj instanceof M2) {
			model = ((M2) obj);
		} else if (obj instanceof MD21) {
			model = ((MD21) obj).getM2();
		} else
			throw new Exception("Unknown structure");
		System.out.println("Model read.");

		int newVersion = convertModel(model, cmd);
		System.out.println("Model converted.");

		BlizzardOutputStream out = new BlizzardOutputStream(cmd.getOptionValue("output"));
		if (newVersion == M2Format.LEGION) {
			//Pack the MD20 in MD21 chunk
			MD21 pack = new MD21();
			pack.setM2(model);
			out.writeObject(pack);
		} else {
			out.writeObject(model);
		}
		System.out.println("Model written.");
		out.close();
	}

	private static int convertModel(M2 model, CommandLine cmd) throws Exception {
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("classic", M2Format.CLASSIC);
		map.put("burningcrusade", M2Format.BURNING_CRUSADE);
		map.put("lateburningcrusade", M2Format.BURNING_CRUSADE + 3);
		map.put("lichking", M2Format.LICH_KING);
		map.put("cataclysm", M2Format.CATACLYSM);
		map.put("pandaria", M2Format.PANDARIA);
		map.put("draenor", M2Format.DRAENOR);
		map.put("legion", M2Format.LEGION);
		boolean converted = false;
		int newVersion = 0;
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
		return newVersion;
	}
}
