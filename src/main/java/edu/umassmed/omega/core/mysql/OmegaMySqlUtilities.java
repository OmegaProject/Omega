package edu.umassmed.omega.core.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import edu.umassmed.omega.commons.data.coreElements.OmegaElement;
import edu.umassmed.omega.commons.data.coreElements.OmeroElement;

public class OmegaMySqlUtilities {
	public static long getID(final int dbID) {
		final long id = Long.valueOf(String.valueOf(dbID));
		return id;
	}

	public static OmegaElement getElement(final List<OmegaElement> elements,
	        final long id, final boolean gatewayId) {
		for (final OmegaElement element : elements)
			if (!gatewayId) {
				if (element.getElementID() == id)
					return element;
			} else {
				if ((element instanceof OmeroElement)
						&& (((OmeroElement) element).getOmeroId() == id))
					return element;
			}
		return null;
	}

	public static int getDBIdFROMOmegaElementId(final Connection connection,
	        final String elementType, final long omegaID) throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("SELECT * FROM ");
		query.append(elementType);
		query.append(" WHERE Omero_Id = ");
		query.append(omegaID);
		final PreparedStatement stat = connection.prepareStatement(query
		        .toString());
		final ResultSet results = stat.executeQuery();
		int dbID = -1;
		if (results.next()) {
			dbID = results.getInt(1);
		}
		results.close();
		stat.close();
		return dbID;
	}
}
