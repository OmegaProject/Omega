package edu.umassmed.omega.commons.utilities;

import java.awt.Color;
import java.util.List;
import java.util.Random;

import javax.swing.JColorChooser;
import javax.swing.JComponent;

import edu.umassmed.omega.dataNew.trajectoryElements.OmegaTrajectory;

public class OmegaTrajectoryColorManagerUtility {

	public static Color openPaletteColor(
	        final List<OmegaTrajectory> trajectories, final JComponent parent,
	        final int trajIndex) {
		final OmegaTrajectory traj = trajectories.get(trajIndex);

		final StringBuffer buf = new StringBuffer();
		buf.append("Choose color for trajectory ");
		buf.append(trajIndex + 1);

		final Color c = JColorChooser.showDialog(parent, buf.toString(),
		        traj.getColor());

		return c;
	}

	public static void generateRandomColors(
	        final List<OmegaTrajectory> trajectories) {
		final Random random = new Random();

		for (int i = 0; i < trajectories.size(); i++) {
			final float fr = (random.nextFloat() / 2.0f) + 0.5f;
			final float fg = (random.nextFloat() / 2.0f) + 0.5f;
			final float fb = (random.nextFloat() / 2.0f) + 0.5f;
			trajectories.get(i).setColor(new Color(fr, fg, fb));
		}
	}
}
