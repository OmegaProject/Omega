package edu.umassmed.omega.core;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.RootPaneContainer;

import edu.umassmed.omega.commons.plugins.OmegaAlgorithmPlugin;
import edu.umassmed.omega.core.gui.OmegaAlgorithmPluginInfoDialog;

public class OmegaAlgorithmPluginDetailsDialogManager {
	private static Map<OmegaAlgorithmPlugin, OmegaAlgorithmPluginInfoDialog> dialogsByPlugin;
	private static Map<Long, OmegaAlgorithmPluginInfoDialog> dialogsByID;

	static {
		OmegaAlgorithmPluginDetailsDialogManager.dialogsByPlugin = new LinkedHashMap<>();
		OmegaAlgorithmPluginDetailsDialogManager.dialogsByID = new LinkedHashMap<>();
	}

	public static void registerDialog(final Long id,
	        final OmegaAlgorithmPlugin plugin) {
		final OmegaAlgorithmPluginInfoDialog dialog = new OmegaAlgorithmPluginInfoDialog(
		        null, plugin);
		OmegaAlgorithmPluginDetailsDialogManager.dialogsByID.put(id, dialog);
		OmegaAlgorithmPluginDetailsDialogManager.dialogsByPlugin.put(plugin, dialog);
	}

	public static void showDialog(final Long id,
	        final RootPaneContainer parentContainer) {
		final OmegaAlgorithmPluginInfoDialog dialog = OmegaAlgorithmPluginDetailsDialogManager.dialogsByID
		        .get(id);
		dialog.updateParentContainer(parentContainer);
		if (dialog != null) {
			dialog.setVisible(true);
		} else
			// TODO throw error
			return;
	}

	public static void showDialog(final OmegaAlgorithmPlugin plugin,
	        final RootPaneContainer parentContainer) {
		final OmegaAlgorithmPluginInfoDialog dialog = OmegaAlgorithmPluginDetailsDialogManager.dialogsByID
		        .get(plugin);
		dialog.updateParentContainer(parentContainer);
		if (dialog != null) {
			dialog.setVisible(true);
		} else
			// TODO throw error
			return;
	}
}
