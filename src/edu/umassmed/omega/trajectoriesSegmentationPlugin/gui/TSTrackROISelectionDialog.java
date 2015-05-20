package edu.umassmed.omega.trajectoriesSegmentationPlugin.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.RootPaneContainer;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.umassmed.omega.commons.constants.OmegaConstants;
import edu.umassmed.omega.commons.gui.dialogs.GenericDialog;
import edu.umassmed.omega.commons.utilities.OmegaStringUtilities;
import edu.umassmed.omega.data.trajectoryElements.OmegaROI;
import edu.umassmed.omega.trajectoriesSegmentationPlugin.TSConstants;

public class TSTrackROISelectionDialog extends GenericDialog {
	private static final long serialVersionUID = -4055355735984837884L;

	private OmegaROI roi;
	private final List<OmegaROI> availableROIs;
	private JButton ok_btt;
	private JList<String> list;
	private JScrollPane listScrollPane;

	public TSTrackROISelectionDialog(final RootPaneContainer parentContainer,
	        final List<OmegaROI> availableROIs) {
		super(parentContainer, TSConstants.ROI_SELECT_DIALOG, true);

		this.roi = null;
		this.availableROIs = availableROIs;
		this.createAndSetListModel();
		String maxS = "";
		for (int i = 0; i < this.list.getModel().getSize(); i++) {
			final String s = this.list.getModel().getElementAt(i);
			if (maxS.length() < s.length()) {
				maxS = s;
			}
		}
		final Dimension stringDim = OmegaStringUtilities.getStringSize(
		        this.list.getGraphics(), this.list.getFont(), maxS);
		final Dimension newDim = new Dimension(stringDim.width,
		        this.list.getHeight());
		this.listScrollPane.setPreferredSize(newDim);
		this.listScrollPane.setSize(newDim);

		final Dimension dim = new Dimension(400, 200);
		this.setSize(dim);
		this.setPreferredSize(dim);

		this.revalidate();
		this.repaint();
	}

	@Override
	protected void createAndAddWidgets() {
		final JLabel lbl = new JLabel(TSConstants.ROI_SELECT_DIALOG_MSG);
		this.add(lbl, BorderLayout.NORTH);

		this.list = new JList<>();
		this.list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.listScrollPane = new JScrollPane(this.list);

		this.add(this.listScrollPane, BorderLayout.CENTER);

		final JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		this.ok_btt = new JButton(TSConstants.ROI_SELECT_OK);
		this.ok_btt.setPreferredSize(OmegaConstants.BUTTON_SIZE);
		this.ok_btt.setSize(OmegaConstants.BUTTON_SIZE);
		buttonPanel.add(this.ok_btt);

		this.add(buttonPanel, BorderLayout.SOUTH);
	}

	@Override
	protected void addListeners() {
		this.ok_btt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				TSTrackROISelectionDialog.this.setVisible(false);
			}
		});
		this.list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(final ListSelectionEvent evt) {
				final int index = TSTrackROISelectionDialog.this.list
				        .getSelectedIndex();
				TSTrackROISelectionDialog.this.roi = TSTrackROISelectionDialog.this.availableROIs
				        .get(index);
			}
		});
	}

	private void createAndSetListModel() {
		final ListModel<String> model = new DefaultListModel<String>() {
			private static final long serialVersionUID = 1L;

			@Override
			public int getSize() {
				return TSTrackROISelectionDialog.this.availableROIs.size();
			}

			@Override
			public String getElementAt(final int index) {
				final OmegaROI roi = TSTrackROISelectionDialog.this.availableROIs
				        .get(index);
				final String x = new BigDecimal(String.valueOf(roi.getX()))
				        .setScale(2, RoundingMode.HALF_UP).toString();
				final String y = new BigDecimal(String.valueOf(roi.getY()))
				        .setScale(2, RoundingMode.HALF_UP).toString();
				final StringBuffer buf = new StringBuffer();
				buf.append(TSConstants.ROI_SELECT_FRAMEINDEX);
				buf.append(roi.getFrameIndex() + 1);
				buf.append(TSConstants.ROI_SELECT_X);
				buf.append(x);
				buf.append(TSConstants.ROI_SELECT_Y);
				buf.append(y);
				return buf.toString();
			}

			@Override
			public void addListDataListener(final ListDataListener l) {
				// TODO Auto-generated method stub

			}

			@Override
			public void removeListDataListener(final ListDataListener l) {
				// TODO Auto-generated method stub

			}

		};
		this.list.setModel(model);
	}

	public OmegaROI getSelectedROI() {
		return this.roi;
	}
}
