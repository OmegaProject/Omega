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
package edu.umassmed.omega.sptPlugin.tools;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;

import javax.swing.JOptionPane;

import com.galliva.gallibrary.GLogManager;

import edu.umassmed.omega.commons.constants.OmegaConstants;

public class SPTInformationFileLoader extends SPTInformationLoader {

	private String fileName = "";
	private FileInputStream fstream = null;
	private DataInputStream in = null;
	private BufferedReader br = null;

	public SPTInformationFileLoader(final String fileName) {
		this.fileName = fileName;
		this.executionInfoHandler = new SPTExecutionInfoHandler();
	}

	@Override
	public void initLoader() {
		try {
			this.fstream = new FileInputStream(this.fileName);
			this.in = new DataInputStream(this.fstream);
			this.br = new BufferedReader(new InputStreamReader(this.in));
		} catch (final FileNotFoundException e) {
			JOptionPane.showMessageDialog(null,
			        OmegaConstants.ERROR_NO_SPT_INFORMATION,
			        OmegaConstants.OMEGA_TITLE, JOptionPane.ERROR_MESSAGE);
			GLogManager.log(String.format("%s: %s",
			        OmegaConstants.ERROR_NO_SPT_INFORMATION, e.toString()),
			        Level.SEVERE);
			this.br = null;
		}
	}

	@Override
	public SPTExecutionInfoHandler loadInformation() {
		if (this.br != null) {
			String line = null;
			boolean error = false;

			try {
				while ((line = this.br.readLine()) != null) {
					if (!this.processLine(line)) {
						error = true;
						break;
					}
				}
			}

			catch (final IOException e) {
				GLogManager.log(String.format("%s: %s",
				        OmegaConstants.ERROR_NO_SPT_INFORMATION, e.toString()),
				        Level.SEVERE);
				error = true;
			}

			if (error) {
				JOptionPane.showMessageDialog(null,
				        OmegaConstants.ERROR_NO_SPT_INFORMATION,
				        OmegaConstants.OMEGA_TITLE, JOptionPane.ERROR_MESSAGE);
				return null;
			} else
				return this.executionInfoHandler;
		} else
			return null;
	}

	private boolean processLine(String line) {
		line = line.trim().replaceAll(" ", "");

		String[] splitted = new String[2];

		try {
			splitted = line.split(OmegaConstants.SPT_INFORMATION_SEPARATOR);

			// image data
			if (splitted[0].equals("image_name")) {
				this.executionInfoHandler.getImageData().setImageName(
				        splitted[1]);
			}
			if (splitted[0].equals("image_dataset")) {
				this.executionInfoHandler.getImageData().setImageDatasetName(
				        splitted[1]);
			}
			if (splitted[0].equals("frames_number")) {
				this.executionInfoHandler.getImageData().setT(
				        Integer.valueOf(splitted[1]));
			}
			if (splitted[0].equals("image_width")) {
				this.executionInfoHandler.getImageData().setX(
				        Integer.valueOf(splitted[1]));
			}
			if (splitted[0].equals("image_height")) {
				this.executionInfoHandler.getImageData().setY(
				        Integer.valueOf(splitted[1]));
			}
			if (splitted[0].equals("image_width_size")) {
				this.executionInfoHandler.getImageData().setSizeX(
				        Double.valueOf(splitted[1]));
			}
			if (splitted[0].equals("image_height_size")) {
				this.executionInfoHandler.getImageData().setSizeY(
				        Double.valueOf(splitted[1]));
			}
			if (splitted[0].equals("image_delta_t")) {
				this.executionInfoHandler.getImageData().setSizeT(
				        Double.valueOf(splitted[1]));
			}

			// omero data
			if (splitted[0].equals("channel_processed")) {
				this.executionInfoHandler.getOmeroParameters().setC(
				        Integer.valueOf(splitted[1]));
			}
			if (splitted[0].equals("plane_processed")) {
				this.executionInfoHandler.getOmeroParameters().setZ(
				        Integer.valueOf(splitted[1]));
			}

			// SPT running data
			if (splitted[0].equals("radius")) {
				this.executionInfoHandler.setRadius(splitted[1]);
			}
			if (splitted[0].equals("cut_off")) {
				this.executionInfoHandler.setCutOff(splitted[1]);
			}
			if (splitted[0].equals("percentile")) {
				this.executionInfoHandler.setPercentile(splitted[1]);
			}
			if (splitted[0].equals("displacement")) {
				this.executionInfoHandler.setDisplacement(splitted[1]);
			}
			if (splitted[0].equals("link_range")) {
				this.executionInfoHandler.setLinkRange(splitted[1]);
			}
		} catch (final Exception e) {
			return false;
		}
		return true;
	}

	@Override
	public void closeLoader() {
		try {
			this.in.close();
		} catch (final IOException e) {
			GLogManager.log(
			        String.format("%s: %s", "unable to close the reader",
			                e.toString()), Level.WARNING);
		}
	}
}
