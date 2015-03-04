package edu.umassmed.omega.trajectoriesSegmentationPlugin.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.RootPaneContainer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.umassmed.omega.commons.constants.OmegaConstants;
import edu.umassmed.omega.commons.gui.GenericPanel;
import edu.umassmed.omega.data.coreElements.OmegaImage;
import edu.umassmed.omega.data.coreElements.OmegaImagePixels;
import edu.umassmed.omega.data.trajectoryElements.OmegaROI;
import edu.umassmed.omega.data.trajectoryElements.OmegaSegment;
import edu.umassmed.omega.data.trajectoryElements.OmegaSegmentationType;
import edu.umassmed.omega.data.trajectoryElements.OmegaSegmentationTypes;
import edu.umassmed.omega.data.trajectoryElements.OmegaTrajectory;

public class TSPanel extends GenericPanel {

	private static final long serialVersionUID = -6876397782525067201L;
	private static final String SELECTION_FIRST_MOTION = "Spots";
	private static final String SELECTION_FIRST_SPOTS = "Motion type";

	private static final String ACTUAL_SEGM = "Current selection: ";

	private final TSPluginPanel pluginPanel;

	private final List<TSTrackPanel> segmentTrajectoryPanels;
	// private final List<JScrollPane> segmentTrajectoryScrollPane;

	private final List<JRadioButton> segm_btt;
	private ButtonGroup buttonGroup;
	private OmegaSegmentationTypes segmTypes;

	private JTabbedPane mainPanel;
	private JPanel radioButtonPanel;
	private ActionListener radioButton_al;

	private JComboBox<String> segmOn_cb;
	private JButton selectStart_btt, selectEnd_btt, reset_btt, scaleToFit_btt,
	        scale1on1_btt;

	private JLabel segment_lbl;

	private String segmentationName;
	private boolean segmentationEnded;
	private int sizeX, sizeY;
	private double pixelSizeX, pixelSizeY;

	private int currentPanelIndex;

	public TSPanel(final RootPaneContainer parent,
	        final TSPluginPanel pluginPanel,
	        final OmegaSegmentationTypes segmTypes) {
		super(parent);
		this.segmTypes = segmTypes;
		this.pluginPanel = pluginPanel;

		this.pixelSizeX = -1;
		this.pixelSizeY = -1;
		this.sizeX = -1;
		this.sizeY = -1;

		this.segm_btt = new ArrayList<JRadioButton>();
		this.segmentTrajectoryPanels = new ArrayList<>();
		// this.segmentTrajectoryScrollPane = new ArrayList<>();

		this.segmentationName = OmegaSegmentationTypes.NOT_ASSIGNED;

		this.currentPanelIndex = -1;
		this.segmentationEnded = true;

		this.setLayout(new BorderLayout());

		this.createAndAddWidgets();

		this.addListeners();
	}

	private void createAndAddWidgets() {
		final Dimension btt_dim = OmegaConstants.BUTTON_SIZE;
		final JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());

		final JPanel optionsPanel = new JPanel();
		optionsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

		this.scaleToFit_btt = new JButton("Scale to fit");
		this.scaleToFit_btt.setPreferredSize(btt_dim);
		this.scaleToFit_btt.setSize(btt_dim);
		optionsPanel.add(this.scaleToFit_btt);

		this.scale1on1_btt = new JButton("Scale 1:1");
		this.scale1on1_btt.setPreferredSize(btt_dim);
		this.scale1on1_btt.setSize(btt_dim);
		optionsPanel.add(this.scale1on1_btt);

		// this.autoscale_cbox = new JCheckBox("Trajectories autoscale");
		// this.autoscale_cbox.setSelected(true);
		// optionsPanel.add(this.autoscale_cbox);

		this.selectStart_btt = new JButton("Select tracks start");
		this.selectStart_btt.setPreferredSize(btt_dim);
		this.selectStart_btt.setSize(btt_dim);
		optionsPanel.add(this.selectStart_btt);

		this.selectEnd_btt = new JButton("Select tracks end");
		this.selectEnd_btt.setPreferredSize(btt_dim);
		this.selectEnd_btt.setSize(btt_dim);
		optionsPanel.add(this.selectEnd_btt);

		this.reset_btt = new JButton("Reset selection");
		this.reset_btt.setPreferredSize(btt_dim);
		this.reset_btt.setSize(btt_dim);
		optionsPanel.add(this.reset_btt);

		final JLabel lbl = new JLabel("Select: ");
		optionsPanel.add(lbl);

		this.segmOn_cb = new JComboBox<String>();
		this.segmOn_cb.addItem(TSPanel.SELECTION_FIRST_SPOTS);
		this.segmOn_cb.addItem(TSPanel.SELECTION_FIRST_MOTION);
		optionsPanel.add(this.segmOn_cb);

		this.radioButtonPanel = new JPanel();
		this.radioButtonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.createAndAddSegmentationTypesRadioButtons();

		topPanel.add(optionsPanel, BorderLayout.NORTH);
		topPanel.add(this.radioButtonPanel, BorderLayout.SOUTH);

		this.add(topPanel, BorderLayout.NORTH);

		this.mainPanel = new JTabbedPane();

		this.add(this.mainPanel, BorderLayout.CENTER);

		this.segment_lbl = new JLabel(TSPanel.ACTUAL_SEGM + " none.");
		this.add(this.segment_lbl, BorderLayout.SOUTH);
	}

	private void createAndAddSegmentationTypesRadioButtons() {
		this.buttonGroup = new ButtonGroup();
		for (final OmegaSegmentationType segmType : this.segmTypes.getTypes()) {
			final JRadioButton butt = new JRadioButton(segmType.getName());
			butt.setForeground(segmType.getColor());
			butt.addActionListener(this.getRadioButtonActionListener());
			this.buttonGroup.add(butt);
			if (segmType.getValue() == OmegaSegmentationTypes.NOT_ASSIGNED_VAL) {
				butt.setSelected(true);
			}
			this.segm_btt.add(butt);
			this.radioButtonPanel.add(butt);
		}
	}

	private void removeSegmentationButtActionListener() {
		for (final JRadioButton btt : this.segm_btt) {
			btt.removeActionListener(this.radioButton_al);
		}
	}

	private void addListeners() {
		this.segmOn_cb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				TSPanel.this.handleSegmentationOptionChanged();
			}
		});
		this.mainPanel.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(final ChangeEvent evt) {
				TSPanel.this.handlePanelChanged();
			}
		});
		this.scaleToFit_btt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				TSPanel.this.handleAutoscaleChanged(true);
			}
		});
		this.scale1on1_btt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				TSPanel.this.handleAutoscaleChanged(false);
			}
		});
		this.selectStart_btt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				TSPanel.this.handleSelectStart();
			}
		});
		this.selectEnd_btt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				TSPanel.this.handleSelectEnd();
			}
		});
		this.reset_btt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				TSPanel.this.handleSegmentationReset();
			}
		});
	}

	private void handleSegmentationOptionChanged() {
		for (final TSTrackPanel trackPanel : this.segmentTrajectoryPanels) {
			if (this.isSegmentOnSpotsSelection()) {
				trackPanel.segmentOnROISelection();
				this.segm_btt.get(0).setSelected(true);
			} else {
				trackPanel.segmentOnMotionSelection();
				this.buttonGroup.clearSelection();
			}

		}
	}

	private void handleAutoscaleChanged(final boolean scaleToFit) {
		for (final TSTrackPanel singleTrajPanel : this.segmentTrajectoryPanels) {
			if (scaleToFit) {
				singleTrajPanel.setScaleToFit();
			} else {
				singleTrajPanel.setScaleOneOne();
			}
		}
	}

	private void handlePanelChanged() {
		this.currentPanelIndex = this.mainPanel.getSelectedIndex();
	}

	private ActionListener getRadioButtonActionListener() {
		if (this.radioButton_al == null) {
			this.radioButton_al = new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent evt) {
					final JRadioButton butt = (JRadioButton) evt.getSource();
					TSPanel.this.setSegmentationType(butt.getText());
				}
			};
		}
		return this.radioButton_al;
	}

	private void setSegmentationType(final String segmName) {
		String s = null;
		if (this.segmentationEnded) {
			if (this.isSegmentOnSpotsSelection()) {
				final StringBuffer buf = new StringBuffer();
				buf.append(TSPanel.ACTUAL_SEGM);
				buf.append(segmName);
				buf.append(".");
				s = buf.toString();
			}
		} else {
			if (this.isSegmentOnSpotsSelection()) {
				final String oldSegmName = this.segmentationName;
				s = this.segment_lbl.getText();
				s = s.replace(oldSegmName, segmName);
			} else {
				s = this.segment_lbl.getText();
				s = s.replace("From", segmName + " from");
			}
		}
		this.updateSegmentationStatus(s);
		this.segmentationName = segmName;
		final int segmType = this.segmTypes.getSegmentationValue(segmName);
		for (final TSTrackPanel panel : this.segmentTrajectoryPanels) {
			panel.setSegmentationType(segmType);
		}
		final TSTrackPanel singleTrajPanel = this.segmentTrajectoryPanels
		        .get(this.currentPanelIndex);
		if (!this.isSegmentOnSpotsSelection()) {
			singleTrajPanel.segmentTrajectory();
			this.buttonGroup.clearSelection();
		}
	}

	public void destroySegmentSingleTrajectoryPanels() {
		for (int i = 0; i < this.segmentTrajectoryPanels.size(); i++) {
			this.mainPanel.remove(this.segmentTrajectoryPanels.get(i));
			this.segmentTrajectoryPanels.set(i, null);
		}
		this.segmentTrajectoryPanels.clear();
		this.currentPanelIndex = -1;
		this.mainPanel.repaint();
	}

	public void createSegmentSingleTrajectoryPanels(
	        final Map<OmegaTrajectory, List<OmegaSegment>> segmentationResults) {
		// for (int i = 0; i < this.segmentTrajectoryScrollPane.size(); i++) {
		// this.mainPanel.remove(this.segmentTrajectoryScrollPane.get(i));
		// this.segmentTrajectoryScrollPane.set(i, null);
		// // TODO remove all listeners
		// }
		// this.segmentTrajectoryScrollPane.clear();
		this.destroySegmentSingleTrajectoryPanels();
		if (segmentationResults == null)
			return;
		for (final OmegaTrajectory traj : segmentationResults.keySet()) {
			// final TSSingleTrajectoryPanel panel = new
			// TSSingleTrajectoryPanel(
			// this.getParentContainer(), this.pluginPanel,
			// this.sizeX, this.sizeY, this.pixelSizeX,
			// this.pixelSizeY, traj);
			final TSTrackPanel panel = new TSTrackPanel(
			        this.getParentContainer(), this.pluginPanel, this.sizeX,
			        this.sizeY, this.pixelSizeX, this.pixelSizeY, traj,
			        segmentationResults.get(traj));
			// final JScrollPane scrollPane = new JScrollPane(panel);
			this.segmentTrajectoryPanels.add(panel);
			// this.segmentTrajectoryScrollPane.add(scrollPane);
			// TODO find a way to name panels
			final String s = "Segment Trajectory " + traj.getName();
			// this.mainPanel.add(s, scrollPane);
			this.mainPanel.add(s, panel);
			// panel.setTrajectory(traj);
			panel.setSegmentationType(this.segmTypes
			        .getSegmentationValue(this.segmentationName));
			// panel.rescale();
		}
		this.currentPanelIndex = 0;
		this.mainPanel.repaint();
	}

	public void setRadius(final int radius) {
		for (final TSTrackPanel panel : this.segmentTrajectoryPanels) {
			panel.setRadius(radius);
		}
	}

	public void updateCurrentSegmentTrajectories(
	        final Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap) {
		// for (final OmegaTrajectory traj : segmentsMap.keySet()) {
		// System.out.println(traj.getName());
		// for (final OmegaSegment segm : segmentsMap.get(traj)) {
		// System.out.println("From "
		// + segm.getStartingROI().getFrameIndex() + " to "
		// + segm.getEndingROI().getFrameIndex() + " typ "
		// + segm.getSegmentationType());
		// }
		// }
		for (final TSTrackPanel panel : this.segmentTrajectoryPanels) {
			if (segmentsMap == null) {
				panel.updateSegmentationResults(null);
				continue;
			}
			final OmegaTrajectory traj = panel.getTrajectory();
			final List<OmegaSegment> segments = segmentsMap.get(traj);
			panel.updateSegmentationResults(segments);
		}
	}

	public void setSegmentationTypes(final OmegaSegmentationTypes segmTypes) {
		this.segmTypes = segmTypes;
		this.removeSegmentationButtActionListener();
		this.segm_btt.clear();
		this.radioButtonPanel.removeAll();
		this.createAndAddSegmentationTypesRadioButtons();
		this.revalidate();
		this.repaint();
	}

	private void handleSelectStart() {
		final TSTrackPanel singleTrajPanel = this.segmentTrajectoryPanels
		        .get(this.currentPanelIndex);
		singleTrajPanel.selectTrajectoryStart();
	}

	private void handleSelectEnd() {
		final TSTrackPanel singleTrajPanel = this.segmentTrajectoryPanels
		        .get(this.currentPanelIndex);
		singleTrajPanel.selectTrajectoryEnd();
	}

	private void handleSegmentationReset() {
		final TSTrackPanel singleTrajPanel = this.segmentTrajectoryPanels
		        .get(this.currentPanelIndex);
		singleTrajPanel.resetSegmentation();
		this.segmentationEnded = false;
		final StringBuffer buf = new StringBuffer();
		buf.append(TSPanel.ACTUAL_SEGM);
		buf.append(this.segmentationName.toLowerCase());
		buf.append(".");
		this.updateSegmentationStatus(buf.toString());
	}

	public void selectStartingROI(final OmegaROI startingROI) {
		this.segmentationEnded = false;
		final StringBuffer buf = new StringBuffer();
		buf.append(TSPanel.ACTUAL_SEGM);
		if (this.isSegmentOnSpotsSelection()) {
			buf.append(this.segmentationName);
			buf.append(" from ");
		} else {
			buf.append("From ");
		}
		buf.append(startingROI.getFrameIndex() + 1);
		buf.append(".");
		this.updateSegmentationStatus(buf.toString());
	}

	public void selectEndingROI(final OmegaROI endingROI) {
		final StringBuffer buf = new StringBuffer();
		buf.append(this.segment_lbl.getText().replace(".", ""));
		buf.append(" to ");
		buf.append(endingROI.getFrameIndex() + 1);
		buf.append(".");
		this.updateSegmentationStatus(buf.toString());
	}

	private void updateSegmentationStatus(final String s) {
		this.segment_lbl.setText(s);
		this.segment_lbl.revalidate();
		this.segment_lbl.repaint();
	}

	public void setImage(final OmegaImage image) {
		final OmegaImagePixels pixels = image.getDefaultPixels();
		this.pixelSizeX = pixels.getPixelSizeX();
		this.pixelSizeY = pixels.getPixelSizeY();
		this.sizeX = pixels.getSizeX();
		this.sizeY = pixels.getSizeY();
	}

	public void resetSegmentation() {
		this.destroySegmentSingleTrajectoryPanels();
	}

	@Override
	public void updateParentContainer(final RootPaneContainer parent) {
		super.updateParentContainer(parent);
		for (final TSTrackPanel trackPanel : this.segmentTrajectoryPanels) {
			trackPanel.updateParentContainer(parent);
		}
	}

	public void setSegmentationEnded() {
		this.segmentationEnded = true;
	}

	public boolean isSegmentOnSpotsSelection() {
		final String segmOn = (String) this.segmOn_cb.getSelectedItem();
		if (segmOn.equals(TSPanel.SELECTION_FIRST_SPOTS))
			return true;
		return false;
	}
}
