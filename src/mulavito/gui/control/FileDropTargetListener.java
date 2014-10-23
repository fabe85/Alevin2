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
package mulavito.gui.control;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Abstract listener which accepts a multi-os file list
 * 
 * @author Julian Ott
 */
public abstract class FileDropTargetListener extends DropTargetAdapter {
	private static final String URI_LIST_MIME_TYPE = "text/uri-list;class=java.lang.String";
	private DataFlavor uriFlavor;

	public FileDropTargetListener() {
		try {
			uriFlavor = new DataFlavor(URI_LIST_MIME_TYPE);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			uriFlavor = null;
		}
	}

	private List<File> textURIListToFileList(String data) {
		List<File> list = new ArrayList<File>(1);
		for (StringTokenizer st = new StringTokenizer(data, "\r\n"); st
				.hasMoreTokens();) {
			String s = st.nextToken();
			if (s.startsWith("#")) {
				// the line is a comment (as per the RFC 2483)
				continue;
			}
			try {
				URI uri = new URI(s);
				File file = new File(uri);
				list.add(file);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void drop(DropTargetDropEvent dtde) {
		try {
			Transferable t = dtde.getTransferable();
			List<File> files = null;
			if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				dtde.acceptDrop(DnDConstants.ACTION_REFERENCE);
				files = (List<File>) t
						.getTransferData(DataFlavor.javaFileListFlavor);
			} else if (uriFlavor != null && t.isDataFlavorSupported(uriFlavor)) {
				dtde.acceptDrop(DnDConstants.ACTION_REFERENCE);
				files = textURIListToFileList((String) t
						.getTransferData(uriFlavor));
			} else
				dtde.rejectDrop();

			if (files != null && files.size() == 1) {
				openFile(files.get(0));
				dtde.dropComplete(true);
			} else
				dtde.dropComplete(false);
		} catch (UnsupportedFlavorException ex) {
			dtde.rejectDrop();
		} catch (IOException e) {
			dtde.rejectDrop();
		}
	}

	@Override
	public void dragOver(DropTargetDragEvent dtde) {
		if ((dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor) || (uriFlavor != null && dtde
				.isDataFlavorSupported(uriFlavor)))
				&& canOpenFile())
			dtde.acceptDrag(DnDConstants.ACTION_REFERENCE);
		else
			dtde.rejectDrag();
	}

	/**
	 * gets the file to open
	 */
	protected abstract void openFile(File file);

	/**
	 * indicates that the application is ready for opening
	 */
	protected abstract boolean canOpenFile();
}
