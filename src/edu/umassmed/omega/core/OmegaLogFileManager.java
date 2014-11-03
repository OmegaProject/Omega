package edu.umassmed.omega.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.umassmed.omega.commons.constants.OmegaConstants;
import edu.umassmed.omega.commons.plugins.OmegaPlugin;
import edu.umassmed.omega.commons.utilities.OmegaFileUtilities;

public class OmegaLogFileManager implements UncaughtExceptionHandler {

	private static boolean debug = true;

	private static OmegaLogFileManager instance = null;

	private static final String GENERAL_LOG_NAME = "OmegaLog_";
	private static final String PLUGIN_LOG_NAME = OmegaLogFileManager.GENERAL_LOG_NAME
	        + "Plugin_";

	private final File logsDir;

	private final File awtLogFile, generalUnhandledException, generalLogFile;
	private final Map<OmegaPlugin, File> pluginLogFileMap;

	static {
		OmegaLogFileManager.instance = new OmegaLogFileManager();
	}

	public static void createNewOmegaFileManager(final String workingDirName) {
		OmegaLogFileManager.instance = new OmegaLogFileManager(workingDirName);
	}

	public static OmegaLogFileManager getOmegaLogFileManager() {
		return OmegaLogFileManager.instance;
	}

	private OmegaLogFileManager() {
		this(System.getProperty("user.dir"));
	}

	private OmegaLogFileManager(final String workingDirName) {
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
	}

	public static void markNewRun(final List<OmegaPlugin> plugins) {
		final List<File> files = new ArrayList<>();
		final List<String> messages = new ArrayList<>();
		final String msg = "Application started!";
		messages.add(msg);
		for (final OmegaPlugin plugin : plugins) {
			final File f = OmegaLogFileManager.instance
			        .createNewLogFile(plugin);
			OmegaLogFileManager.instance.pluginLogFileMap.put(plugin, f);
			files.add(f);
		}
		files.add(OmegaLogFileManager.instance.awtLogFile);
		files.add(OmegaLogFileManager.instance.generalUnhandledException);
		files.add(OmegaLogFileManager.instance.generalLogFile);

		for (final File f : files) {
			OmegaLogFileManager.instance.appendToLog(f, messages);
		}
	}

	public static void registerAsExceptionHandlerOnThread(final Thread th) {
		th.setUncaughtExceptionHandler(OmegaLogFileManager.instance);
	}

	public static void handleCause(final File f, final Throwable t,
	        final List<String> messages, final List<Throwable> throwables) {
		final String message = "Caused by: ";
		if (OmegaLogFileManager.debug) {
			System.err.println(message);
			t.printStackTrace();
		}
		messages.add(message);
		throwables.add(t);
		if (t.getCause() != null) {
			OmegaLogFileManager.handleCause(f, t.getCause(), messages,
			        throwables);
		} else {
			OmegaLogFileManager.instance.appendToLog(f, messages, throwables);
		}
	}

	public static void handle(final Throwable t) {
		final List<String> messages = new ArrayList<>();
		final List<Throwable> throwables = new ArrayList<>();
		final String message = "Exception in thread: AWT";
		if (OmegaLogFileManager.debug) {
			System.err.println(message);
			t.printStackTrace();
		}
		final File f = OmegaLogFileManager.instance.awtLogFile;
		messages.add(message);
		throwables.add(t);
		if (t.getCause() != null) {
			OmegaLogFileManager.handleCause(f, t.getCause(), messages,
			        throwables);
		} else {
			OmegaLogFileManager.instance.appendToLog(f, messages, throwables);
		}
	}

	@Override
	public void uncaughtException(final Thread th, final Throwable t) {
		final List<String> messages = new ArrayList<>();
		final List<Throwable> throwables = new ArrayList<>();
		final String message = "Exception in thread: " + th.getName();
		if (OmegaLogFileManager.debug) {
			System.err.println(message);
			t.printStackTrace();
		}
		final File f = OmegaLogFileManager.instance.generalUnhandledException;
		messages.add(message);
		throwables.add(t);
		if (t.getCause() != null) {
			OmegaLogFileManager.handleCause(f, t.getCause(), messages,
			        throwables);
		} else {
			OmegaLogFileManager.instance.appendToLog(f, messages, throwables);
		}
	}

	public static void handleUncaughtException(final Throwable t) {
		final List<String> messages = new ArrayList<>();
		final List<Throwable> throwables = new ArrayList<>();
		final String message = "Exception in thread: "
		        + Thread.currentThread().getName();
		if (OmegaLogFileManager.debug) {
			System.err.println(message);
			t.printStackTrace();
		}
		final File f = OmegaLogFileManager.instance.generalUnhandledException;
		messages.add(message);
		throwables.add(t);
		if (t.getCause() != null) {
			OmegaLogFileManager.handleCause(f, t.getCause(), messages,
			        throwables);
		} else {
			OmegaLogFileManager.instance.appendToLog(f, messages, throwables);
		}
	}

	public static void handlePluginException(final OmegaPlugin plugin,
	        final Throwable t) {
		final List<String> messages = new ArrayList<>();
		final List<Throwable> throwables = new ArrayList<>();
		final String message1 = "Exception in plugin: " + plugin.getName();
		final String message2 = "Exception in thread: "
		        + Thread.currentThread().getName();
		final String message = message1 + "\n" + message2;
		if (OmegaLogFileManager.debug) {
			System.err.println(message);
			t.printStackTrace();
		}
		File f = null;
		if (OmegaLogFileManager.instance.pluginLogFileMap.containsKey(plugin)) {
			f = OmegaLogFileManager.instance.pluginLogFileMap.get(plugin);
		} else {
			f = OmegaLogFileManager.instance.createNewLogFile(plugin);
			OmegaLogFileManager.instance.pluginLogFileMap.put(plugin, f);
		}
		messages.add(message);
		throwables.add(t);
		if (t.getCause() != null) {
			OmegaLogFileManager.handleCause(f, t.getCause(), messages,
			        throwables);
		} else {
			OmegaLogFileManager.instance.appendToLog(f, messages, throwables);
		}
	}

	public static void handleCoreException(final Throwable t) {
		final List<String> messages = new ArrayList<>();
		final List<Throwable> throwables = new ArrayList<>();
		final String message1 = "Exception in core";
		final String message2 = "Exception in thread: "
		        + Thread.currentThread().getName();
		final String message = message1 + "\n" + message2;
		if (OmegaLogFileManager.debug) {
			System.err.println(message);
			t.printStackTrace();
		}
		messages.add(message);
		throwables.add(t);
		final File f = OmegaLogFileManager.instance.generalLogFile;
		if (t.getCause() != null) {
			OmegaLogFileManager.handleCause(f, t.getCause(), messages,
			        throwables);
		} else {
			OmegaLogFileManager.instance.appendToLog(f, messages, throwables);
		}
	}

	private File createNewLogFile(final OmegaPlugin plugin) {
		final String logFileName = OmegaLogFileManager.PLUGIN_LOG_NAME
		        + plugin.getName().replace(" ", "") + ".log";
		return new File(this.logsDir.getPath() + File.separator + logFileName);
	}

	private void appendToLog(final File f, final List<String> messages) {
		final DateFormat format = new SimpleDateFormat(
		        OmegaConstants.OMEGA_DATE_FORMAT);
		final String timestamp = format
		        .format(Calendar.getInstance().getTime());

		File fTemp = null;
		try {
			if (!f.exists()) {
				f.createNewFile();
			} else {
				fTemp = new File(f.getName() + "_tmp.log");
				fTemp.createNewFile();
				OmegaFileUtilities.copyFile(f, fTemp);
			}
			final FileWriter fw = new FileWriter(f);
			final BufferedWriter bw = new BufferedWriter(fw);
			bw.write("# ");
			bw.write(timestamp);
			bw.write("\n");
			for (int i = 0; i < messages.size(); i++) {
				final String message = messages.get(i);
				bw.write(message);
				bw.write("\n");
			}
			bw.write("\n");
			bw.close();
			fw.close();
			if ((fTemp != null)) {
				OmegaFileUtilities.appendFile(fTemp, f);
				fTemp.delete();
			}
		} catch (final IOException ex) {
			// TODO what can I do here?
			ex.printStackTrace();
		}
	}

	private void appendToLog(final File f, final List<String> messages,
	        final List<Throwable> throwables) {
		final DateFormat format = new SimpleDateFormat(
		        OmegaConstants.OMEGA_DATE_FORMAT);
		final String timestamp = format
		        .format(Calendar.getInstance().getTime());

		File fTemp = null;
		try {
			if (!f.exists()) {
				f.createNewFile();
			} else {
				fTemp = new File(f.getName() + "_tmp.log");
				fTemp.createNewFile();
				OmegaFileUtilities.copyFile(f, fTemp);
			}
			final FileWriter fw = new FileWriter(f);
			final BufferedWriter bw = new BufferedWriter(fw);
			bw.write("# ");
			bw.write(timestamp);
			bw.write("\n");
			for (int i = 0; i < messages.size(); i++) {
				final String message = messages.get(i);
				final Throwable t = throwables.get(i);
				bw.write(message);
				bw.write("\n");
				bw.write(t.getClass().getName() + " : " + t.getMessage());
				bw.write("\n");
				for (final StackTraceElement stackTrace : t.getStackTrace()) {
					bw.write(stackTrace.toString());
					bw.write("\n");
				}
			}
			bw.write("\n");
			bw.close();
			fw.close();
			if ((fTemp != null)) {
				OmegaFileUtilities.appendFile(fTemp, f);
				fTemp.delete();
			}
		} catch (final IOException ex) {
			// TODO what can I do here?
			ex.printStackTrace();
		}
	}
}
