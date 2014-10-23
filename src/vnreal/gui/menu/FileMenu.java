/* ***** BEGIN LICENSE BLOCK *****
 * Copyright (C) 2010-2011, The VNREAL Project Team.
 * 
 * This work has been funded by the European FP7
 * Network of Excellence "Euro-NF" (grant agreement no. 216366)
 * through the Specific Joint Developments and Experiments Project
 * "Virtual Network Resource Embedding Algorithms" (VNREAL). 
 *
 * The VNREAL Project Team consists of members from:
 * - University of Wuerzburg, Germany
 * - Universitat Politecnica de Catalunya, Spain
 * - University of Passau, Germany
 * See the file AUTHORS for details and contact information.
 * 
 * This file is part of ALEVIN (ALgorithms for Embedding VIrtual Networks).
 *
 * ALEVIN is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License Version 3 or later
 * (the "GPL"), or the GNU Lesser General Public License Version 3 or later
 * (the "LGPL") as published by the Free Software Foundation.
 *
 * ALEVIN is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * or the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License and
 * GNU Lesser General Public License along with ALEVIN; see the file
 * COPYING. If not, see <http://www.gnu.org/licenses/>.
 *
 * ***** END LICENSE BLOCK ***** */
package vnreal.gui.menu;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import mulavito.gui.dialogs.LocatableFileChooser;
import mulavito.utils.Resources;
import vnreal.Scenario;
import vnreal.gui.GUI;
import vnreal.gui.control.MyFileChooser;
import vnreal.gui.control.MyFileChooser.MyFileChooserType;
import vnreal.gui.dialog.SNDlibImporterGUI;
import vnreal.gui.utils.FileFilters;
import vnreal.io.RigImporter;
import vnreal.io.RigImporter.RigImportException;
import vnreal.io.SNDlibImporter;
import vnreal.io.ShiipImporter;
import vnreal.io.ShiipImporter.ShiipImportException;
import vnreal.io.XMLExporter;
import vnreal.io.XMLImporter;
import vnreal.network.NetworkStack;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.virtual.VirtualNetwork;

/**
 * @author Vlad Singeorzan
 * @author Michael Duelli
 * @since 2010-10-19
 * 
 * @author Philip Huppert
 */
@SuppressWarnings("serial")
public final class FileMenu extends JMenu {
	/**
	 * Mnemonic to access the file menu.
	 */
	private static final int MNEMONIC = KeyEvent.VK_F;

	/**
	 * Title of the file menu.
	 */
	private static final String TITLE = "File";

	/**
	 * The default modifier for keyboard shortcuts to access the entries.
	 */
	private static final int ACCELERATOR_MODIFIER = Toolkit.getDefaultToolkit()
			.getMenuShortcutKeyMask();

	private final Scenario scenario;

	public FileMenu(Scenario scenario) {
		super(TITLE);
		this.scenario = scenario;
		this.setMnemonic(MNEMONIC);
	
		// Build menu entries.
		this.add(new NewScenario());
		this.addSeparator();
		this.add(new ImportXML());
		this.add(new ImportSNDlib());
		this.add(new ImportRIG());
		this.add(new ImportShiip());
		this.addSeparator();
		this.add(new ExportXML());
		this.addSeparator();
		this.add(new CloseScenario());
		this.addSeparator();
		this.add(new Quit());
	}

	/**
	 * Transform a {@link FileMenuItem} to a {@link JMenuItem} and add it to the
	 * menu.
	 * 
	 * @param item to add.
	 */
	private void add(FileMenuItem item) {
		JMenuItem mi = new JMenuItem(item.getText());
		mi.addActionListener(item);
	
		if (item.getAccelerator() != null) {
			mi.setAccelerator(item.getAccelerator());
		}
	
		if (item.getIcon() != null) {
			mi.setIcon(item.getIcon());
		}
	
		this.add(mi);
	}

	/**
	 * This abstract class represents an entry in the {@link FileMenu}.
	 * 
	 * @author Philip Huppert
	 */
	private static abstract class FileMenuItem implements ActionListener {
		/**
		 * @return the label of the menu entry.
		 */
		public abstract String getText();

		/**
		 * @return the icon of the menu entry, or null.
		 */
		public ImageIcon getIcon() {
			return null;
		}

		/**
		 * @return the keyboard shortcut for the menu entry, or null.
		 */
		public KeyStroke getAccelerator() {
			return null;
		}
	}

	/**
	 * This abstract class represents a {@link FileMenuItem} specialized for
	 * file imports.
	 * 
	 * @author Philip Huppert
	 */
	private static abstract class ImportMenuItem extends FileMenuItem {
		/**
		 * Prompt the user to choose a file to open.
		 * 
		 * @param title
		 *            of the dialog.
		 * @param type
		 *            determines the initial directory shown to the user.
		 * @param filter
		 *            determines the type of files shown to the user.
		 * @return the selected file, or null if no file was selected.
		 */
		protected File chooseFile(String title, MyFileChooserType type,
				FileFilter filter) {
			MyFileChooser fileChooser = new MyFileChooser(title, type, true);
			fileChooser.addChoosableFileFilter(filter);
			fileChooser.showOpenDialog(GUI.getInstance());

			if (fileChooser.getSelectedFile() == null) {
				System.err.println("User aborted import selection.");
			}

			return fileChooser.getSelectedFile();
		}

		@Override
		public ImageIcon getIcon() {
			return Resources.getIconByName("/actions/go-last.png");
		}
	}

	/**
	 * This abstract class represents a {@link FileMenuItem} specialized for
	 * file exports.
	 * 
	 * @author Philip Huppert
	 */
	private static abstract class ExportMenuItem extends FileMenuItem {
		/**
		 * Prompt the user to choose a file to save to.
		 * 
		 * @param title
		 *            of the dialog.
		 * @param filter
		 *            determines the type of files shown to the user.
		 * @return the selected file, or null if no file was selected.
		 */
		protected File chooseFile(String title, FileFilter filter) {
			LocatableFileChooser fileChooser = new LocatableFileChooser(
					title, 0, false);
			fileChooser.addChoosableFileFilter(filter);
			fileChooser.showSaveDialog(GUI.getInstance());

			if (fileChooser.getSelectedFile() == null) {
				System.err.println("User aborted export selection.");
			}

			return fileChooser.getSelectedFile();
		}

		@Override
		public ImageIcon getIcon() {
			return Resources.getIconByName("/actions/go-first.png");
		}
	}

	/**
	 * {@link FileMenuItem} to create a new scenario or add empty layers to an
	 * existing one.
	 * 
	 * @author Philip Huppert
	 */
	private class NewScenario extends FileMenuItem {
		@Override
		public String getText() {
			return "New empty scenario / layers";
		}

		@Override
		public ImageIcon getIcon() {
			return Resources.getIconByName("/places/network-workgroup.png");
		}

		@Override
		public KeyStroke getAccelerator() {
			return KeyStroke.getKeyStroke(KeyEvent.VK_N, ACCELERATOR_MODIFIER);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Integer numVNs = null;
			while (numVNs == null) {
				String answer = JOptionPane.showInputDialog(
						GUI.getInstance(),
						"Number of virtual networks:");
	
				if (answer == null) {
					return;
				}
	
				try {
					numVNs = Integer.parseInt(answer);
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(
							GUI.getInstance(),
							"Invalid number of virtual networks.",
							"Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}

			// Create new empty network stack if necessary.
			if (scenario.getNetworkStack() == null) {
				scenario.setNetworkStack(
						new NetworkStack(
								new SubstrateNetwork(false),
								new LinkedList<VirtualNetwork>()));
			}
			NetworkStack stack = scenario.getNetworkStack();

			// Add numVNs additional layers.
			int layers = stack.size();
			for (int i = 0; i < numVNs; i++) {
				stack.addLayer(new VirtualNetwork(layers + i));
			}
		}
	}

	/**
	 * {@link ImportMenuItem} to import a {@link Scenario} from an XML file.
	 * 
	 * @author Philip Huppert
	 */
	private class ImportXML extends ImportMenuItem {
		@Override
		public String getText() {
			return "Import from XML";
		}

		@Override
		public KeyStroke getAccelerator() {
			return KeyStroke.getKeyStroke(KeyEvent.VK_O, ACCELERATOR_MODIFIER);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			File f = this.chooseFile("Scenario Import", MyFileChooserType.XML,
					FileFilters.xmlFilter);
			if (f == null) {
				return;
			}

			try {
				XMLImporter.importScenario(
						f.getCanonicalPath(),
						scenario);
			} catch (Exception ex) {
				// TODO why this catch all for xml import?
				ex.printStackTrace();
			}
		}
	}

	/**
	 * {@link ImportMenuItem} to import a {@link Scenario} from SNDlib.
	 * 
	 * @author Philip Huppert
	 */
	private class ImportSNDlib extends ImportMenuItem {
		@Override
		public String getText() {
			return "Import from SNDlib";
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			File f = this.chooseFile("Scenario Import",
					MyFileChooserType.SNDLIB, FileFilters.txtFilter);
			if (f == null) {
				return;
			}

			try {
				SNDlibImporter i = new SNDlibImporter(
						f.getCanonicalPath());
				if (SNDlibImporterGUI.showNetworkTypeChooser(i, scenario)) {
					if (SNDlibImporterGUI.configureUpgrades(i)) {
						i.setNetworkStack(scenario);
					}
				}
			} catch (Exception ex) {
				// TODO why this catch-all for SNDlib import?
				ex.printStackTrace();
			}
		}
	}

	/**
	 * {@link ImportMenuItem} to import a {@link SubstrateNetwork} from a RIG
	 * file.
	 * 
	 * @author Philip Huppert
	 */
	private class ImportRIG extends ImportMenuItem {

		@Override
		public String getText() {
			return "Import from RIG";
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			File f = this.chooseFile("Substrate Topology Import",
					MyFileChooserType.MISC, FileFilters.rigFilter);
			if (f == null) {
				return;
			}

			SubstrateNetwork sn = null;
			try {
				sn = RigImporter.importNetwork(f);
			} catch (IOException ioE) {
				throw new RuntimeException("IOException during import.", ioE);
			} catch (RigImportException imE) {
				throw new RuntimeException("Import failure.", imE);
			}
			NetworkStack ns = new NetworkStack(sn,
					new ArrayList<VirtualNetwork>());
			scenario.setNetworkStack(ns);
		}
	}
	
	/**
	 * {@link ImportMenuItem} to import a {@link SubstrateNetwork} from a Shiip
	 * file.
	 * 
	 * @author Philip Huppert
	 */
	private class ImportShiip extends ImportMenuItem {

		@Override
		public String getText() {
			return "Import from Shiip";
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			File f = this.chooseFile("Substrate Topology Import",
					MyFileChooserType.MISC, FileFilters.txtFilter);
			if (f == null) {
				return;
			}

			SubstrateNetwork sn = null;
			try {
				sn = ShiipImporter.importNetwork(f);
			} catch (IOException ioE) {
				throw new RuntimeException("IOException during import.", ioE);
			} catch (ShiipImportException imE) {
				throw new RuntimeException("Import failure.", imE);
			}
			NetworkStack ns = new NetworkStack(sn,
					new ArrayList<VirtualNetwork>());
			scenario.setNetworkStack(ns);
		}
	}
	
	
	/**
	 * {@link FileMenuItem} to export the current scenario to an XML file.
	 * 
	 * @author Philip Huppert
	 */
	private class ExportXML extends ExportMenuItem {

		@Override
		public String getText() {
			return "Export to XML";
		}

		@Override
		public KeyStroke getAccelerator() {
			return KeyStroke.getKeyStroke(KeyEvent.VK_S, ACCELERATOR_MODIFIER);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (scenario.getNetworkStack() == null) {
				JOptionPane.showMessageDialog(
						GUI.getInstance(),
						"No scenario opened, no export possible.");
				return;
			}

			File f =this.chooseFile("Scenario Export", FileFilters.xmlFilter);
			if (f == null) {
				return;
			}

			String fileName = f.getAbsolutePath();
			if (!fileName.endsWith(".xml")) {
				fileName = fileName + ".xml";
			}

			try {
				XMLExporter.exportScenario(fileName, scenario);
			} catch (Exception ex) {
				// TODO why this catch-all for XML export?
				ex.printStackTrace();
			}
		}
	}

	/**
	 * {@link FileMenuItem} to close the currently opened scenario.
	 * 
	 * @author Philip Huppert
	 */
	private class CloseScenario extends FileMenuItem {
		@Override
		public String getText() {
			return "Close scenario";
		}

		@Override
		public KeyStroke getAccelerator() {
			return KeyStroke.getKeyStroke(KeyEvent.VK_W, ACCELERATOR_MODIFIER);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			int answer = JOptionPane.showConfirmDialog(
					GUI.getInstance(),
					"Do you really want to close the current scenarion?",
					"Close Scenario",
					JOptionPane.OK_CANCEL_OPTION);

			if (answer == JOptionPane.OK_OPTION) {
				scenario.setNetworkStack(null);
			}
		}
	}

	/**
	 * {@link FileMenuItem} to quit ALEVIN.
	 * 
	 * @author Philip Huppert
	 */
	private class Quit extends FileMenuItem {
		@Override
		public String getText() {
			return "Quit";
		}

		@Override
		public ImageIcon getIcon() {
			return Resources.getIconByName("/actions/system-log-out.png");
		}

		@Override
		public KeyStroke getAccelerator() {
			return KeyStroke.getKeyStroke(KeyEvent.VK_Q, ACCELERATOR_MODIFIER);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			int answer = JOptionPane.showConfirmDialog(
					GUI.getInstance(),
					"Do you really want to close the current scenario and quit ALEVIN?",
					"Quit",
					JOptionPane.OK_CANCEL_OPTION);

			if (answer == JOptionPane.OK_OPTION) {
				System.exit(0);
			}
		}
	}
}
