package edu.umassmed.omega.core.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.WindowConstants;

import edu.umassmed.omega.commons.OmegaPlugin;
import edu.umassmed.omega.commons.gui.GenericFrame;
import edu.umassmed.omega.core.OmegaApplication;

public class OmegaFrame extends JFrame {

	private static final long serialVersionUID = -4775204088456661307L;

	public static int SPLITPANEL_LEFT = 0;
	public static int SPLITPANEL_RIGHT = 1;
	public static String PROP_PLUGIN = "PluginSelected";
	public static String PROP_TOGGLEWINDOW = "ToggleWindow";

	private final OmegaApplication omegaApp;

	private final List<JFrame> separatedFrames;

	private TopPanel topPanel;
	private WorkspacePanel workspacePanel;
	private SidePanel sidePanel;

	private JMenuBar menu;
	private JMenu fileMenu, windowsMenu;
	private JMenuItem quitMItem;
	private JMenuItem attachEdetachAllWindows;

	private JSplitPane mainSplitPane;

	private JScrollPane leftScrollPane, rightScrollPane;

	private long pluginSelected;
	private boolean isAttached;

	public OmegaFrame(final OmegaApplication omegaApp) {
		this.omegaApp = omegaApp;

		this.setTitle("OMEGA - Open Microscopy Environment inteGrated Analysis");
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.getContentPane().setLayout(new BorderLayout());

		this.separatedFrames = new ArrayList<JFrame>();
		this.isAttached = true;

		this.createAndAddWidgets();
		this.createMenu();
		this.setJMenuBar(this.menu);

		this.addListeners();
	}

	public void initialize() {
		this.topPanel.initializePanel();
		this.workspacePanel.initializePanel();
		this.sidePanel.initializePanel();
	}

	private void createAndAddWidgets() {
		this.topPanel = new TopPanel(this);
		this.getContentPane().add(this.topPanel, BorderLayout.NORTH);

		this.mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
		this.getContentPane().add(this.mainSplitPane, BorderLayout.CENTER);

		this.workspacePanel = new WorkspacePanel(this);
		this.leftScrollPane = new JScrollPane(this.workspacePanel);
		this.mainSplitPane.setLeftComponent(this.leftScrollPane);

		this.sidePanel = new SidePanel(this);
		this.rightScrollPane = new JScrollPane(this.sidePanel);
		this.mainSplitPane.setRightComponent(this.rightScrollPane);

		this.setSplitPanelDividerLocation(0.75);

		// Display the window.
	}

	private void createMenu() {
		this.menu = new JMenuBar();

		this.fileMenu = new JMenu("File");
		this.quitMItem = new JMenuItem("Quit");
		this.fileMenu.add(this.quitMItem);

		this.windowsMenu = new JMenu("Window");
		this.attachEdetachAllWindows = new JMenuItem("Detach all windows");
		this.windowsMenu.add(this.attachEdetachAllWindows);

		final JMenu workspaceMenu = this.workspacePanel.getMenu();

		this.menu.add(this.fileMenu);
		this.menu.add(workspaceMenu);
		this.menu.add(this.windowsMenu);
	}

	private void addListeners() {
		this.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(final ComponentEvent evt) {
				OmegaFrame.this.mainSplitPane.setDividerLocation(0.75);
			}
		});
		this.quitMItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				OmegaFrame.this.quit();
			}
		});

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				OmegaFrame.this.quit();
			}
		});

		this.attachEdetachAllWindows.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				if (OmegaFrame.this.isAttached) {
					OmegaFrame.this.detachWindows();
				} else {
					OmegaFrame.this.attachWindows();
				}
			}
		});

		this.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(final PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(OmegaFrame.PROP_PLUGIN)) {
					final OmegaPlugin plugin = OmegaFrame.this.omegaApp
					        .getPlugin((long) evt.getNewValue());
					OmegaFrame.this.pluginSelected = (long) evt.getNewValue();
					OmegaFrame.this.workspacePanel.showPlugin(plugin);
				}
			}
		});
	}

	protected void setSplitPanelDividerLocation(final Double percentage) {
		this.mainSplitPane.setDividerLocation(0.75);
	}

	protected Dimension getDimensionDifference() {
		final Dimension windowDim = this.getSize();
		final Dimension spDim = this.sidePanel.getSize();
		final Dimension wpDim = this.workspacePanel.getSize();

		final Dimension diff = new Dimension();
		diff.width = windowDim.width - spDim.width - wpDim.width;
		diff.height = windowDim.height - spDim.height - wpDim.height;

		return diff;
	}

	protected Point getSideFramePosition() {
		final Point windowPos = this.getLocationOnScreen();
		final Dimension windowDim = this.getSize();
		final Dimension spDim = this.sidePanel.getSize();

		final Point workspaceFramePos = new Point();
		workspaceFramePos.x += windowPos.x + (windowDim.width - spDim.width);
		workspaceFramePos.y += windowPos.y + (windowDim.height - spDim.height)
		        + 9;
		return workspaceFramePos;
	}

	protected Point getWorkspaceFramePosition() {
		final Point windowPos = this.getLocationOnScreen();
		final Dimension windowDim = this.getSize();
		final Dimension wpDim = this.workspacePanel.getSize();

		final Point sideFramePos = new Point();
		sideFramePos.x += windowPos.x;
		sideFramePos.y += windowPos.y + (windowDim.height - wpDim.height) + 9;
		return sideFramePos;
	}

	protected Dimension getWorkspaceFrameDimension() {
		final Dimension wpDim = this.workspacePanel.getSize();

		wpDim.width += 28;

		return wpDim;
	}

	protected Dimension getDetachedNewDimension() {
		final Dimension windowDim = this.getSize();
		final Dimension wpDim = this.workspacePanel.getSize();

		windowDim.height -= wpDim.height + 28;

		return windowDim;
	}

	protected Dimension getAttachedNewDimension() {
		final Dimension windowDim = this.getSize();
		final Dimension wpDim = this.workspacePanel.getSize();

		windowDim.height += wpDim.height + 28;

		return windowDim;
	}

	public void detachWindows() {
		this.mainSplitPane.setLeftComponent(null);
		this.mainSplitPane.setRightComponent(null);
		// this.leftScrollPane.remove(this.workspacePanel);
		// this.rightScrollPane.remove(this.sidePanel);

		this.attachEdetachAllWindows.setText("Attach all windows");
		final GenericFrame sideFrame = new GenericFrame(this, this.sidePanel,
		        "Sidebar", this.getSideFramePosition(),
		        this.sidePanel.getSize());
		sideFrame.doNothingOnClose();
		this.separatedFrames.add(sideFrame);
		this.sidePanel.setAttached(false);
		this.sidePanel.updateParentContainer(sideFrame);

		final GenericFrame workspaceFrame = new GenericFrame(this,
		        this.workspacePanel, "Workspace",
		        this.getWorkspaceFramePosition(),
		        this.getWorkspaceFrameDimension());
		workspaceFrame.doNothingOnClose();
		this.separatedFrames.add(workspaceFrame);
		this.workspacePanel.setAttached(false);
		this.workspacePanel.updateParentContainer(workspaceFrame);

		this.isAttached = false;
		this.setSize(this.getDetachedNewDimension());

		this.validate();
		this.repaint();
	}

	public void attachWindows() {
		this.mainSplitPane.setLeftComponent(this.workspacePanel);
		this.mainSplitPane.setRightComponent(this.sidePanel);

		this.attachEdetachAllWindows.setText("Detach all windows");

		for (final JFrame frame : this.separatedFrames) {
			frame.getContentPane().removeAll();
			frame.dispose();
		}

		this.sidePanel.setAttached(true);
		this.sidePanel.updateParentContainer(this);

		this.workspacePanel.setAttached(true);
		this.workspacePanel.updateParentContainer(this);

		this.separatedFrames.clear();
		this.isAttached = true;
		this.setSize(this.getAttachedNewDimension());

		this.validate();
		this.repaint();
	}

	public boolean isAttached() {
		return this.isAttached;
	}

	public void setAttached(final boolean tof) {
		this.isAttached = tof;
	}

	public void quit() {
		this.omegaApp.saveOptions();
		this.dispose();
		System.exit(0);
	}
}