package edu.umassmed.omega.trajectoriesSegmentationPlugin.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
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
import edu.umassmed.omega.commons.data.coreElements.OmegaImage;
import edu.umassmed.omega.commons.data.coreElements.OmegaImagePixels;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaROI;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaSegment;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaSegmentationType;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaSegmentationTypes;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaTrajectory;
import edu.umassmed.omega.commons.gui.GenericPanel;
import edu.umassmed.omega.trajectoriesSegmentationPlugin.TSConstants;

public class TSPanel extends GenericPanel {
	
	private static final long serialVersionUID = -6876397782525067201L;
	
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
	private JButton selectLast_btt, selectStart_btt, selectEnd_btt, reset_btt,
	        scaleToFit_btt, scale1on1_btt;
	
	private JLabel segment_lbl;
	
	private String segmentationName;
	private boolean segmentationEnded;
	private int sizeX, sizeY;
	private double pixelSizeX, pixelSizeY;
	
	private int currentPanelIndex;
	
	private boolean newPanels;
	
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
		
		this.newPanels = false;
		
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
		
		this.scaleToFit_btt = new JButton(TSConstants.SCALE_FIT);
		this.scaleToFit_btt.setPreferredSize(btt_dim);
		this.scaleToFit_btt.setSize(btt_dim);
		optionsPanel.add(this.scaleToFit_btt);
		
		this.scale1on1_btt = new JButton(TSConstants.SCALE_ONE);
		this.scale1on1_btt.setPreferredSize(btt_dim);
		this.scale1on1_btt.setSize(btt_dim);
		optionsPanel.add(this.scale1on1_btt);
		
		// this.autoscale_cbox = new JCheckBox("Trajectories autoscale");
		// this.autoscale_cbox.setSelected(true);
		// optionsPanel.add(this.autoscale_cbox);
		
		this.selectStart_btt = new JButton(TSConstants.SELECT_TRACK_START);
		this.selectStart_btt.setToolTipText(TSConstants.SELECT_TRACK_START_MS);
		this.selectStart_btt.setPreferredSize(btt_dim);
		this.selectStart_btt.setSize(btt_dim);
		optionsPanel.add(this.selectStart_btt);
		
		this.selectEnd_btt = new JButton(TSConstants.SELECT_TRACK_END);
		this.selectEnd_btt.setToolTipText(TSConstants.SELECT_TRACK_END_MS);
		this.selectEnd_btt.setPreferredSize(btt_dim);
		this.selectEnd_btt.setSize(btt_dim);
		optionsPanel.add(this.selectEnd_btt);
		
		this.selectLast_btt = new JButton(TSConstants.SELECT_TRACK_LAST);
		this.selectLast_btt.setToolTipText(TSConstants.SELECT_TRACK_LAST_MS);
		this.selectLast_btt.setPreferredSize(btt_dim);
		this.selectLast_btt.setSize(btt_dim);
		optionsPanel.add(this.selectLast_btt);
		
		this.reset_btt = new JButton(TSConstants.SELECT_RESET);
		this.reset_btt.setToolTipText(TSConstants.SELECT_RESET_MS);
		this.reset_btt.setPreferredSize(btt_dim);
		this.reset_btt.setSize(btt_dim);
		optionsPanel.add(this.reset_btt);
		
		final JLabel lbl = new JLabel(TSConstants.SELECT);
		optionsPanel.add(lbl);
		
		this.segmOn_cb = new JComboBox<String>();
		this.segmOn_cb.setToolTipText(TSConstants.SELECT_FIRST_MS);
		this.segmOn_cb.addItem(TSConstants.SELECT_FIRST_SPOTS);
		this.segmOn_cb.addItem(TSConstants.SELECT_FIRST_MOTION);
		optionsPanel.add(this.segmOn_cb);
		
		this.radioButtonPanel = new JPanel();
		this.radioButtonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.createAndAddSegmentationTypesRadioButtons();
		
		topPanel.add(optionsPanel, BorderLayout.NORTH);
		topPanel.add(this.radioButtonPanel, BorderLayout.SOUTH);
		
		this.add(topPanel, BorderLayout.NORTH);
		
		this.mainPanel = new JTabbedPane();
		
		this.add(this.mainPanel, BorderLayout.CENTER);
		
		this.segment_lbl = new JLabel(TSConstants.ACTUAL_SEGM
		        + TSConstants.SELECT_NONE);
		this.add(this.segment_lbl, BorderLayout.SOUTH);
	}
	
	private void createAndAddSegmentationTypesRadioButtons() {
		this.buttonGroup = new ButtonGroup();
		for (final OmegaSegmentationType segmType : this.segmTypes.getTypes()) {
			final JRadioButton butt = new JRadioButton(segmType.getName());
			butt.setForeground(segmType.getColor());
			butt.addActionListener(this.getRadioButtonActionListener());
			if ((segmType.getDescription() != null)
					&& !segmType.getDescription().isEmpty()) {
				butt.setToolTipText("<html><p width=\"500\">"
						+ segmType.getDescription() + "</p></html>");
			}
			// butt.setToolTipText(segmType.getDescription());
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
		this.selectLast_btt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				TSPanel.this.handleSelectLast();
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
				this.setSegmentationType(this.segm_btt.get(0).getText());
			} else {
				trackPanel.segmentOnMotionSelection();
				this.buttonGroup.clearSelection();
				this.setSegmentationType(null);
			}
			
		}
	}

	@Override
	public void paint(final Graphics g) {
		super.paint(g);
		if (this.newPanels) {
			this.handleAutoscale();
			this.newPanels = false;
		}
	}
	
	public void handleAutoscale() {
		for (final TSTrackPanel singleTrajPanel : this.segmentTrajectoryPanels) {
			singleTrajPanel.setInitialScale();
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
		if (this.currentPanelIndex == -1)
			return;
		final TSTrackPanel singleTrajPanel = this.segmentTrajectoryPanels
		        .get(this.currentPanelIndex);
		String segm = "Nothing selected";
		if (segmName != null) {
			segm = segmName;
		}
		String s = null;
		if (this.segmentationEnded) {
			final StringBuffer buf = new StringBuffer();
			buf.append(TSConstants.ACTUAL_SEGM);
			if (this.isSegmentOnSpotsSelection()) {
				buf.append(segm);
				buf.append(TSConstants.SELECT_PUNCT);
				s = buf.toString();
			} else {
				buf.append(segm);
				buf.append(TSConstants.SELECT_PUNCT);
				s = buf.toString();
			}
		} else {
			if (this.isSegmentOnSpotsSelection()) {
				final String oldSegmName = this.segmentationName;
				s = this.segment_lbl.getText();
				s = s.replace(oldSegmName, segm);
			} else {
				s = this.segment_lbl.getText();
				s = s.replace(TSConstants.SELECT_FROM_UPPER, segm
						+ TSConstants.SELECT_FROM_LOWER);
			}
		}
		this.updateSegmentationStatus(s);
		this.segmentationName = segm;
		int segmType = -1;
		if (segmName != null) {
			segmType = this.segmTypes.getSegmentationValue(segm);
		}
		for (final TSTrackPanel panel : this.segmentTrajectoryPanels) {
			panel.setSegmentationType(segmType);
		}
		if (!this.isSegmentOnSpotsSelection()) {
			this.buttonGroup.clearSelection();
			singleTrajPanel.segmentTrajectory();
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
			int sizeX = this.sizeX;
			int sizeY = this.sizeY;
			if ((sizeX == -1) && (sizeY == -1)) {
				for (final OmegaROI roi : traj.getROIs()) {
					final int x = (int) (roi.getX() + 1);
					final int y = (int) (roi.getY() + 1);
					if (sizeX < x) {
						sizeX = x;
					}
					if (sizeY < y) {
						sizeY = y;
					}
				}
			}
			final TSTrackPanel panel = new TSTrackPanel(
			        this.getParentContainer(), this.pluginPanel, sizeX, sizeY,
			        this.pixelSizeX, this.pixelSizeY, traj,
			        segmentationResults.get(traj));
			this.newPanels = true;
			// final JScrollPane scrollPane = new JScrollPane(panel);
			this.segmentTrajectoryPanels.add(panel);
			// this.segmentTrajectoryScrollPane.add(scrollPane);
			// TODO find a way to name panels
			final String s = TSConstants.SEGMENT_CONFIRM + traj.getName();
			// this.mainPanel.add(s, scrollPane);
			this.mainPanel.add(s, panel);
			// panel.setTrajectory(traj);
			panel.setSegmentationType(this.segmTypes
			        .getSegmentationValue(this.segmentationName));
			this.handleSegmentationOptionChanged();
		}
		this.currentPanelIndex = 0;
		this.revalidate();
		this.repaint();
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

	private void handleSelectLast() {
		final TSTrackPanel singleTrajPanel = this.segmentTrajectoryPanels
		        .get(this.currentPanelIndex);
		singleTrajPanel.selectTrajectoryLast();
	}
	
	private void handleSegmentationReset() {
		final TSTrackPanel singleTrajPanel = this.segmentTrajectoryPanels
		        .get(this.currentPanelIndex);
		singleTrajPanel.resetSegmentation();
		this.segmentationEnded = false;
		final StringBuffer buf = new StringBuffer();
		buf.append(TSConstants.ACTUAL_SEGM);
		buf.append(this.segmentationName.toLowerCase());
		buf.append(TSConstants.SELECT_PUNCT);
		this.updateSegmentationStatus(buf.toString());
	}
	
	public void selectStartingROI(final OmegaROI startingROI) {
		this.segmentationEnded = false;
		final StringBuffer buf = new StringBuffer();
		buf.append(TSConstants.ACTUAL_SEGM);
		if (this.isSegmentOnSpotsSelection()) {
			buf.append(this.segmentationName);
			buf.append(TSConstants.SELECT_FROM_LOWER_SPACE);
		} else {
			buf.append(TSConstants.SELECT_FROM_UPPER_SPACE);
		}
		buf.append(startingROI.getFrameIndex());
		buf.append(TSConstants.SELECT_PUNCT);
		this.updateSegmentationStatus(buf.toString());
	}
	
	public void selectEndingROI(final OmegaROI endingROI) {
		final StringBuffer buf = new StringBuffer();
		buf.append(this.segment_lbl.getText().replace(TSConstants.SELECT_PUNCT,
		        ""));
		buf.append(TSConstants.SELECT_TO_LOWER_SPACE);
		buf.append(endingROI.getFrameIndex());
		buf.append(TSConstants.SELECT_PUNCT);
		this.updateSegmentationStatus(buf.toString());
	}
	
	private void updateSegmentationStatus(final String s) {
		this.segment_lbl.setText(s);
		this.segment_lbl.revalidate();
		this.segment_lbl.repaint();
	}
	
	public void setImage(final OmegaImage image) {
		if (image == null)
			return;
		final OmegaImagePixels pixels = image.getDefaultPixels();
		this.pixelSizeX = pixels.getPhysicalSizeX();
		this.pixelSizeY = pixels.getPhysicalSizeY();
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
		if (segmOn.equals(TSConstants.SELECT_FIRST_SPOTS))
			return true;
		return false;
	}

	protected List<TSTrackPanel> getTrackPanels() {
		return this.segmentTrajectoryPanels;
	}
}
