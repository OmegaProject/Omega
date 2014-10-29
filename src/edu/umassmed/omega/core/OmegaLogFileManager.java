package edu.umassmed.omega.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import edu.umassmed.omega.commons.constants.OmegaConstants;
import edu.umassmed.omega.commons.plugins.OmegaPlugin;

public class OmegaLogFileManager implements UncaughtExceptionHandler {

	private static boolean debug = true;

	private static OmegaLogFileManager instance = null;

	private static final String GENERAL_LOG_NAME = "OmegaLog_";
	private static final String PLUGIN_LOG_NAME = OmegaLogFileManager.GENERAL_LOG_NAME
	        + "Plugin_";

	private final File logsDir;

	private final File awtLogFile, generalUnhandledException, generalLogFile;
	private final Map<OmegaPlugin, File> pluginLogFileMap;

	public OmegaLogFileManager() {
		this(System.getProperty("user.dir"));
	}

	public OmegaLogFileManager(final String workingDirName) {
		this.logsDir = new File(workingDirName + File.separator + "logs");
		if (!this.logsDir.exists()) {
			this.logsDir.mkdir();
		}
		this.pluginLogFileMap = new HashMap<>();

		final String fileName = this.logsDir.getPath() + File.separator
		        + OmegaLogFileManager.GENERAL_LOG_NAME;
		this.awtLogFile = new File(fileName + "awt.log");
		this.generalUnhandledException = new File(fileName + "unhandled.log");
		this.generalLogFile = new File(fileName + "core.log");

		System.setProperty("sun.awt.exception.handler",
		        OmegaLogFileManager.class.getName());
		Thread.currentThread().setUncaughtExceptionHandler(this);
		OmegaLogFileManager.instance = this;
	}

	public static void registerAsHandlerOnThread(final Thread th) {
		th.setUncaughtExceptionHandler(OmegaLogFileManager.instance);
	}

	public void handle(final Throwable t) {
		final String message = "Exception in thread: AWT";
		if (OmegaLogFileManager.debug) {
			System.err.println(message);
			t.printStackTrace();
		}
		this.appendToLog(this.awtLogFile, message, t);
	}

	@Override
	public void uncaughtException(final Thread th, final Throwable t) {
		final String message = "Exception in thread: " + th.getName();
		if (OmegaLogFileManager.debug) {
			System.err.println(message);
			t.printStackTrace();
		}
		this.appendToLog(this.generalUnhandledException, message, t);
	}

	public void handlePluginException(final OmegaPlugin plugin,
	        final Throwable t) {
		final String message = "Exception in plugin: " + plugin.getName();
		if (OmegaLogFileManager.debug) {
			System.err.println(message);
			t.printStackTrace();
		}
		File pluginLogFile = null;
		if (this.pluginLogFileMap.containsKey(plugin)) {
			pluginLogFile = this.pluginLogFileMap.get(plugin);
		} else {
			pluginLogFile = this.createNewLogFile(plugin);
			this.pluginLogFileMap.put(plugin, pluginLogFile);
		}
		this.appendToLog(pluginLogFile, message, t);
	}

	public void handleCoreException(final Throwable t) {
		final String message = "Exception in core";
		if (OmegaLogFileManager.debug) {
			System.err.println(message);
			t.printStackTrace();
		}
		this.appendToLog(this.generalLogFile, message, t);
	}

	private File createNewLogFile(final OmegaPlugin plugin) {
		final String logFileName = OmegaLogFileManager.PLUGIN_LOG_NAME
		        + plugin.getName().replace(" ", "") + ".log";
		return new File(this.logsDir.getPath() + File.separator + logFileName);
	}

	private void appendToLog(final File f, final String message,
	        final Throwable t) {
		final DateFormat format = new SimpleDateFormat(
		        OmegaConstants.OMEGA_DATE_FORMAT);
		final String timestamp = format
		        .format(Calendar.getInstance().getTime());

		try {
			if (!f.exists()) {
				f.createNewFile();
			}
			final FileWriter fw = new FileWriter(f, true);
			final BufferedWriter bw = new BufferedWriter(fw);
			bw.write("# ");
			bw.write(timestamp);
			bw.write("\n");
			bw.write(message);
			bw.write("\n");
			bw.write(t.getClass().getName() + " : " + t.getMessage());
			bw.write("\n");
			for (final StackTraceElement stackTrace : t.getStackTrace()) {
				bw.write(stackTrace.toString());
			}
			bw.close();
			fw.close();
		} catch (final IOException e) {
			// TODO manage
			e.printStackTrace();
		}
	}
}
