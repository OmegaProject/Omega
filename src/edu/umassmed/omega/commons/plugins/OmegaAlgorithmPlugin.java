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
package edu.umassmed.omega.commons.plugins;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.umassmed.omega.commons.utilities.OperatingSystemEnum;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaAlgorithmInformation;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.dataNew.coreElements.OmegaPerson;

public abstract class OmegaAlgorithmPlugin extends OmegaPlugin {

	public OmegaAlgorithmPlugin() {
		this(1);
	}

	public OmegaAlgorithmPlugin(final int maxNumOfPanels) {
		super(maxNumOfPanels);
	}

	public List<OperatingSystemEnum> getSupportedPlatforms() {
		final List<OperatingSystemEnum> supportedPlatforms = new ArrayList<OperatingSystemEnum>();
		for (final OperatingSystemEnum os : OperatingSystemEnum.values()) {
			supportedPlatforms.add(os);
		}
		return supportedPlatforms;
	}

	public abstract String getAlgorithmName();

	public abstract String getAlgorithmDescription();

	public abstract OmegaPerson getAlgorithmAuthor();

	public abstract Double getAlgorithmVersion();

	public abstract Date getAlgorithmPublicationDate();

	public boolean checkIfThisAlgorithm(final OmegaAnalysisRun analysisRun) {
		final OmegaAlgorithmInformation algoInfo = analysisRun
		        .getAlgorithmSpec().getAlgorithmInfo();
		final boolean tof1 = this.getAlgorithmName().equals(algoInfo.getName());
		final boolean tof2 = this.getAlgorithmDescription().equals(
		        algoInfo.getDescription());
		final boolean tof3 = this.getAlgorithmAuthor().isSamePersonAs(
		        algoInfo.getAuthor());
		final boolean tof4 = this.getAlgorithmVersion().equals(
		        algoInfo.getVersion());
		final boolean tof5 = this.getAlgorithmPublicationDate().equals(
		        algoInfo.getPublicationData());
		return tof1 && tof2 && tof3 && tof4 && tof5;
	}
}
