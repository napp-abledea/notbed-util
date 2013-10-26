/**
 *
 */
package com.notbed.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.apache.commons.logging.Log;

/**
 * @author Alexandru Bledea
 * @since Oct 2, 2013
 */
public class UReflection {

	private static final String SEPARATOR = "/";
	private static final String DOT = ".";
	private static final String DOLLAR = "$";
	private static final String CLASS_ENDING = ".class";

	/**
	 * @param relevantClass
	 * @param annotationClass
	 * @param log
	 * @return
	 */
	public static Class[] tryGetClassesWithAnnotation(Class relevantClass, Class annotationClass, Log log) {
		return tryGetClassesWithAnnotation(relevantClass, relevantClass.getPackage().getName(), annotationClass, log);
	}

	/**
	 * @param relevantClass
	 * @param packageName
	 * @param annotationClass
	 * @param log
	 * @return
	 */
	public static Class[] tryGetClassesWithAnnotation(Class relevantClass, String packageName, Class annotationClass, Log log) {
		Collection<Class> classes = new ArrayList();
		Class[] tryLoadClasses = tryLoadClasses(relevantClass, packageName, log);
		for (Class class1 : tryLoadClasses) {
			if (class1.isAnnotationPresent(annotationClass)) {
				classes.add(class1);
			}
		}
		return classes.toArray(new Class[classes.size()]);
	}

	/**
	 * @param relevantClass
	 * @param log
	 * @return
	 */
	public static Class[] tryLoadClasses(Class relevantClass, Log log) {
		if (relevantClass == null) {
			log.debug("No class provided.");
			return new Class[0];
		}
		Package classPackage = relevantClass.getPackage();
		if (classPackage == null) {
			log.debug("Class doesn't have a package?");
			return new Class[0];
		}
		return tryLoadClasses(relevantClass, classPackage.getName(), log);
	}

	/**
	 * @param relevantClass
	 * @param packageName
	 * @param log
	 * @return
	 */
	public static Class[] tryLoadClasses(Class relevantClass, String packageName, Log log) {
		if (relevantClass == null || packageName == null) {
			log.debug("No class or package provided.");
			return new Class[0];
		}
		packageName = packageName.replace(DOT, SEPARATOR);
		if (!packageName.endsWith(SEPARATOR)) {
			packageName += SEPARATOR;
		}

		ClassLoader classLoader = relevantClass.getClassLoader();
		String[] classesFromJar = getClassesFromJar(relevantClass, log);
		Collection<Class> classes = getClasses(classesFromJar, false, packageName, classLoader, log);

		String[] classesFromClassLoader = getClassesFromClassLoader(relevantClass, log);
		classes.addAll(getClasses(classesFromClassLoader, false, packageName, classLoader, log));
		return classes.toArray(new Class[classes.size()]);
	}

	/**
	 * @param classNames
	 * @param includeChildren
	 * @param packageName
	 * @param classLoader
	 * @param log
	 * @return
	 */
	private static Collection<Class> getClasses(String[] classNames, boolean includeChildren, String packageName, ClassLoader classLoader, Log log) {
		Collection<Class> classes = new HashSet();
		for (String string : classNames) {
			Class<?> class1 = getClass(string, packageName, includeChildren, classLoader, log);
			if (class1 != null) {
				classes.add(class1);
			}
		}
		return classes;
	}

	/**
	 * @param classFromJar
	 * @param log
	 * @return
	 */
	private static String[] getClassesFromJar(Class classFromJar, Log log) {
		try {
			ProtectionDomain protectionDomain = classFromJar.getProtectionDomain();
			if (protectionDomain != null) {
				CodeSource codeSource = protectionDomain.getCodeSource();
				if (codeSource != null) {
					return getClassesFromJar(codeSource.getLocation(), log);
				}
			}
		} catch (Exception e) {
			log.error("Failed to get contents from the jar", e);
		}

		return new String[0];
	}

	/**
	 * @param location
	 * @param log
	 * @return
	 * @throws IOException
	 */
	private static String[] getClassesFromJar(URL location, Log log) throws IOException {
		final Collection<String> filenames = new HashSet();

		JarInputStream jarFile = null;
		try {
			jarFile = new JarInputStream(location.openStream());
			JarEntry jarEntry;
			while ((jarEntry = jarFile.getNextJarEntry()) != null) {
				String name = jarEntry.getName();
				if (!name.contains(DOLLAR) && name.endsWith(CLASS_ENDING)) {
					filenames.add(name);
				}
			}
		} finally {
			if (jarFile != null) {
				try {
					jarFile.close();
				} catch (IOException e) {
					log.error("An error occured while trying to close the jar.", e);
				}
			}
		}
		return filenames.toArray(new String[filenames.size()]);
	}

	/**
	 * @param fileName
	 * @param packageSlash
	 * @param includeChildren
	 * @param classLoader
	 * @param log
	 * @return
	 */
	private static Class getClass(String fileName, String packageSlash, boolean includeChildren, ClassLoader classLoader, Log log) {

		if (fileName.endsWith(CLASS_ENDING) && !fileName.contains(DOLLAR)) {
			if (fileName.startsWith(packageSlash)) {
				if (!includeChildren) {
					String searchForChildren = remove(fileName, packageSlash);
					if (searchForChildren.contains(SEPARATOR)) {
						return null; // from a sub package
					}
				}
				fileName = remove(fileName, CLASS_ENDING);
				fileName = replace(fileName, SEPARATOR, DOT);
				try {
					return Class.forName(fileName, false, classLoader);
				} catch (ClassNotFoundException e) {
					log.warn("Cannot load class " + fileName, e);
				}
			}
		}
		return null;
	}

	/**
	 * @param classFromClassLoader
	 * @param log
	 * @return
	 */
	private static String[] getClassesFromClassLoader(Class classFromClassLoader, Log log) {
		Collection<String> newClasses = new ArrayList();
		try {
			ClassLoader classLoader = classFromClassLoader.getClassLoader();
			String packageName = classFromClassLoader.getPackage().getName();
			String path = packageName.replace('.', '/');
			Enumeration<URL> resources = classLoader.getResources(path);
			List<File> dirs = new ArrayList<File>();
			while (resources.hasMoreElements()) {
				dirs.add(new File(resources.nextElement().getFile()));
			}
			Collection<String> classes = new ArrayList();
			for (File directory : dirs) {
				classes.addAll(getAllClasses(directory));
			}
			for (String string : classes) {
				int indexOf = string.lastIndexOf(path);
				if (indexOf != -1) {
					newClasses.add(string.substring(indexOf, string.length()));
				}
			}
		} catch (IOException e) {
			log.error("An error occurred while reading from classloader", e);
		}

		return newClasses.toArray(new String[newClasses.size()]);
	}

	/**
	 * @param dir
	 * @return
	 */
	private static Collection<String> getAllClasses(File dir) {
		Collection<String> classes = new ArrayList();
		File[] files = dir.listFiles();
		for (File file : files) {
			String name = file.getName();
			if (file.isDirectory()) {
				classes.addAll(getAllClasses(file));
			} else if (file.isFile() && !name.contains(DOLLAR) && name.endsWith(CLASS_ENDING)) {
				name = replace(file.getAbsolutePath(), "\\", "/");
				classes.add(name);
			}
		}
		return classes;
	}

	/**
	 * @param target
	 * @param what
	 * @param withWhat
	 * @return
	 */
	private static String replace(String target, String what, String withWhat) {
		return target.replace(what, withWhat);
	}

	/**
	 * @param target
	 * @param what
	 * @param withWhat
	 * @return
	 */
	private static String remove(String target, String what) {
		return replace(target, what, "");
	}
}