package edu.umassmed.omega.core.gui;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.RootPaneContainer;
import javax.swing.border.TitledBorder;

import edu.umassmed.omega.commons.gui.GenericPanel;
import edu.umassmed.omega.dataNew.coreElements.OmegaElement;
import edu.umassmed.omega.dataNew.coreElements.OmegaNamedElement;

public class OmegaElementInformationsPanel extends GenericPanel {

	private static final long serialVersionUID = -8599077833612345455L;

	private JLabel class_label, id_label, name_label;

	public OmegaElementInformationsPanel(final RootPaneContainer parent) {
		super(parent);

		this.setLayout(new GridLayout(3, 1));

		this.setBorder(new TitledBorder("Element information"));

		this.createAndAddWidgets();

		this.addListeners();
	}

	private void createAndAddWidgets() {
		this.class_label = new JLabel();
		this.add(this.class_label);

		this.id_label = new JLabel();
		this.id_label.setText("No element isSelected");
		this.add(this.id_label);

		this.name_label = new JLabel();
		this.add(this.name_label);
	}

	private void addListeners() {
		// TODO Auto-generated method stub

	}

	public void update(final OmegaElement element) {
		if (element != null) {
			this.class_label.setText(element.getClass().getSimpleName());
			this.id_label.setText(Long.toString(element.getElementID()));
			if (element instanceof OmegaNamedElement) {
				this.name_label
				        .setText(((OmegaNamedElement) element).getName());
			}
		} else {
			this.class_label.setText("");
			this.id_label.setText("No element isSelected");
			this.name_label.setText("");
		}
		this.repaint();
	}
}
