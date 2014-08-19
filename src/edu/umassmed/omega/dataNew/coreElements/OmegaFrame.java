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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import edu.umassmed.omega.dataNew.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaAnalysisRunContainer;

public class OmegaFrame extends OmegaElement implements
        OmegaAnalysisRunContainer {

	private OmegaImagePixels pixels;

	private final Integer index;

	private final List<OmegaAnalysisRun> analysisRuns;

	private Integer zPlane, channel;
	// TODO needed?
	private final Date timeStamps;

	public OmegaFrame(final Integer index) {
		super((long) -1);

		this.pixels = null;

		this.index = index;

		this.zPlane = -1;
		this.channel = -1;

		this.timeStamps = Calendar.getInstance().getTime();

		this.analysisRuns = new ArrayList<OmegaAnalysisRun>();
	}

	public OmegaFrame(final Long elementID, final Integer index,
	        final Integer channel, final Integer zPlane) {
		super(elementID);

		this.pixels = null;

		this.index = index;

		this.zPlane = zPlane;
		this.channel = channel;

		this.timeStamps = Calendar.getInstance().getTime();

		this.analysisRuns = new ArrayList<OmegaAnalysisRun>();
	}

	public OmegaFrame(final Long elementID, final Integer index,
	        final Integer channel, final List<OmegaAnalysisRun> analysisRuns) {
		super(elementID);

		this.pixels = null;

		this.index = index;

		this.channel = channel;

		this.timeStamps = Calendar.getInstance().getTime();

		this.analysisRuns = new ArrayList<OmegaAnalysisRun>();
	}

	public void setParentPixels(final OmegaImagePixels pixels) {
		this.pixels = pixels;
	}

	public OmegaImagePixels getParentPixels() {
		return this.pixels;
	}

	public Integer getIndex() {
		return this.index;
	}

	public Integer getChannel() {
		return this.channel;
	}

	public void setChannel(final Integer channel) {
		this.channel = channel;
	}

	public Integer getZPlane() {
		return this.zPlane;
	}

	public void setZPlane(final Integer zPlane) {
		this.zPlane = zPlane;
	}

	public Date getTimeStamps() {
		return this.timeStamps;
	}

	@Override
	public List<OmegaAnalysisRun> getAnalysisRuns() {
		return this.analysisRuns;
	}

	@Override
	public void addAnalysisRun(final OmegaAnalysisRun analysisRun) {
		this.analysisRuns.add(analysisRun);
	}
}
