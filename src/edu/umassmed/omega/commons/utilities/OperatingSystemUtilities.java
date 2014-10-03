package edu.umassmed.omega.commons.utilities;

import edu.umassmed.omega.commons.plugins.OmegaAlgorithmPlugin;

public class OperatingSystemUtilities {

	public static boolean isPluginSupported(final OmegaAlgorithmPlugin plugin) {
		if (plugin.getSupportedPlatforms().contains(
		        OperatingSystemUtilities.getOS()))
			return true;
		return false;
	}

	public static OperatingSystemEnum getOS() {
		final String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("win"))
			return OperatingSystemEnum.WIN;
		else if (os.contains("mac"))
			return OperatingSystemEnum.OSX;
		else if (os.contains("nix"))
			return OperatingSystemEnum.UNIX;
		else if (os.contains("nux"))
			return OperatingSystemEnum.LINUX;
		else if (os.contains("sunos"))
			return OperatingSystemEnum.SOLARIS;
		else if (os.contains("aix"))
			return OperatingSystemEnum.AIX;
		else
			return OperatingSystemEnum.NOTSUPPORTED;
	}

	public static boolean isWindows() {
		return OperatingSystemUtilities.getOS() == OperatingSystemEnum.WIN;
	}

	public static boolean isMacOSX() {
		return OperatingSystemUtilities.getOS() == OperatingSystemEnum.OSX;
	}

	public static boolean isUnix() {
		return OperatingSystemUtilities.getOS() == OperatingSystemEnum.UNIX;
	}

	public static boolean isLinux() {
		return OperatingSystemUtilities.getOS() == OperatingSystemEnum.UNIX;
	}

	public static boolean isAIX() {
		return OperatingSystemUtilities.getOS() == OperatingSystemEnum.AIX;
	}

	public static boolean isSolaris() {
		return OperatingSystemUtilities.getOS() == OperatingSystemEnum.SOLARIS;
	}

	public static boolean isSupported() {
		return OperatingSystemUtilities.getOS() != OperatingSystemEnum.NOTSUPPORTED;
	}
}
