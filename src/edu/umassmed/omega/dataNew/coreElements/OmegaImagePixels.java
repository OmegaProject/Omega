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
package edu.umassmed.omega.dataNew.coreElements;

import java.util.ArrayList;
import java.util.List;

import edu.umassmed.omega.dataNew.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaAnalysisRunContainer;

public class OmegaImagePixels extends OmegaElement implements
        OmegaAnalysisRunContainer {

	private OmegaImage image;

	private final String pixelsType;

	private final int sizeX, sizeY, sizeZ, sizeC, sizeT;

	private final double pixelSizeX, pixelSizeY, pixelSizeZ;

	private int selectedZ;

	private int selectedC;

	private final List<OmegaFrame> frames;

	private final List<OmegaAnalysisRun> analysisRuns;

	public OmegaImagePixels(final Long elementID, final String pixelsType) {
		super(elementID);

		this.image = null;

		this.pixelsType = pixelsType;

		this.sizeX = -1;
		this.sizeY = -1;
		this.sizeZ = -1;
		this.sizeC = -1;
		this.sizeT = -1;

		this.pixelSizeX = -1;
		this.pixelSizeY = -1;
		this.pixelSizeZ = -1;

		this.selectedZ = -1;
		this.selectedC = -1;

		this.frames = new ArrayList<OmegaFrame>();
		this.analysisRuns = new ArrayList<OmegaAnalysisRun>();
	}

	public OmegaImagePixels(final Long elementID, final String pixelsType,
	        final int sizeX, final int sizeY, final int sizeZ) {
		super(elementID);

		this.image = null;

		this.pixelsType = pixelsType;

		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;

		this.sizeC = -1;
		this.sizeT = -1;

		this.pixelSizeX = -1;
		this.pixelSizeY = -1;
		this.pixelSizeZ = -1;

		this.selectedZ = -1;
		this.selectedC = -1;

		this.frames = new ArrayList<OmegaFrame>();
		this.analysisRuns = new ArrayList<OmegaAnalysisRun>();
	}

	public OmegaImagePixels(final Long elementID, final String pixelsType,
	        final int sizeX, final int sizeY, final int sizeZ,
	        final List<OmegaFrame> frames) {
		super(elementID);

		this.image = null;

		this.pixelsType = pixelsType;

		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;

		this.sizeC = -1;
		this.sizeT = -1;

		this.pixelSizeX = -1;
		this.pixelSizeY = -1;
		this.pixelSizeZ = -1;

		this.selectedZ = -1;
		this.selectedC = -1;

		this.frames = frames;
		this.analysisRuns = new ArrayList<OmegaAnalysisRun>();
	}

	public OmegaImagePixels(final Long elementID, final String pixelsType,
	        final int sizeX, final int sizeY, final int sizeZ, final int sizeC,
	        final int sizeT) {
		super(elementID);

		this.image = null;

		this.pixelsType = pixelsType;

		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;

		this.sizeC = sizeC;
		this.sizeT = sizeT;

		this.pixelSizeX = -1;
		this.pixelSizeY = -1;
		this.pixelSizeZ = -1;

		this.selectedZ = -1;
		this.selectedC = -1;

		this.frames = new ArrayList<OmegaFrame>();
		this.analysisRuns = new ArrayList<OmegaAnalysisRun>();
	}

	public OmegaImagePixels(final Long elementID, final String pixelsType,
	        final int sizeX, final int sizeY, final int sizeZ, final int sizeC,
	        final int sizeT, final List<OmegaFrame> frames) {
		super(elementID);

		this.image = null;

		this.pixelsType = pixelsType;

		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;

		this.sizeC = sizeC;
		this.sizeT = sizeT;

		this.pixelSizeX = -1;
		this.pixelSizeY = -1;
		this.pixelSizeZ = -1;

		this.selectedZ = -1;
		this.selectedC = -1;

		this.frames = frames;
		this.analysisRuns = new ArrayList<OmegaAnalysisRun>();
	}

	public OmegaImagePixels(final Long elementID, final String pixelsType,
	        final int sizeX, final int sizeY, final int sizeZ, final int sizeC,
	        final int sizeT, final double pixelSizeX, final double pixelSizeY,
	        final double pixelSizeZ) {
		super(elementID);

		this.image = null;

		this.pixelsType = pixelsType;

		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;

		this.sizeC = sizeC;
		this.sizeT = sizeT;

		if (pixelSizeX == 0) {
			this.pixelSizeX = -1;
		} else {
			this.pixelSizeX = pixelSizeX;
		}
		if (pixelSizeY == 0) {
			this.pixelSizeY = -1;
		} else {
			this.pixelSizeY = pixelSizeY;
		}
		if (pixelSizeZ == 0) {
			this.pixelSizeZ = -1;
		} else {
			this.pixelSizeZ = pixelSizeZ;
		}

		this.selectedZ = -1;
		this.selectedC = -1;

		this.frames = new ArrayList<OmegaFrame>();
		this.analysisRuns = new ArrayList<OmegaAnalysisRun>();
	}

	public OmegaImagePixels(final Long elementID, final String pixelsType,
	        final int sizeX, final int sizeY, final int sizeZ, final int sizeC,
	        final int sizeT, final double pixelSizeX, final double pixelSizeY,
	        final double pixelSizeZ, final List<OmegaFrame> frames) {
		super(elementID);

		this.image = null;

		this.pixelsType = pixelsType;

		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;

		this.sizeC = sizeC;
		this.sizeT = sizeT;

		if (pixelSizeX == 0) {
			this.pixelSizeX = -1;
		} else {
			this.pixelSizeX = pixelSizeX;
		}
		if (pixelSizeY == 0) {
			this.pixelSizeY = -1;
		} else {
			this.pixelSizeY = pixelSizeY;
		}
		if (pixelSizeZ == 0) {
			this.pixelSizeZ = -1;
		} else {
			this.pixelSizeZ = pixelSizeZ;
		}

		this.selectedZ = -1;
		this.selectedC = -1;

		this.frames = frames;
		this.analysisRuns = new ArrayList<OmegaAnalysisRun>();
	}

	public void setParentImage(final OmegaImage image) {
		this.image = image;
	}

	public OmegaImage getParentImage() {
		return this.image;
	}

	public String getPixelsType() {
		return this.pixelsType;
	}

	public int getSizeX() {
		return this.sizeX;
	}

	public int getSizeY() {
		return this.sizeY;
	}

	public int getSizeZ() {
		return this.sizeZ;
	}

	public int getSizeC() {
		return this.sizeC;
	}

	public int getSizeT() {
		return this.sizeT;
	}

	public double getPixelSizeX() {
		return this.pixelSizeX;
	}

	public double getPixelSizeY() {
		return this.pixelSizeY;
	}

	public double getPixelSizeZ() {
		return this.pixelSizeZ;
	}

	public List<OmegaFrame> getFrames() {
		return this.frames;
	}

	public void addFrames(final List<OmegaFrame> frames) {
		this.frames.addAll(frames);
	}

	public void addFrame(final OmegaFrame frame) {
		this.frames.add(frame);
	}

	public boolean containsFrame(final long id) {
		for (final OmegaFrame frame : this.frames) {
			if (frame.getElementID() == id)
				return true;
		}
		return false;
	}

	@Override
	public List<OmegaAnalysisRun> getAnalysisRuns() {
		return this.analysisRuns;
	}

	@Override
	public void addAnalysisRun(final OmegaAnalysisRun analysisRun) {
		this.analysisRuns.add(analysisRun);
	}

	@Override
	public void removeAnalysisRun(final OmegaAnalysisRun analysisRun) {
		this.analysisRuns.remove(analysisRun);
	}

	@Override
	public boolean containsAnalysisRun(final long id) {
		for (final OmegaAnalysisRun analysisRun : this.analysisRuns) {
			if (analysisRun.getElementID() == id)
				return true;
		}
		return false;
	}

	public int getSelectedZ() {
		return this.selectedZ;
	}

	public void setSelectedZ(final int newZ) {
		this.selectedZ = newZ;
	}

	public int getSelectedC() {
		return this.selectedC;
	}

	public void setSelectedC(final int newC) {
		this.selectedC = newC;
	}
}
