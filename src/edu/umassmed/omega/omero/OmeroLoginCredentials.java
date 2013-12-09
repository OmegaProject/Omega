package edu.umassmed.omega.omero;

/**
 * @author Vanni Galli
 */
public class OmeroLoginCredentials {

	/** The name of the user. */
	private final String userName;

	/** The password of the user. */
	private final String password;

	/**
	 * Creates a new instance.
	 * 
	 * @param userName
	 *            The user name.
	 * @param password
	 *            The password.
	 * @param hostname
	 *            The name of the server.
	 */
	public OmeroLoginCredentials(final String username, final String password) {
		this.userName = username;
		this.password = password;
	}

	/**
	 * Returns the name of the user.
	 * 
	 * @return See above.
	 */
	public String getUserName() {
		return this.userName;
	}

	/**
	 * Returns the address of the server
	 * 
	 * @return See above.
	 */
	public String getPassword() {
		return this.password;
	}
}
