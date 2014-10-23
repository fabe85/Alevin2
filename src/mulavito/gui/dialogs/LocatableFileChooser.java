/* ***** BEGIN LICENSE BLOCK *****
 * Copyright (C) 2008-2011, The 100GET-E3-R3G Project Team.
 * 
 * This work has been funded by the Federal Ministry of Education
 * and Research of the Federal Republic of Germany
 * (BMBF Förderkennzeichen 01BP0775). It is part of the EUREKA project
 * "100 Gbit/s Carrier-Grade Ethernet Transport Technologies
 * (CELTIC CP4-001)". The authors alone are responsible for this work.
 *
 * See the file AUTHORS for details and contact information.
 * 
 * This file is part of MuLaViTo (Multi-Layer Visualization Tool).
 *
 * MuLaViTo is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License Version 3 or later
 * (the "GPL"), or the GNU Lesser General Public License Version 3 or later
 * (the "LGPL") as published by the Free Software Foundation.
 *
 * MuLaViTo is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * or the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License and
 * GNU Lesser General Public License along with MuLaViTo; see the file
 * COPYING. If not, see <http://www.gnu.org/licenses/>.
 *
 * ***** END LICENSE BLOCK ***** */
package mulavito.gui.dialogs;

import java.awt.Component;
import java.awt.HeadlessException;
import java.io.File;
import java.util.TreeMap;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

/**
 * <p>
 * An extension of {@link JFileChooser} that stores several categories for the
 * directories last used.
 * </p>
 * 
 * <p>
 * You have to extend this class with some code like the following to add
 * locations:
 * 
 * <pre>
 * static {
 * 	registerDirectory(MyFileChooserType.TOPOLOGIES.ordinal(), new File(System
 * 			.getProperty(&quot;user.dir&quot;)
 * 			+ &quot;/&quot; + &quot;src&quot; + &quot;/&quot; + &quot;topologies&quot; + &quot;/&quot;));
 * }
 * </pre>
 * 
 * </p>
 * 
 * Features:
 * <ul>
 * <li>Remember last working directory
 * <li>Automatically remove useless *.* filter
 * <li>Add "All supported formats" filter
 * </ul>
 * 
 * @author Michael Duelli
 */
@SuppressWarnings("serial")
public class LocatableFileChooser extends JFileChooser {
	private static TreeMap<Integer, File> directories = new TreeMap<Integer, File>();

	protected static void registerDirectory(int key, File directory) {
		if (directory == null)
			throw new IllegalArgumentException();
		directories.put(key, directory);
	}

	private int key;
	private boolean readOnly;

	/**
	 * @param title
	 *            The title of the dialog
	 * @param key
	 *            the directory to be used, previously registered vie
	 *            registerDirectory()
	 * @param readOnly
	 *            If true, a file filter for all supported formats is installed.
	 */
	public LocatableFileChooser(String title, int key, boolean readOnly) {
		setDialogTitle(title);
		setAcceptAllFileFilterUsed(false);

		this.key = key;
		this.readOnly = readOnly;

		File curr = directories.get(key);
		if (curr != null)
			setCurrentDirectory(curr);
		if (this.readOnly)
			setFileFilter(allSupported);
	}

	@Override
	public void addChoosableFileFilter(FileFilter filter) {
		super.addChoosableFileFilter(filter);

		if (readOnly)
			setFileFilter(allSupported);
	}

	@Override
	public int showOpenDialog(Component parent) throws HeadlessException {
		int ret = super.showOpenDialog(parent);

		if (ret == APPROVE_OPTION)
			update();

		return ret;
	}

	@Override
	public int showSaveDialog(Component parent) throws HeadlessException {
		int ret = super.showSaveDialog(parent);

		if (ret == APPROVE_OPTION)
			update();

		return ret;
	}

	private void update() {
		File curr = directories.get(key);
		if (curr != null)
			directories.put(key, this.getSelectedFile());
	}

	private FileFilter allSupported = new FileFilter() {
		@Override
		public boolean accept(File pathname) {
			// Accept all supported file formats.
			for (FileFilter ff : getChoosableFileFilters())
				if (!ff.equals(allSupported) && ff.accept(pathname))
					return true;

			return false;
		}

		@Override
		public String getDescription() {
			return "All supported formats";
		}
	};

	@Override
	public void approveSelection() {
		if (readOnly
				|| !getSelectedFile().exists()
				|| JOptionPane.showConfirmDialog(this,
						"File already exists, overwrite anyway?", "Question",
						JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION)
			super.approveSelection();
	}
}
