package edu.umassmed.omega.trajectoriesSegmentationPlugin;

import java.util.Date;
import java.util.GregorianCalendar;

import edu.umassmed.omega.commons.constants.OmegaConstantsMathSymbols;
import edu.umassmed.omega.commons.constants.OmegaGUIConstants;

public class TSConstants {

	public static final String ROI_SELECT_DIALOG = "Select ROI";
	public static final String ROI_SELECT_DIALOG_MSG = "Multiple ROI at that position, select the one you want to use:";
	public static final String ROI_SELECT_OK = "Ok";
	public static final String ROI_SELECT_FRAMEINDEX = "FrameIndex: ";
	public static final String ROI_SELECT_X = " X: ";
	public static final String ROI_SELECT_Y = " Y: ";

	public static final String PIXELS_UM = "<html><div align=\"center\">Pixels<br/>("
			+ OmegaConstantsMathSymbols.MU + "m)</div></html>";

	public static final String ZOOM_IN = OmegaGUIConstants.ZOOM_IN;
	public static final String ZOOM_OUT = OmegaGUIConstants.ZOOM_OUT;

	public static final String SAVE_CONFIRM = "Save Current Changes";
	public static final String SAVE_CONFIRM_MSG = "Do you want to save all current changes in a new analysis?";

	public static final String CANCEL_CONFIRM = "Cancel All Current Changes";
	public static final String CANCEL_CONFIRM_MSG = "Do you want to cancel all current changes?";

	public static final String BROWSER_TABNAME = "Browser";
	public static final String SEGMENTATION_TABNAME = "Segmentation";

	public static final String PREFERENCES = OmegaGUIConstants.PREFERENCES;

	public static final String EDIT = OmegaGUIConstants.MENU_EDIT;

	public static final String SAVE = OmegaGUIConstants.SAVE;
	public static final String UNDO = OmegaGUIConstants.UNDO;
	public static final String REDO = OmegaGUIConstants.REDO;
	public static final String CANCEL_ALL = OmegaGUIConstants.CANCEL_ALL;

	public static final String SELECT = "Select: ";
	public static final String SELECT_RESET = "Reset";
	public static final String SELECT_RESET_MS = "Reset Selection";
	public static final String SELECT_TRACK_START = "Select Start";
	public static final String SELECT_TRACK_START_MS = "Select Track Start";
	public static final String SELECT_TRACK_LAST = "Select Last";
	public static final String SELECT_TRACK_LAST_MS = "Select Track Last Selected Point";
	public static final String SELECT_TRACK_END = "Select End";
	public static final String SELECT_TRACK_END_MS = "Select Track End";
	public static final String SELECT_FIRST_MOTION = "Spot";
	public static final String SELECT_FIRST_SPOTS = "Motion Type";
	public static final String SELECT_FIRST_MS = "Start segmentation by selecting first the desired Motion Type or Spot";
	public static final String SELECT_NONE = " none.";
	public static final String SELECT_FROM_UPPER = "From";
	public static final String SELECT_FROM_LOWER = " from";
	public static final String SELECT_FROM_UPPER_SPACE = "From ";
	public static final String SELECT_FROM_LOWER_SPACE = " from ";
	public static final String SEGMENT_CONFIRM = "Segment Track ";
	public static final String SELECT_PUNCT = ".";
	public static final String SELECT_TO_LOWER_SPACE = " to ";

	public static final String SCALE_ONE = OmegaGUIConstants.SIDEPANEL_SCALE_ONE;
	public static final String SCALE_FIT = OmegaGUIConstants.SIDEPANEL_SCALE_FIT;

	public static final String ACTUAL_SEGM = "Current selection: ";

	public static final String PLUGIN_NAME = "OMEGA Trajectory Segmentation";
	public static final String PLUGIN_SNAME = "OMEGA TS";
	public static final String PLUGIN_DESC = "This is the default Trajectory Segmentation plugin provided by OMEGA. It utilizes the Trajectory Browser graphical user interface to visualize and select individual trajectories in the context of the image of origin, to evaluate the uniformity of their dynamic properties and to subdivide them into uniform segments when necessary. While the results of segmentation are arbitrarily decided by the user, OMEGA allows segmentation results to be evaluated and if necessary corrected by combining subsequent cycles of segmentation and motion analysis.";
	public static final String PLUGIN_ALGO_DESC = "This is a simple manual trajectory segmentation algorithm designed to subdivide individual trajectories into segments characterized by uniform motion properties. The subdivision of individual trajectories into segments is subjectively decided by the user and is therefore arbitrary. ";
	public static final Date PLUGIN_PUBL = new GregorianCalendar(2016, 12, 1)
			.getTime();
	public static final String PLUGIN_REFERENCE = "";
}
