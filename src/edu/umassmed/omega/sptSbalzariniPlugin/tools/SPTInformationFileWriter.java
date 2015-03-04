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
package edu.umassmed.omega.sptSbalzariniPlugin.tools;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import edu.umassmed.omega.data.coreElements.OmegaDataset;
import edu.umassmed.omega.data.coreElements.OmegaImage;
import edu.umassmed.omega.data.coreElements.OmegaImagePixels;

public class SPTInformationFileWriter extends SPTInformationWriter {

	private String fileName = "";
	private FileWriter fstream = null;
	private BufferedWriter out = null;

	public SPTInformationFileWriter(final String fileName) {
		this.fileName = fileName;
	}

	@Override
	public void initWriter() {
		try {
			this.fstream = new FileWriter(this.fileName);
			this.out = new BufferedWriter(this.fstream);
		} catch (final IOException e) {

		}
	}

	@Override
	public String writeInformation(
	        final SPTExecutionInfoHandler executionInfoHandler) {
		String statsString = "";

		try {
			if (executionInfoHandler != null) {
				final OmegaImage image = executionInfoHandler.getImage();
				if (image != null) {
					this.out.write(String.format("%s = %s",
					        "image_name       ", image.getName()));
					this.out.newLine();
					for (final OmegaDataset dataset : image.getParentDatasets()) {
						this.out.write(String.format("%s = %s",
						        "image_dataset    ", dataset.getName()));
						this.out.newLine();
					}
					final OmegaImagePixels defaultPixels = image
					        .getDefaultPixels();
					this.out.write(String.format("%s = %d",
					        "image_width      ", defaultPixels.getSizeX()));
					this.out.newLine();
					this.out.write(String.format("%s = %f",
					        "image_width_size ", defaultPixels.getPixelSizeX()));
					this.out.newLine();
					this.out.write(String.format("%s = %d",
					        "image_height     ", defaultPixels.getSizeY()));
					this.out.newLine();
					this.out.write(String.format("%s = %f",
					        "image_height_size", defaultPixels.getPixelSizeY()));
					this.out.newLine();
					this.out.write(String.format("%s = %d",
					        "frames_number    ", defaultPixels.getSizeT()));
					this.out.newLine();
					this.out.write(String.format("%s = %f",
					        "total_time       ", defaultPixels.getPixelsSizeT()));
					this.out.newLine();
					this.out.write(String.format("%s = %f",
					        "image_delta_t    ", imageDataHandler.getSizeT()
					                / imageDataHandler.getT()));
					this.out.newLine();

					statsString = statsString
					        + String.format("%s : %s", "image name       ",
					                imageDataHandler.getImageName()) + "\n";
					statsString = statsString
					        + String.format("%s : %s", "image dataset    ",
					                imageDataHandler.getImageDatasetName())
					        + "\n";
					statsString = statsString
					        + String.format("%s : %d", "image width      ",
					                imageDataHandler.getX()) + "\n";
					statsString = statsString
					        + String.format("%s : %f", "image width size ",
					                imageDataHandler.getSizeX()) + "\n";
					statsString = statsString
					        + String.format("%s : %d", "image height     ",
					                imageDataHandler.getY()) + "\n";
					statsString = statsString
					        + String.format("%s : %f", "image height size",
					                imageDataHandler.getSizeY()) + "\n";
					statsString = statsString
					        + String.format("%s : %d", "frames number    ",
					                imageDataHandler.getT()) + "\n";
					statsString = statsString
					        + String.format("%s : %f", "total time       ",
					                imageDataHandler.getSizeT()) + "\n";
					statsString = statsString
					        + String.format("%s : %f", "image delta_t    ",
					                imageDataHandler.getSizeT()
					                        / imageDataHandler.getT()) + "\n";
				}

				final OmeroParametersHandler omeroParametersHandler = executionInfoHandler
				        .getOmeroParameters();
				if (omeroParametersHandler != null) {
					this.out.write(String.format("%s = %d",
					        "channel_processed", omeroParametersHandler.getC()));
					this.out.newLine();
					this.out.write(String.format("%s = %d",
					        "plane_processed  ", omeroParametersHandler.getZ()));
					this.out.newLine();

					statsString = statsString + "\n";
					statsString = statsString
					        + String.format("%s : %d", "channel processed",
					                omeroParametersHandler.getC()) + "\n";
					statsString = statsString
					        + String.format("%s : %d", "plane processed  ",
					                omeroParametersHandler.getZ()) + "\n";
				}

				this.out.write(String.format("%s = %s", "radius           ",
				        executionInfoHandler.getRadius()));
				this.out.newLine();
				this.out.write(String.format("%s = %s", "cut_off          ",
				        executionInfoHandler.getCutOff()));
				this.out.newLine();
				this.out.write(String.format("%s = %s", "percentile       ",
				        executionInfoHandler.getPercentile()));
				this.out.newLine();
				this.out.write(String.format("%s = %s", "displacement     ",
				        executionInfoHandler.getDisplacement()));
				this.out.newLine();
				this.out.write(String.format("%s = %s", "link_range       ",
				        executionInfoHandler.getLinkRange()));

				statsString = statsString
				        + String.format("%s : %s", "radius           ",
				                executionInfoHandler.getRadius()) + "\n";
				statsString = statsString
				        + String.format("%s : %s", "cut_off          ",
				                executionInfoHandler.getCutOff()) + "\n";
				statsString = statsString
				        + String.format("%s : %s", "percentile       ",
				                executionInfoHandler.getPercentile()) + "\n";
				statsString = statsString
				        + String.format("%s : %s", "displacement     ",
				                executionInfoHandler.getDisplacement()) + "\n";
				statsString = statsString
				        + String.format("%s : %s", "link_range       ",
				                executionInfoHandler.getLinkRange()) + "\n";
			}
		} catch (final IOException e) {
		}

		return statsString;
	}

	@Override
	public void closeWriter() {
		try {
			this.out.close();
		} catch (final IOException e) {
		}
	}
}
