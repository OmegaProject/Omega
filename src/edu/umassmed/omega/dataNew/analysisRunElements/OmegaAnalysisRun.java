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
package edu.umassmed.omega.dataNew.analysisRunElements;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import edu.umassmed.omega.commons.constants.OmegaConstants;
import edu.umassmed.omega.dataNew.coreElements.OmegaElement;
import edu.umassmed.omega.dataNew.coreElements.OmegaExperimenter;

public abstract class OmegaAnalysisRun extends OmegaElement implements
        OmegaAnalysisRunContainer {

	private final String name;

	private final Date timeStamps;

	private final OmegaExperimenter experimenter;

	// TODO aggiungere OmegaExperimenterGroup permissions

	private final OmegaAlgorithmSpecification algorithmSpec;

	private List<OmegaAnalysisRun> analysisRuns;

	public OmegaAnalysisRun(final OmegaExperimenter owner,
	        final OmegaAlgorithmSpecification algorithmSpec) {
		super((long) -1);

		this.timeStamps = Calendar.getInstance().getTime();

		this.experimenter = owner;

		this.algorithmSpec = algorithmSpec;

		this.analysisRuns = new ArrayList<OmegaAnalysisRun>();

		final StringBuffer nameBuf = new StringBuffer();
		final DateFormat format = new SimpleDateFormat(
		        OmegaConstants.OMEGA_DATE_FORMAT);
		nameBuf.append(format.format(this.timeStamps));
		nameBuf.append("_");
		nameBuf.append(algorithmSpec.getAlgorithmInfo().getName());
		this.name = nameBuf.toString();
	}

	public OmegaAnalysisRun(final OmegaExperimenter owner,
	        final OmegaAlgorithmSpecification algorithmSpec,
	        final Date timeStamps, final String name) {
		super((long) -1);

		this.timeStamps = timeStamps;

		this.experimenter = owner;

		this.algorithmSpec = algorithmSpec;

		this.analysisRuns = new ArrayList<OmegaAnalysisRun>();

		this.name = name;
	}

	public OmegaAnalysisRun(final OmegaExperimenter owner,
	        final OmegaAlgorithmSpecification algorithmSpec,
	        final List<OmegaAnalysisRun> analysisRuns) {
		this(owner, algorithmSpec);

		this.analysisRuns = analysisRuns;
	}

	public String getName() {
		return this.name;
	}

	public Date getTimeStamps() {
		return this.timeStamps;
	}

	public OmegaExperimenter getExperimenter() {
		return this.experimenter;
	}

	public OmegaAlgorithmSpecification getAlgorithmSpec() {
		return this.algorithmSpec;
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
