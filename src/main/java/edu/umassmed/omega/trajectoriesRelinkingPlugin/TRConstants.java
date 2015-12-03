package main.java.edu.umassmed.omega.trajectoriesRelinkingPlugin;

import java.util.Date;
import java.util.GregorianCalendar;

import main.java.edu.umassmed.omega.commons.constants.OmegaGUIConstants;

public class TRConstants {

	public static final String SELECT_IMAGE = OmegaGUIConstants.SELECT_IMAGE;
	public static final String SELECT_TRACKS_SPOT = OmegaGUIConstants.SELECT_TRACKS_SPOT;
	public static final String SELECT_TRACKS_LINKING = OmegaGUIConstants.SELECT_TRACKS_LINKING;
	public static final String SELECT_TRACKS_ADJ = OmegaGUIConstants.SELECT_TRACKS_ADJ;
	public static final String SELECT_TRACKS_SEGM = OmegaGUIConstants.SELECT_TRACKS_SEGM;
	public static final String SELECT_TRACK_MEASURES = OmegaGUIConstants.SELECT_TRACK_MEASURES;

	public static final String SPLIT_APPENDIX_1 = ".1";
	public static final String SPLIT_APPENDIX_2 = ".2";
	public static final String MERGE_APPENDIX = "_";

	public static final String SAVE_CONFIRM = "Save Current Changes";
	public static final String SAVE_CONFIRM_MSG = "Do you want to save all current changes in a new analysis?";

	public static final String CANCEL_CONFIRM = "Cancel All Current Changes";
	public static final String CANCEL_CONFIRM_MSG = "Do you want to cancel all current changes?";

	public static final String BROWSER_TABNAME = "Browser";
	public static final String EDITOR_TABNAME = "Editor";

	public static final String EDIT = OmegaGUIConstants.MENU_EDIT;

	public static final String SAVE = OmegaGUIConstants.SAVE;
	public static final String UNDO = OmegaGUIConstants.UNDO;
	public static final String REDO = OmegaGUIConstants.REDO;
	public static final String CANCEL_ALL = OmegaGUIConstants.CANCEL_ALL;

	public static final String SPLIT_ACTION = "Split Track";
	public static final String MERGE_ACTION = "Merge Tracks";

	public static final String MERGE_ACTION_START = "Merge Tracks Start";
	public static final String MERGE_ACTION_END = "Merge Tracks End";

	public static final String MERGE_CONFIRM = "Merge Tracks Confirmation";
	public static final String MERGE_CONFIRM_MSG1 = "<html>Tracks ";
	public static final String MERGE_CONFIRM_MSG2 = " will be merged.<br>";
	public static final String MERGE_CONFIRM_MSG3 = "Resulting track will contain ";
	public static final String MERGE_CONFIRM_MSG4 = " spots</html>";

	public static final String SPLIT_CONFIRM = "";
	public static final String SPLIT_CONFIRM_MSG1 = "<html>Track ";
	public static final String SPLIT_CONFIRM_MSG2 = " will be split at timepoint ";
	public static final String SPLIT_CONFIRM_MSG3 = ".<br>Resulting tracks will contain ";
	public static final String SPLIT_CONFIRM_MSG4 = " spots respectively</html>";

	public static final String CONFIRM_MSG_AND = " and ";

	public static final String PLUGIN_NAME = "OMEGA Trajectory Editor";
	public static final String PLUGIN_SNAME = "OMEGA TE";
	public static final String PLUGIN_DESC = "This is the default Trajectory Editor plugin provided by OMEGA. It utilizes the Trajectory Browser graphical user interface to facilitate the visualization and selection of individual trajectories in the context of the image of origin, their inspection and the editing of individual links when necessary.";
	public static final String PLUGIN_ALGO_DESC = "This is a simple manual trajectory re-linking algorithm designed to edit individual particle-particle links in cases in which linking errors are introduced by the Single Particle Tracking algorithm. Such errors are common and typically result from particle blinking, temporary focal plane shifts or the crossing of trajectories with temporary particle fusion.";
	public static final Date PLUGIN_PUBL = new GregorianCalendar(2014, 12, 1)
	        .getTime();

}
