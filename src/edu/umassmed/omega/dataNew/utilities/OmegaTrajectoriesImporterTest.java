package edu.umassmed.omega.dataNew.utilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.umassmed.omega.algoritmhs.OmegaAlgorithmPoint;
import edu.umassmed.omega.algoritmhs.OmegaTrajectoryStatistics;
import edu.umassmed.omega.dataNew.trajectoryElements.OmegaROI;
import edu.umassmed.omega.dataNew.trajectoryElements.OmegaTrajectory;

public class OmegaTrajectoriesImporterTest {
	public static void main(final String[] args) {
		final String dirName = "F:\\2014-10-06_TrajectoryGeneratorValidation_NoNoise";
		final File dir = new File(dirName);
		final String omegaSMSSDirName = dirName + "\\omegaSMSS";
		final String omegaDDirName = dirName + "\\omegaD";
		final File omegaSMSSDir = new File(omegaSMSSDirName);
		omegaSMSSDir.mkdir();
		final File omegaDDir = new File(omegaDDirName);
		omegaDDir.mkdir();
		final String subDirName1 = "tracks_[\\d-]+";
		final String subDirName2 = "L_[\\d]+_SMSS_[\\d-]+_D_[\\d-]+";
		final List<String> dataOrder = new ArrayList<String>();
		dataOrder.add(OmegaTrajectoriesImporterUtilities.PARTICLE_FRAMEINDEX);
		dataOrder.add(OmegaTrajectoriesImporterUtilities.PARTICLE_XCOORD);
		dataOrder.add(OmegaTrajectoriesImporterUtilities.PARTICLE_YCOORD);
		final String fileName = "track_[\\d]+.out";
		final String trajIdent = null;
		final String particleIdent = null;
		final String nonParticleIdent = null;
		final String particleSep = "\t";
		final Map<Double, Map<Integer, Map<Double, Map<Double, List<Double>>>>> smssOutput = new LinkedHashMap<>();
		final Map<Double, Map<Integer, Map<Double, Map<Double, List<Double>>>>> dOutput = new LinkedHashMap<>();
		try {
			for (final File f1 : dir.listFiles()) {
				if (!f1.isDirectory()) {
					continue;
				}
				final String fName1 = f1.getName();
				if (!fName1.matches(subDirName1)) {
					continue;
				}
				final String[] vals1 = fName1.split("_");
				final Double snr = Double.valueOf(vals1[1].replace("-", "."));
				for (final File f2 : f1.listFiles()) {
					if (!f2.isDirectory()) {
						continue;
					}
					final String fName2 = f2.getName();
					if (!fName2.matches(subDirName2)) {
						continue;
					}
					final String[] vals2 = fName2.split("_");
					final Integer L = Integer.valueOf(vals2[1]);
					final Double SMSS = Double.valueOf(vals2[3].replace("-",
					        "."));
					final Double D = Double.valueOf(vals2[5].replace("-", "."));
					System.out.println("Import SNR " + snr + " L " + L
					        + " SMSS " + SMSS + " D " + D);
					final List<OmegaTrajectory> trajs = OmegaTrajectoriesImporterUtilities
					        .importTrajectories(fileName, trajIdent,
					                particleIdent, nonParticleIdent,
					                particleSep, dataOrder, f2);

					Map<Integer, Map<Double, Map<Double, List<Double>>>> lSMSSMap;
					if (smssOutput.containsKey(snr)) {
						lSMSSMap = smssOutput.get(snr);
					} else {
						lSMSSMap = new LinkedHashMap<>();
					}
					Map<Double, Map<Double, List<Double>>> smssSMSSMap;
					if (lSMSSMap.containsKey(L)) {
						smssSMSSMap = lSMSSMap.get(L);
					} else {
						smssSMSSMap = new LinkedHashMap<>();
					}
					Map<Double, List<Double>> dSMSSMap;
					if (smssSMSSMap.containsKey(SMSS)) {
						dSMSSMap = smssSMSSMap.get(SMSS);
					} else {
						dSMSSMap = new LinkedHashMap<>();
					}
					List<Double> outputSMSS;
					if (dSMSSMap.containsKey(D)) {
						outputSMSS = dSMSSMap.get(D);
					} else {
						outputSMSS = new ArrayList<>();
					}

					Map<Integer, Map<Double, Map<Double, List<Double>>>> lDMap;
					if (dOutput.containsKey(snr)) {
						lDMap = dOutput.get(snr);
					} else {
						lDMap = new LinkedHashMap<>();
					}
					Map<Double, Map<Double, List<Double>>> smssDMap;
					if (lDMap.containsKey(L)) {
						smssDMap = lDMap.get(L);
					} else {
						smssDMap = new LinkedHashMap<>();
					}
					Map<Double, List<Double>> dDMap;
					if (smssDMap.containsKey(SMSS)) {
						dDMap = smssDMap.get(SMSS);
					} else {
						dDMap = new LinkedHashMap<>();
					}
					List<Double> outputD;
					if (dDMap.containsKey(D)) {
						outputD = dDMap.get(D);
					} else {
						outputD = new ArrayList<>();
					}

					for (final OmegaTrajectory traj : trajs) {
						final List<OmegaAlgorithmPoint> points = new ArrayList<>();
						for (final OmegaROI roi : traj.getROIs()) {
							points.add(new OmegaAlgorithmPoint(roi.getX(), roi
							        .getY(), roi.getFrameIndex()));
						}
						final OmegaTrajectoryStatistics stats = new OmegaTrajectoryStatistics(
						        points, 1.0, 3);
						final Double omegaD = stats.getGammaAndD(2)[3];
						final Double omegaSMSS = stats.getSMSS()[0];
						outputSMSS.add(omegaSMSS);
						outputD.add(omegaD);
					}

					dDMap.put(D, outputD);
					dSMSSMap.put(D, outputSMSS);
					smssDMap.put(SMSS, dDMap);
					smssSMSSMap.put(SMSS, dSMSSMap);
					lDMap.put(L, smssDMap);
					lSMSSMap.put(L, smssSMSSMap);
					dOutput.put(snr, lDMap);
					smssOutput.put(snr, lSMSSMap);
				}
			}
		} catch (final IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int snrCounter = 0, lCounter = 0;
		String snrString = "", lString = "";
		for (final Double snr : smssOutput.keySet()) {
			snrCounter++;
			if (snrCounter < 10) {
				snrString = "0" + String.valueOf(snrCounter);
			} else {
				snrString = String.valueOf(snrCounter);
			}
			final Map<Integer, Map<Double, Map<Double, List<Double>>>> lSMSSMap = smssOutput
			        .get(snr);
			final Map<Integer, Map<Double, Map<Double, List<Double>>>> lDMap = dOutput
			        .get(snr);
			for (final Integer l : lSMSSMap.keySet()) {
				lCounter++;
				if (lCounter < 10) {
					lString = "0" + String.valueOf(lCounter);
				} else {
					lString = String.valueOf(lCounter);
				}
				int rowCounter = 0;
				final Map<Double, Map<Double, List<Double>>> smssSMSSMap = lSMSSMap
				        .get(l);
				final Map<Double, Map<Double, List<Double>>> smssDMap = lDMap
				        .get(l);
				for (final Double smss : smssSMSSMap.keySet()) {
					final Map<Double, List<Double>> dSMSSMap = smssSMSSMap
					        .get(smss);
					final Map<Double, List<Double>> dDMap = smssDMap.get(smss);
					for (final Double d : dSMSSMap.keySet()) {
						rowCounter++;
						System.out.println("Computing SNR " + snr + " L " + l
						        + " SMSS " + smss + " D " + d);
						final List<Double> smssValues = dSMSSMap.get(d);
						final List<Double> dValues = dDMap.get(d);
						final String smssFileName = omegaSMSSDir
						        + "\\SMSS_values_SNR_" + snrString + "_L_"
						        + lString + ".csv";
						final String dFileName = omegaDDir + "\\D_values_SNR_"
						        + snrString + "_L_" + lString + ".csv";
						final StringBuffer row = new StringBuffer();
						row.append(String.valueOf(rowCounter));
						row.append(" ");
						row.append(String.valueOf(snr));
						row.append(" ");
						row.append(String.valueOf(l));
						row.append(" ");
						row.append(String.valueOf(smss));
						row.append(" ");
						row.append(String.valueOf(d));
						row.append(";");
						row.append("***");
						row.append(";");
						final StringBuffer smssRow = new StringBuffer(
						        row.toString());
						final StringBuffer dRow = new StringBuffer(
						        row.toString());

						for (final Double val : smssValues) {
							smssRow.append(String.valueOf(val));
							smssRow.append(";");
						}
						smssRow.append("\n");
						for (final Double val : dValues) {
							dRow.append(String.valueOf(val));
							dRow.append(";");
						}
						dRow.append("\n");
						try {
							final File smssFile = new File(smssFileName);
							FileWriter fw = new FileWriter(smssFile, true);
							BufferedWriter bw = new BufferedWriter(fw);
							bw.write(smssRow.toString());
							bw.close();
							fw.close();

							final File dFile = new File(dFileName);
							fw = new FileWriter(dFile, true);
							bw = new BufferedWriter(fw);
							bw.write(dRow.toString());
							bw.close();
							fw.close();
						} catch (final IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
}
