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
package mulavito.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * Scanner utility for scanning across loadable classes at run time.
 * 
 * @author Julian Ott
 * @author Michael Duelli
 * @since 2011-02-24
 */
public final class ClassScanner {
	private ClassScanner() {
		// prevent access
	}

	/**
	 * gets all classes implementing the given interface
	 * 
	 * @param caller
	 *            The class that requests
	 * @param pckgname
	 */
	public static Class<?>[] getClassesImplementing(Class<?> caller,
			String pckgname, Class<?> interf) {
		ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
		try {
			for (Class<?> c : getClasses(caller, pckgname)) {
				for (Class<?> i : c.getInterfaces()) {
					if (i.equals(interf)) {
						classes.add(c);
						break;
					}
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new AssertionError("Class not found.");
		}
		return classes.toArray(new Class[classes.size()]);
	}

	/**
	 * gets all classes that derive the given class or implement the given
	 * interface
	 * 
	 * @param caller
	 *            The class that requests
	 * @param pckgname
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<Class<? extends T>> getDerivates(Class<?> caller,
			String pckgname, Class<T> sup) throws ClassNotFoundException {
		ArrayList<Class<? extends T>> classes = new ArrayList<Class<? extends T>>();
		for (Class<?> c : getClasses(caller, pckgname)) {
			if ((c.getModifiers() & Modifier.ABSTRACT) == 0
					&& sup.isAssignableFrom(c))
				classes.add((Class<? extends T>) c);
		}
		return classes;
	}

	/**
	 * From http://stackoverflow.com/questions/320542/how-to-get-the-path-of-a-
	 * running-jar-file
	 * 
	 * Used to get all classes in the specified package.
	 * 
	 * @param caller
	 *            The class that requests
	 * @param pckgname
	 *            The package name.
	 * @return The classes contained by the specified package.
	 * @throws ClassNotFoundException
	 */
	private static Class<?>[] getClasses(Class<?> caller, String pckgname)
			throws ClassNotFoundException {
		String source = caller.getProtectionDomain().getCodeSource()
				.getLocation().getPath();
		if (source.toLowerCase().endsWith(".jar"))
			try {
				return getClassesFromJARFile(caller.getProtectionDomain()
						.getCodeSource().getLocation().toURI(), pckgname);
			} catch (URISyntaxException e) {
				throw new AssertionError(e);
			}
		else
			return getClassesLocal(pckgname);
	}

	/**
	 * @see http://stackoverflow.com/questions/346811/listing-the-files-in-a-
	 *      directory-of-the-current-jar-file
	 */
	private static Class<?>[] getClassesFromJARFile(URI jar, String packageName)
			throws ClassNotFoundException {
		ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
		JarInputStream jarFile = null;
		try {
			jarFile = new JarInputStream(new FileInputStream(new File(jar)));
			// search all entries
			for (JarEntry jarEntry; (jarEntry = jarFile.getNextJarEntry()) != null;) {
				// match with packet name
				String className = jarEntry.getName();
				if (className.endsWith(".class")) {
					className = className.substring(0,
							className.length() - ".class".length()).replace(
							'/', '.');
					if (className.startsWith(packageName))
						classes.add(Class.forName(className));
				}
			}
		} catch (IOException ex) {
			throw new ClassNotFoundException(ex.getMessage());
		}

		// try to close in any case
		try {
			jarFile.close();
		} catch (IOException ex) {
		}

		return classes.toArray(new Class<?>[classes.size()]);
	}

	/** @see http://forums.sun.com/thread.jspa?threadID=341935 */
	private static Class<?>[] getClassesLocal(String pckgname)
			throws ClassNotFoundException {
		List<Class<?>> classes = new LinkedList<Class<?>>();

		// Get a File object for the package
		File directory = null;
		try {
			ClassLoader cld = Thread.currentThread().getContextClassLoader();
			if (cld == null)
				throw new ClassNotFoundException("Cannot get class loader.");

			String path = pckgname.replace('.', '/');
			URL resource = cld.getResource(path);
			if (resource == null)
				throw new ClassNotFoundException("No resource for " + path);

			directory = new File(resource.toURI());
		} catch (NullPointerException x) {
			throw new ClassNotFoundException(pckgname + " (" + directory
					+ ") does not appear to be a valid package");
		} catch (URISyntaxException e) {
			throw new RuntimeException(pckgname + " could not be located", e);
		}

		if (directory != null && directory.exists()) {
			// Get the list of the files contained in the package
			String[] files = directory.list();
			for (int i = 0; i < files.length; i++) {
				// we are only interested in .class files
				if (files[i].endsWith(".class")) {
					// removes the .class extension
					classes.add(Class.forName(pckgname + '.'
							+ files[i].substring(0, files[i].length() - 6)));
				}
			}
		} else
			throw new ClassNotFoundException(pckgname
					+ " does not appear to be a valid package");

		return classes.toArray(new Class[classes.size()]);
	}
}
