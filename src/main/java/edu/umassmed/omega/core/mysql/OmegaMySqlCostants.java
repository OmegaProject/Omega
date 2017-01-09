package edu.umassmed.omega.core.mysql;

public class OmegaMySqlCostants {
	
	public static String OMERO_ID_FIELD = "Omero_Id";
	public static String IMAGE_OMERO_ID_FIELD = "Image_Omero_Id";
	public static String DATASET_OMERO_ID_FIELD = "Dataset_Omero_Id";
	public static String PROJECT_ID_FIELD = "Project_Seq_Id";
	public static String DATASET_ID_FIELD = "Dataset_Seq_Id";
	public static String IMAGE_ID_FIELD = "Image_Seq_Id";
	public static String IMAGEPIXELS_ID_FIELD = "Pixel_Seq_Id";
	public static String FRAME_ID_FIELD = "Frame_Seq_Id";
	public static String EXPERIMENTER_ID_FIELD = "Experimenter_Seq_Id";
	public static String PERSON_ID_FIELD = "Person_Seq_Id";
	public static String ALGO_INFO_ID_FIELD = "AlgorithmInformation_Seq_Id";
	public static String PARAM_ID_FIELD = "Parameter_Seq_Id";
	public static String ALGO_SPEC_ID_FIELD = "AlgorithmSpecification_Seq_Id";
	public static String SEGMENT_ID_FIELD = "Segment_Seq_Id";
	public static String ANALYSIS_ID_FIELD = "AnalysisRun_Seq_Id";
	public static String ANALYSIS_PARENT_ID_FIELD = "Parent_AnalysisRun_Seq_Id";
	public static String ROI_ID_FIELD = "ROI_Seq_Id";
	public static String PARTICLE_ID_FIELD = "Particle_Seq_Id";
	public static String ROI_VALUES_ID_FIELD = "ROI_Values_Seq_Id";
	public static String TRAJECTORY_ID_FIELD = "Trajectory_Seq_Id";
	public static String SEGMENTATION_TYPES_ID_FIELD = "SegmentationTypes_Seq_Id";
	public static String SEGMENTATION_TYPE_ID_FIELD = "SegmentationType_Seq_Id";
	public static String TRACKING_MEASURES_ID_FIELD = "TrackingMeasures_Seq_Id";
	public static String CHANNEL_ID_FIELD = "Channel_Seq_Id";
	
	public static String NAME_FIELD = "Name";
	
	public static String AQUISITION_DATE_FIELD = "Aquisition_Date";
	public static String IMPORT_DATE_FIELD = "Import_Date";
	
	public static String PIXELSTYPE_FIELD = "pixel_type";
	public static String PIXELSSIZE_X_FIELD = "PixelSizeX";
	public static String PIXELSSIZE_Y_FIELD = "PixelSizeY";
	public static String PIXELSSIZE_Z_FIELD = "PixelSizeZ";
	
	public static String SIZE_X_FIELD = "SizeX";
	public static String SIZE_Y_FIELD = "SizeY";
	public static String SIZE_Z_FIELD = "SizeZ";
	public static String SIZE_C_FIELD = "SizeC";
	public static String SIZE_T_FIELD = "SizeT";
	
	public static String FRAME_INDEX_FIELD = "Frame_index";
	public static String CHANNEL_FIELD = "Channel";
	public static String PLANE_FIELD = "ZPlane";
	
	public static String FIRST_NAME_FIELD = "First_name";
	public static String LAST_NAME_FIELD = "Last_name";
	
	public static String VERSION_FIELD = "Version";
	public static String DESCRIPTION_FIELD = "Description";
	public static String PUBLICATION_DATE_FIELD = "Publication_date";
	public static String REFERENCE_FIELD = "Reference";
	
	public static String VALUE_FIELD = "Value";
	public static String TYPE_FIELD = "Type";
	
	public static String SEGMENT_TYPE_FIELD = "SegmentationType";
	public static String SEGMENT_START_ROI_FIELD = "StartingROI_Id";
	public static String SEGMENT_END_ROI_FIELD = "EndingROI_Id";
	
	public static String ROI_POS_X_FIELD = "Pos_X";
	public static String ROI_POS_Y_FIELD = "Pos_Y";
	
	public static String PEAK_INTENSITY_FIELD = "PeakIntensity";
	public static String CENTROID_INTENSITY_FIELD = "CentroidIntensity";
	public static String M0_PROV_FIELD = "m0_prov";
	public static String M2_PROV_FIELD = "m2_prov";
	
	public static String NUMBER_POINTS_FIELD = "NumberOfPoints";
	public static String COLOR_RED_FIELD = "Color_Red";
	public static String COLOR_GREEN_FIELD = "Color_Green";
	public static String COLOR_BLUE_FIELD = "Color_Blue";
	public static String ANNOTATION_FILED = "Annotation";
	
	public static String SNR_VALUE_FIELD = "value";
	
	public static String MIN_VALUE_FIELD = "min";
	public static String AVG_VALUE_FIELD = "avg";
	public static String MAX_VALUE_FIELD = "max";
	
	public static String DATE_FIELD = "Date";
	
	public static String INDEX_FIELD = "val_index";
	public static String NY_INDEX_FIELD = "ny_index";
	
	public static String ANGLE_FIELD = "angle";
	public static String DIRECTIONAL_CHANGE_FIELD = "directionalChange";
	
	public static String D_FIELD = "d";
	public static String SMSS_FIELD = "smss";
	
	public static String CHANNEL_INDEX = "Channel_index";
	
	public static String PROJECT_TABLE = "project";
	public static String DATASET_TABLE = "dataset";
	public static String IMAGE_DATASET_TABLE = "images_datasets_map";
	public static String IMAGE_TABLE = "image";
	public static String IMAGEPIXELS_TABLE = "pixels";
	public static String CHANNEL_TABLE = "channel";
	public static String PIXELS_CHANNEL_TABLE = "pixels_channel_map";
	public static String FRAME_TABLE = "frame";
	public static String EXPERIMENTER_TABLE = "experimenter";
	public static String PERSON_TABLE = "person";
	public static String ALGO_INFO_TABLE = "algorithm_information";
	public static String PARAM_TABLE = "parameter";
	public static String ALGO_SPEC_TABLE = "algorithm_specification";
	public static String SEGMENT_TABLE = "segment";
	public static String ROI_TABLE = "roi";
	public static String PARTICLE_TABLE = "particle";
	public static String ROI_VALUES_TABLE = "roi_values";
	public static String TRAJECTORY_TABLE = "trajectory";
	public static String TRAJECTORY_ROI_TABLE = "trajectories_roi_map";
	public static String ANALYSIS_TRAJECTORY_TABLE = "analysis_trajectories_map";
	public static String ANALYSIS_TABLE = "analysis_run";
	public static String ANALYSIS_PARENT_TABLE = "analysis_run_map";
	public static String SEGMENTATION_TYPES_TABLE = "segmentation_types";
	public static String SEGMENTATION_TYPE_TABLE = "segmentation_type";
	public static String SEGMENTATION_TYPES_MAP = "segmentation_types_map";
	public static String ANALYSIS_SEGMENTATION_TYPES_MAP = "analysis_segmentation_types_map";
	public static String TRACKING_MEASURES_TABLE = "tracking_measures";
	public static String TRACKING_MEASURES_SEGMENT_TABLE = "trackingMeasures_segment_map";
	public static String TRACKING_MEASURES_INTENSITY_PEAK_TABLE = "intensity_peak_signal";
	public static String TRACKING_MEASURES_INTENSITY_MEAN_TABLE = "intensity_mean_signal";
	public static String TRACKING_MEASURES_INTENSITY_CENTROID_TABLE = "intensity_centroid_signal";
	public static String TRACKING_MEASURES_VELOCITY_LOCAL_VELOCITY_TABLE = "velocity_local_velocity";
	public static String TRACKING_MEASURES_VELOCITY_AVERAGE_STRAIGHT_LINE_VELOCITY_TABLE = "velocity_avg_straight_line_velocity";
	public static String TRACKING_MEASURES_VELOCITY_LOCAL_SPEED_TABLE = "velocity_local_speed";
	public static String TRACKING_MEASURES_VELOCITY_AVERAGE_CURVILINEAR_SPEED_TABLE = "velocity_avg_curvilinear_speed";
	public static String TRACKING_MEASURES_VELOCITY_FORWARD_PROGRESSION_LINEARITY_TABLE = "velocity_forward_progression_linearity";
	public static String TRACKING_MEASURES_MOBILITY_DISTANCE_TABLE = "mobility_distance";
	public static String TRACKING_MEASURES_MOBILITY_DISPLACEMENT_TABLE = "mobility_displacement";
	public static String TRACKING_MEASURES_MOBILITY_MAX_DISPLACEMENT_TABLE = "mobility_max_displacement";
	public static String TRACKING_MEASURES_MOBILITY_TOTAL_TIME_TRAVELED_TABLE = "mobility_total_time_traveled";
	public static String TRACKING_MEASURES_MOBILITY_CONFINMENT_RATIO_TABLE = "mobility_confinement_ratio";
	public static String TRACKING_MEASURES_MOBILITY_ANGLE_DIRECTION_CHANGE_TABLE = "mobility_angle_directional_change";
	public static String TRACKING_MEASURES_DIFFUSIVITY_GAMMA_LOG_TABLE = "diffusivity_gamma_log";
	public static String TRACKING_MEASURES_DIFFUSIVITY_GAMMA_TABLE = "diffusivity_gamma";
	public static String TRACKING_MEASURES_DIFFUSIVITY_GAMMA_D_LOG_TABLE = "diffusivity_gamma_d_log";
	public static String TRACKING_MEASURES_DIFFUSIVITY_GAMMA_D_TABLE = "diffusivity_gamma_d";
	public static String TRACKING_MEASURES_DIFFUSIVITY_LOG_DELTA_T_TABLE = "diffusivity_log_delta_t";
	public static String TRACKING_MEASURES_DIFFUSIVITY_DELTA_T_TABLE = "diffusivity_delta_t";
	public static String TRACKING_MEASURES_DIFFUSIVITY_LOG_MU_TABLE = "diffusivity_log_mu";
	public static String TRACKING_MEASURES_DIFFUSIVITY_MU_TABLE = "diffusivity_mu";
	public static String TRACKING_MEASURES_DIFFUSIVITY_NY_TABLE = "diffusivity_ny";
	public static String TRACKING_MEASURES_DIFFUSIVITY_SMSS_LOG_TABLE = "diffusivity_smss_log";
	public static String TRACKING_MEASURES_DIFFUSIVITY_SMSS_TABLE = "diffusivity_smss";
	public static String TRACKING_MEASURES_DIFFUSIVITY_ERRORS_LOG_TABLE = "diffusivity_errors_log";
	public static String TRACKING_MEASURES_DIFFUSIVITY_ERRORS_TABLE = "diffusivity_errors";
	
	public static String TRACKING_MEASURES_DIFFUSIVITY_SNR_TABLE = "trackingMeasures_diff_snr_map";
	public static String TRACKING_MEASURES_DIFFUSIVITY_PARENT_TABLE = "trackingMeasures_diff_parent_map";
	
	public static String SNR_LOCAL_SNR_TABLE = "snr_local_snr";
	public static String SNR_LOCAL_SNR_TABLE_ERROR_INDEX = "snr_local_snr_error_index";
	public static String SNR_LOCAL_NOISE_TABLE = "snr_local_noise";
	public static String SNR_LOCAL_PEAK_SIGNAL_TABLE = "snr_local_peak_signal";
	public static String SNR_LOCAL_SIGNAL_SIZE_TABLE = "snr_local_particle_area";
	public static String SNR_LOCAL_MEAN_SIGNAL_TABLE = "snr_local_mean_signal";
	public static String SNR_LOCAL_CENTER_SIGNAL_TABLE = "snr_local_center_signal";
	public static String SNR_IMAGE_MAX_SNR_TABLE = "snr_image_max_snr";
	public static String SNR_IMAGE_MAX_ERROR_INDEX_SNR_TABLE = "snr_image_max_snr_error_index";
	public static String SNR_IMAGE_MIN_SNR_TABLE = "snr_image_min_snr";
	public static String SNR_IMAGE_MIN_ERROR_INDEX_SNR_TABLE = "snr_image_min_snr_error_index";
	public static String SNR_IMAGE_AVG_SNR_TABLE = "snr_image_avg_snr";
	public static String SNR_IMAGE_AVG_ERROR_INDEX_SNR_TABLE = "snr_image_avg_snr_error_index";
	public static String SNR_IMAGE_NOISE_TABLE = "snr_image_noise";
	public static String SNR_IMAGE_BG_TABLE = "snr_image_bg";
}
