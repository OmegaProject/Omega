package unused.trajectoryManagerPlugin.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.RootPaneContainer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import edu.umassmed.omega.commons.constants.OmegaConstants;
import edu.umassmed.omega.commons.gui.GenericPanel;
import edu.umassmed.omega.data.trajectoryElements.OmegaTrajectory;

public class TMCurrentTrajectoryInformation extends GenericPanel {
	private static final int ARROW_BTT_WIDTH = 50;
	private static final int BTT_WIDTH = 100;
	private static final int HEIGHT = 20;
	private static final int NAME_PERC = 40;

	private static final long serialVersionUID = -3000818642933786305L;

	private List<OmegaTrajectory> selectedTrajectories;

	private JPanel mainPanel;
	private JTextField name_txt;
	private JLabel info_lbl;
	private JButton save_btt, editNote_btt;
	private JButton left_btt, right_btt;

	private int currentIndex, oldMainPanelWidth;

	private final TMPluginPanel pluginPanel;

	public TMCurrentTrajectoryInformation(
	        final RootPaneContainer parentContainer,
	        final TMPluginPanel pluginPanel) {
		super(parentContainer);
		this.pluginPanel = pluginPanel;
		this.oldMainPanelWidth = 0;
		this.currentIndex = -1;
		this.selectedTrajectories = null;

		this.setLayout(new BorderLayout());

		this.createAndAddWidgets();
		this.addListeners();
	}

	private void createAndAddWidgets() {

		final Dimension btt_dim = new Dimension(
		        TMCurrentTrajectoryInformation.ARROW_BTT_WIDTH,
		        TMCurrentTrajectoryInformation.HEIGHT);
		this.left_btt = new JButton("<");
		// this.left_btt.setIcon(this.createArrowIcon(SwingConstants.LEFT));
		this.left_btt.setPreferredSize(btt_dim);
		this.left_btt.setSize(btt_dim);
		this.add(this.left_btt, BorderLayout.WEST);

		this.right_btt = new JButton(">");
		// this.right_btt.setIcon(this.createArrowIcon(SwingConstants.RIGHT));
		this.right_btt.setPreferredSize(btt_dim);
		this.right_btt.setSize(btt_dim);
		this.add(this.right_btt, BorderLayout.EAST);

		this.mainPanel = new JPanel();
		this.mainPanel.setLayout(new FlowLayout(FlowLayout.LEADING));

		this.name_txt = new JTextField();
		// TODO sizes
		this.mainPanel.add(this.name_txt);

		this.info_lbl = new JLabel();
		// TODO sizes

		final Dimension btt2_dim = new Dimension(
		        TMCurrentTrajectoryInformation.BTT_WIDTH,
		        TMCurrentTrajectoryInformation.HEIGHT);
		this.save_btt = new JButton("Save");
		this.save_btt.setPreferredSize(btt2_dim);
		this.save_btt.setSize(btt2_dim);
		this.save_btt.setEnabled(false);
		this.mainPanel.add(this.save_btt);

		this.editNote_btt = new JButton("Edit notes");
		this.editNote_btt.setPreferredSize(btt2_dim);
		this.editNote_btt.setSize(btt2_dim);
		this.editNote_btt.setEnabled(false);
		this.mainPanel.add(this.editNote_btt);

		this.mainPanel.add(this.info_lbl);

		this.add(this.mainPanel, BorderLayout.CENTER);
	}

	// private Icon createArrowIcon(final int direction) {
	// final BasicArrowButton button = new BasicArrowButton(direction);
	// final Dimension size = button.getPreferredSize();
	// final BufferedImage image = new BufferedImage(size.width, size.height,
	// BufferedImage.TYPE_INT_ARGB);
	// final Graphics2D g2d = image.createGraphics();
	// button.paint(g2d);
	// g2d.dispose();
	// final ImageIcon icon = new ImageIcon(image);
	// return icon;
	// }

	private void addListeners() {
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent evt) {
				TMCurrentTrajectoryInformation.this.resizeFields();
			}
		});
		this.left_btt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				TMCurrentTrajectoryInformation.this
				        .manageCurrentTrajectoryChange(-1);
			}
		});
		this.right_btt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				TMCurrentTrajectoryInformation.this
				        .manageCurrentTrajectoryChange(1);
			}
		});
		this.name_txt.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(final DocumentEvent evt) {
				TMCurrentTrajectoryInformation.this
				        .manageNameChanged(TMCurrentTrajectoryInformation.this.name_txt
				                .getText());
			}

			@Override
			public void insertUpdate(final DocumentEvent evt) {
				TMCurrentTrajectoryInformation.this
				        .manageNameChanged(TMCurrentTrajectoryInformation.this.name_txt
				                .getText());
			}

			@Override
			public void changedUpdate(final DocumentEvent evt) {
				System.out.println("test changed");
			}
		});
		this.save_btt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				TMCurrentTrajectoryInformation.this.manageSaveTrajName();
			}
		});
	}

	private void manageSaveTrajName() {
		this.save_btt.setEnabled(false);
		final OmegaTrajectory currentTraj = this.selectedTrajectories
		        .get(this.currentIndex);
		currentTraj.setName(this.name_txt.getText());
		currentTraj.setNameChanged(true);
		this.pluginPanel.handleTrajNameChanged();
	}

	private void manageNameChanged(final String newName) {
		// TODO insert control
		this.save_btt.setEnabled(true);
	}

	private void resizeFields() {
		final Dimension dim = this.mainPanel.getSize();
		final int width = dim.width - (OmegaConstants.BUTTON_SIZE.width * 2);
		if (this.oldMainPanelWidth == width)
			return;

		this.oldMainPanelWidth = width;
		final int name_width = (width / 100)
		        * TMCurrentTrajectoryInformation.NAME_PERC;
		final Dimension name_dim = new Dimension(name_width,
		        TMCurrentTrajectoryInformation.HEIGHT);
		this.name_txt.setPreferredSize(name_dim);
		this.name_txt.setSize(name_dim);

		final int info_width = width - name_width;
		final Dimension info_dim = new Dimension(info_width,
		        TMCurrentTrajectoryInformation.HEIGHT);
		this.info_lbl.setPreferredSize(info_dim);
		this.info_lbl.setSize(info_dim);

		this.revalidate();
		this.repaint();
	}

	private void manageCurrentTrajectoryChange(final int change) {
		if ((this.selectedTrajectories == null)
		        || this.selectedTrajectories.isEmpty())
			return;
		this.currentIndex += change;
		if (this.currentIndex < 0) {
			this.currentIndex = this.selectedTrajectories.size() - 1;
		} else if (this.currentIndex >= this.selectedTrajectories.size()) {
			this.currentIndex = 0;
		}
		this.setCurrentTrajectory();
	}

	private void setCurrentTrajectory() {
		if ((this.selectedTrajectories == null)
		        || this.selectedTrajectories.isEmpty())
			return;
		final OmegaTrajectory currentTraj = this.selectedTrajectories
		        .get(this.currentIndex);
		this.name_txt.setText(currentTraj.getName());
		this.info_lbl.setText(this.createInfoString(currentTraj));
		this.save_btt.setEnabled(false);
		this.repaint();
	}

	private String createInfoString(final OmegaTrajectory traj) {
		final StringBuffer buf = new StringBuffer();
		// TODO
		return buf.toString();
	}

	public void setSelectedTrajectories(
	        final List<OmegaTrajectory> selectedTrajectories) {
		this.selectedTrajectories = selectedTrajectories;
		this.currentIndex = 0;
		this.setCurrentTrajectory();
	}
}
