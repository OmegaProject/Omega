/*******************************************************************************
 * Copyright (C) 2014 University of Massachusetts Medical School
 * Alessandro Rigano (Program in Molecular Medicine)
 * Caterina Strambio De Castillia (Program in Molecular Medicine)
 *
 * Created by the Open Microscopy Environment inteGrated Analysis (OMEGA) team: 
 * Alex Rigano, Caterina Strambio De Castillia, Jasmine Clark, Vanni Galli, 
 * Raffaello Giulietti, Loris Grossi, Eric Hunter, Tiziano Leidi, Jeremy Luban, 
 * Ivo Sbalzarini and Mario Valle.
 *
 * Key contacts:
 * Caterina Strambio De Castillia: caterina.strambio@umassmed.edu
 * Alex Rigano: alex.rigano@umassmed.edu
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package edu.umassmed.omega.commons.gui;

import java.io.File;

import javax.swing.JTextField;

public class GenericTextFieldValidable extends JTextField {
	public static final int CONTENT_NOVALIDATION = -1;

	public static final int CONTENT_INT = 0;
	public static final int CONTENT_DOUBLE = 1;

	public static final int CONTENT_FILE = 10;
	public static final int CONTENT_FOLDER = 11;

	private int content;

	public GenericTextFieldValidable(final int content) {
		switch (content) {
		case CONTENT_FILE:
		case CONTENT_FOLDER:
		case CONTENT_INT:
		case CONTENT_DOUBLE:
			this.content = content;
			break;
		default:
			this.content = GenericTextFieldValidable.CONTENT_NOVALIDATION;
		}
	}

	public String getError() {
		final String s = this.getText();
		final StringBuffer error = new StringBuffer();
		error.append(s);
		error.append(" is not a valid ");
		switch (this.content) {
		case CONTENT_INT:
			error.append("integer");
			break;
		case CONTENT_DOUBLE:
			error.append("double");
			break;
		case CONTENT_FILE:
			error.append("folder");
			break;
		case CONTENT_FOLDER:
			error.append("file");
			break;
		}
		return error.toString();
	}

	public boolean isContentValidated() {
		switch (this.content) {
		case CONTENT_INT:
			return this.isContentInt();
		case CONTENT_DOUBLE:
			return this.isContentDouble();
		case CONTENT_FILE:
			return this.isContentFile();
		case CONTENT_FOLDER:
			return this.isContentFolder();
		default:
			return true;
		}
	}

	private boolean isContentInt() {
		final String s = this.getText();
		try {
			Integer.parseInt(s);
			return true;
		} catch (final NumberFormatException ex) {
			return false;
		}
	}

	private boolean isContentDouble() {
		final String s = this.getText();
		try {
			Double.parseDouble(s);
			return true;
		} catch (final NumberFormatException ex) {
			return false;
		}
	}

	private boolean isContentFile() {
		final String s = this.getText();
		final File f = new File(s);
		if (f.exists() && f.isFile())
			return true;
		return false;
	}

	private boolean isContentFolder() {
		final String s = this.getText();
		final File f = new File(s);
		if (f.exists() && f.isDirectory())
			return true;
		return false;
	}
}
