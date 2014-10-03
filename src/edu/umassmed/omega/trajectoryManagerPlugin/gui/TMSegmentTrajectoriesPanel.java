package edu.umassmed.omega.trajectoryManagerPlugin.gui;

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
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.RootPaneContainer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.umassmed.omega.commons.gui.GenericPanel;
import edu.umassmed.omega.dataNew.trajectoryElements.OmegaROI;
import edu.umassmed.omega.dataNew.trajectoryElements.OmegaSegment;
import edu.umassmed.omega.dataNew.trajectoryElements.OmegaSegmentationType;
import edu.umassmed.omega.dataNew.trajectoryElements.OmegaSegmentationTypes;
import edu.umassmed.omega.dataNew.trajectoryElements.OmegaTrajectory;

public class TMSegmentTrajectoriesPanel extends GenericPanel {

	private static final long serialVersionUID = -6876397782525067201L;

	private static final String ACTUAL_SEGM = "Actual segmentation: ";

	private final TMPluginPanel pluginPanel;

	private final List<TMSegmentSingleTrajectoryPanel> segmentTrajectoryPanels;
	private final List<JScrollPane> segmentTrajectoryScrollPane;

	private final List<JRadioButton> segm_butt;
	private OmegaSegmentationTypes segmTypes;

	private JTabbedPane mainPanel;
	private JPanel radioButtonPanel;
	private ActionListener radioButton_al;

	private JCheckBox autoscale_cbox;
	private JButton selectStart_btt, selectEnd_btt, reset_btt;

	private JLabel segment_lbl;

	private String segmentationName;
	private boolean segmentationEnded;
	private double pixelSizeX, pixelSizeY;

	private int actualPanelIndex;

	public TMSegmentTrajectoriesPanel(final RootPaneContainer parent,
	        final TMPluginPanel pluginPanel,
	        final OmegaSegmentationTypes segmTypes) {
		super(parent);
		this.segmTypes = segmTypes;
		this.pluginPanel = pluginPanel;

		this.pixelSizeX = -1;
		this.pixelSizeY = -1;

		this.segm_butt = new ArrayList<JRadioButton>();
		this.segmentTrajectoryPanels = new ArrayList<TMSegmentSingleTrajectoryPanel>();
		this.segmentTrajectoryScrollPane = new ArrayList<JScrollPane>();

		this.segmentationName = OmegaSegmentationTypes.NOT_ASSIGNED;

		this.actualPanelIndex = -1;
		this.segmentationEnded = true;

		this.setLayout(new BorderLayout());

		this.createAndAddWidgets();

		this.addListeners();
	}

	private void createAndAddWidgets() {

		final JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());

		final JPanel optionsPanel = new JPanel();
		optionsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

		this.autoscale_cbox = new JCheckBox("Trajectories autoscale");
		this.autoscale_cbox.setSelected(true);
		optionsPanel.add(this.autoscale_cbox);

		final Dimension btt_dim = new Dimension(150, 20);

		this.selectStart_btt = new JButton("Select traj start");
		this.selectStart_btt.setPreferredSize(btt_dim);
		this.selectStart_btt.setSize(btt_dim);
		optionsPanel.add(this.selectStart_btt);

		this.selectEnd_btt = new JButton("Select traj end");
		this.selectEnd_btt.setPreferredSize(btt_dim);
		this.selectEnd_btt.setSize(btt_dim);
		optionsPanel.add(this.selectEnd_btt);

		this.reset_btt = new JButton("Reset segmentation");
		this.reset_btt.setPreferredSize(btt_dim);
		this.reset_btt.setSize(btt_dim);
		optionsPanel.add(this.reset_btt);

		this.radioButtonPanel = new JPanel();
		this.radioButtonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.createAndAddSegmentationTypesRadioButtons();

		topPanel.add(optionsPanel, BorderLayout.NORTH);
		topPanel.add(this.radioButtonPanel, BorderLayout.SOUTH);

		this.add(topPanel, BorderLayout.NORTH);

		this.mainPanel = new JTabbedPane();

		this.add(this.mainPanel, BorderLayout.CENTER);

		this.segment_lbl = new JLabel(TMSegmentTrajectoriesPanel.ACTUAL_SEGM
		        + " none.");
		this.add(this.segment_lbl, BorderLayout.SOUTH);
	}

	private void createAndAddSegmentationTypesRadioButtons() {
		final ButtonGroup buttonGroup = new ButtonGroup();
		for (final OmegaSegmentationType segmType : this.segmTypes.getTypes()) {
			final JRadioButton butt = new JRadioButton(segmType.getName());
			butt.setForeground(segmType.getColor());
			butt.addActionListener(this.getRadioButtonActionListener());
			buttonGroup.add(butt);
			if (segmType.getValue() == OmegaSegmentationTypes.NOT_ASSIGNED_VAL) {
				butt.setSelected(true);
			}
			this.segm_butt.add(butt);
			this.radioButtonPanel.add(butt);
		}
	}

	private void removeSegmentationButtActionListener() {
		for (final JRadioButton btt : this.segm_butt) {
			btt.removeActionListener(this.radioButton_al);
		}
	}

	private void addListeners() {
		this.mainPanel.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(final ChangeEvent evt) {
				TMSegmentTrajectoriesPanel.this.handlePanelChanged();
			}
		});
		this.autoscale_cbox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				TMSegmentTrajectoriesPanel.this.handleAutoscaleChanged();
			}
		});
		this.selectStart_btt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				TMSegmentTrajectoriesPanel.this.handleSelectStart();
			}
		});
		this.selectEnd_btt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				TMSegmentTrajectoriesPanel.this.handleSelectEnd();
			}
		});
		this.reset_btt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				TMSegmentTrajectoriesPanel.this.handleSegmentationReset();
			}
		});
	}

	private void handleAutoscaleChanged() {
		final boolean autoscale = this.autoscale_cbox.isSelected();
		for (final TMSegmentSingleTrajectoryPanel singleTrajPanel : this.segmentTrajectoryPanels) {
			singleTrajPanel.setAutoscale(autoscale);
		}
	}

	private void handlePanelChanged() {
		this.actualPanelIndex = this.mainPanel.getSelectedIndex();
		System.out.println(this.actualPanelIndex);
	}

	private ActionListener getRadioButtonActionListener() {
		if (this.radioButton_al == null) {
			this.radioButton_al = new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent evt) {
					final JRadioButton butt = (JRadioButton) evt.getSource();
					TMSegmentTrajectoriesPanel.this.setSegmentationType(butt
					        .getText());
				}
			};
		}
		return this.radioButton_al;
	}

	private void setSegmentationType(final String segmName) {
		String s = null;
		if (this.segmentationEnded) {
			final StringBuffer buf = new StringBuffer();
			buf.append(TMSegmentTrajectoriesPanel.ACTUAL_SEGM);
			buf.append(segmName.toLowerCase());
			buf.append(".");
			s = buf.toString();
		} else {
			final String oldSegmName = this.segmentationName;
			s = this.segment_lbl.getText();
			s = s.replace(oldSegmName.toLowerCase(), segmName.toLowerCase());
		}
		this.updateSegmentationStatus(s);
		this.segmentationName = segmName;
		for (final TMSegmentSingleTrajectoryPanel panel : this.segmentTrajectoryPanels) {
			panel.setSegmentationType(segmName);
		}
	}

	public void createSegmentSingleTrajectoryPanels(
	        final List<OmegaTrajectory> trajs) {
		for (int i = 0; i < this.segmentTrajectoryScrollPane.size(); i++) {
			this.mainPanel.remove(this.segmentTrajectoryScrollPane.get(i));
			this.segmentTrajectoryScrollPane.set(i, null);
			// TODO remove all listeners
		}
		this.segmentTrajectoryScrollPane.clear();
		for (int i = 0; i < this.segmentTrajectoryPanels.size(); i++) {
			this.segmentTrajectoryPanels.set(i, null);
		}
		this.segmentTrajectoryPanels.clear();
		this.actualPanelIndex = -1;
		for (final OmegaTrajectory traj : trajs) {
			final TMSegmentSingleTrajectoryPanel panel = new TMSegmentSingleTrajectoryPanel(
			        this.getParentContainer(), this.pluginPanel,
			        this.pixelSizeX, this.pixelSizeY, traj,
			        this.autoscale_cbox.isSelected());
			final JScrollPane scrollPane = new JScrollPane(panel);
			this.segmentTrajectoryPanels.add(panel);
			this.segmentTrajectoryScrollPane.add(scrollPane);
			// TODO find a way to name panels
			final String s = "Segment Trajectory " + traj.getName();
			this.mainPanel.add(s, scrollPane);
			// panel.setTrajectory(traj);
			panel.setSegmentationType(this.segmentationName);
			panel.rescale();
		}
		this.mainPanel.repaint();
	}

	public void setRadius(final int radius) {
		for (final TMSegmentSingleTrajectoryPanel panel : this.segmentTrajectoryPanels) {
			panel.setRadius(radius);
		}
	}

	public void updateActualSegmentTrajectories(
	        final Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap) {
		for (final OmegaTrajectory traj : segmentsMap.keySet()) {
			System.out.println(traj.getName());
			for (final OmegaSegment segm : segmentsMap.get(traj)) {
				System.out.println("From "
				        + segm.getStartingROI().getFrameIndex() + " to "
				        + segm.getEndingROI().getFrameIndex() + " typ "
				        + segm.getSegmentationType());
			}
		}
		for (final TMSegmentSingleTrajectoryPanel panel : this.segmentTrajectoryPanels) {
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
		this.segm_butt.clear();
		this.radioButtonPanel.removeAll();
		this.createAndAddSegmentationTypesRadioButtons();
		this.revalidate();
		this.repaint();
	}

	public void setPixelSizes(final double pixelSizeX, final double pixelSizeY) {
		this.pixelSizeX = pixelSizeX;
		this.pixelSizeY = pixelSizeY;
	}

	private void handleSelectStart() {
		final TMSegmentSingleTrajectoryPanel singleTrajPanel = this.segmentTrajectoryPanels
		        .get(this.actualPanelIndex);
		singleTrajPanel.selectTrajectoryStart();
	}

	private void handleSelectEnd() {
		final TMSegmentSingleTrajectoryPanel singleTrajPanel = this.segmentTrajectoryPanels
		        .get(this.actualPanelIndex);
		singleTrajPanel.selectTrajectoryEnd();
	}

	private void handleSegmentationReset() {
		final TMSegmentSingleTrajectoryPanel singleTrajPanel = this.segmentTrajectoryPanels
		        .get(this.actualPanelIndex);
		singleTrajPanel.resetSegmentation();
		this.segmentationEnded = false;
		final StringBuffer buf = new StringBuffer();
		buf.append(TMSegmentTrajectoriesPanel.ACTUAL_SEGM);
		buf.append(this.segmentationName.toLowerCase());
		buf.append(".");
		this.updateSegmentationStatus(buf.toString());
	}

	public void selectStartingROI(final OmegaROI startingROI) {
		this.segmentationEnded = false;
		final StringBuffer buf = new StringBuffer();
		buf.append(TMSegmentTrajectoriesPanel.ACTUAL_SEGM);
		buf.append(this.segmentationName.toLowerCase());
		buf.append(" from ");
		buf.append(startingROI.getFrameIndex() + 1);
		buf.append(".");
		this.updateSegmentationStatus(buf.toString());
	}

	public void selectEndingROI(final OmegaROI endingROI) {
		this.segmentationEnded = true;
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
}
