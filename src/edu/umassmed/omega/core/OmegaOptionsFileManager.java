/*******************************************************************************
 * Copyright (C) 2014 University of Massachusetts Medical School
 * Alessandro Rigano (Program in Molecular Medicine)
 * Caterina Strambio De Castillia (Program in Molecular Medicine)
 *
 * Created by the Open Microscopy Environment inteGrated Analysis (OMEGA) team: 
 * Alex Rigano, Caterina Strambio De Castillia, Jasmine Clark, Vanni Galli, 
 * Raffaello Giulietti, Loris Grossi, Eric Hunter, Tiziano Leidi, Jeremy Luban, 
 * Ivo Sbalzarini and Mario Valle.
 *
 * Key contacts:
 * Caterina Strambio De Castillia: caterina.strambio@umassmed.edu
 * Alex Rigano: alex.rigano@umassmed.edu
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package edu.umassmed.omega.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OmegaOptionsFileManager {
	private static String WORKING_DIR_FILE_IDENT = "Omega current working directory";
	private static String CONFIG_FILE_IDENT = "Omega configuration file";

	private static String FILE_DATE_IDENT = "Last updated";

	private static String CAT_IDENT = "CATEGORY";

	private static String WORKING_DIR_FILENAME = ".omegaWorkingDir";
	private static String CONFIG_FILENAME = ".omegaConfig";

	private final File workingDirFile;
	private File configFile;

	private String workingDirPath;

	private final Map<String, List<String>> optionsCat;
	private final Map<String, String> options;

	public OmegaOptionsFileManager() {
		this.optionsCat = new LinkedHashMap<String, List<String>>();
		this.options = new LinkedHashMap<String, String>();

		this.workingDirFile = new File(
		        OmegaOptionsFileManager.WORKING_DIR_FILENAME);
		this.workingDirPath = null;
		this.configFile = null;

		final int loadWorkingDirError = this.loadWorkingDirPathFromFile();

		if (loadWorkingDirError != 0) {
			this.workingDirPath = System.getProperty("user.dir");
		}

		this.configFile = new File(this.workingDirPath + File.separator
		        + OmegaOptionsFileManager.CONFIG_FILENAME);

		this.loadOptionsFromFile();
	}

	protected int loadWorkingDirPathFromFile() {
		try {
			final FileReader fr = new FileReader(this.workingDirFile);
			final BufferedReader br = new BufferedReader(fr);
			final String workingDirFileIdent = br.readLine();
			if (!workingDirFileIdent
			        .equals(OmegaOptionsFileManager.WORKING_DIR_FILE_IDENT)) {
				// TODO ERROR
				br.close();
				fr.close();
				return -1;
			}

			final String dateString = br.readLine().replace(
			        OmegaOptionsFileManager.FILE_DATE_IDENT, "");
			final String date = dateString.substring(1);
			final DateFormat format = DateFormat.getInstance();
			final Date d = format.parse(date);
			System.out.println(format.format(d));

			this.workingDirPath = br.readLine();
			br.close();
			fr.close();
			return 1;
		} catch (final FileNotFoundException e) {
			return -1;
		} catch (final IOException e) {
			return -1;
		} catch (final ParseException e) {
			return -1;
		}
	}

	protected int saveWorkingDirPathToFile() {
		try {
			final FileWriter fw = new FileWriter(this.configFile);
			final BufferedWriter bw = new BufferedWriter(fw);

			bw.write(OmegaOptionsFileManager.WORKING_DIR_FILE_IDENT + "\n");
			final DateFormat format = DateFormat.getInstance();
			bw.write(OmegaOptionsFileManager.FILE_DATE_IDENT + "\t"
			        + format.format(Calendar.getInstance().getTime()) + "\n");

			bw.write(this.workingDirPath);

			bw.close();
			fw.close();
			return 1;
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return -1;
	}

	protected int loadOptionsFromFile() {
		try {
			final FileReader fr = new FileReader(this.configFile);
			final BufferedReader br = new BufferedReader(fr);
			final String configFileIdent = br.readLine();
			if ((configFileIdent == null)
			        || !configFileIdent
			                .equals(OmegaOptionsFileManager.CONFIG_FILE_IDENT)) {
				// TODO ERROR
				br.close();
				fr.close();
				return -1;
			}

			final String dateString = br.readLine().replace(
			        OmegaOptionsFileManager.FILE_DATE_IDENT, "");
			final String date = dateString.substring(1);
			final DateFormat format = DateFormat.getInstance();
			final Date d = format.parse(date);
			System.out.println(format.format(d));

			String line = br.readLine();
			String actualCategory = "CATEGORY GENERAL";
			while (line != null) {
				if (line.contains(OmegaOptionsFileManager.CAT_IDENT)) {
					actualCategory = line;
				} else if (line.isEmpty()) {
					line = br.readLine();
					continue;
				} else {
					final int divisor = line.indexOf("\t");
					final String optionName = line.substring(0, divisor);
					final String values = line.substring(divisor + 1);
					this.options.put(optionName, values);
					List<String> optionsList;
					if (this.optionsCat.containsKey(actualCategory)) {
						optionsList = this.optionsCat.get(actualCategory);
					} else {
						optionsList = new ArrayList<String>();
					}
					optionsList.add(optionName);
					this.optionsCat.put(actualCategory, optionsList);
				}
				line = br.readLine();
			}

			br.close();
			fr.close();
			return 1;
		} catch (final FileNotFoundException e) {
			return -1;
		} catch (final IOException e) {
			return -1;
		} catch (final ParseException e) {
			return -1;
		}
	}

	protected void saveOptionsToFile() {
		try {
			final FileWriter fw = new FileWriter(this.configFile);
			final BufferedWriter bw = new BufferedWriter(fw);

			bw.write(OmegaOptionsFileManager.CONFIG_FILE_IDENT + "\n");
			final DateFormat format = DateFormat.getInstance();
			bw.write(OmegaOptionsFileManager.FILE_DATE_IDENT + "\t"
			        + format.format(Calendar.getInstance().getTime()) + "\n");

			bw.write("\n");
			for (final String optionCat : this.optionsCat.keySet()) {
				bw.write(optionCat + "\n");
				final List<String> optionsList = this.optionsCat.get(optionCat);
				for (final String optionsName : optionsList) {
					bw.write(optionsName + "\t" + this.options.get(optionsName)
					        + "\n");
				}
				bw.write("\n");
			}

			bw.close();
			fw.close();
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public Map<String, Map<String, String>> getGeneralOptions() {
		final Map<String, Map<String, String>> generalOptions = new LinkedHashMap<String, Map<String, String>>();
		for (final String category : this.optionsCat.keySet()) {
			if (!category.contains("GENERAL")) {
				continue;
			}
			final Map<String, String> options = new LinkedHashMap<String, String>();
			for (final String option : this.optionsCat.get(category)) {
				options.put(option, this.options.get(option));
			}
			generalOptions.put(category, options);
		}
		return generalOptions;
	}

	public Map<String, String> getOptions(final String category) {
		final Map<String, String> options = new LinkedHashMap<String, String>();
		if (!this.optionsCat.keySet().contains(category))
			return options;
		else {
			for (final String option : this.optionsCat.get(category)) {
				options.put(option, this.options.get(option));
			}
		}
		return options;
	}

	public void addOptions(final String category,
	        final Map<String, String> newOptions) {
		for (final String option : newOptions.keySet()) {
			List<String> optionsList;
			if (this.optionsCat.containsKey(category)) {
				optionsList = this.optionsCat.get(category);
			} else {
				optionsList = new ArrayList<String>();
			}

			if (!optionsList.contains(option)) {
				optionsList.add(option);
			}
			this.optionsCat.put(category, optionsList);
			this.options.put(option, newOptions.get(option));
		}
	}

	public void changeWorkingDir(final String path) {
		this.workingDirPath = path;
	}
}
