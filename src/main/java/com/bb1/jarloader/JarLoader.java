package com.bb1.jarloader;

import static com.bb1.exceptions.handler.ExceptionHandler.handle;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.bb1.reflection.ClassUtils;

/**
 * 
 * Copyright 2022 BradBot_1
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 * http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * A collection of methods to load a jar file
 * 
 * @author BradBot_1
 */
public final class JarLoader  {
	/**
	 * Loads a jar file into memory
	 * 
	 * @param jarFile The {@link File} to load
	 * @return The {@link LoadedJar} that contains the created {@link ClassLoader}
	 */
	public static final @Nullable LoadedJar load(@NotNull final File jarFile) {
		try {
			final JarFile jar = handle(()->new JarFile(jarFile));
			final URLClassLoader child = new URLClassLoader(new URL[] { handle(()->jarFile.toURI().toURL()) }, JarLoader.class.getClassLoader());
			jar.close();
			return new LoadedJar(jarFile.getAbsolutePath(), child, null);
		} catch (Throwable t) {
			return null;
		}
	}
	/**
	 * Loads a jar file into memory and checks it for annotated classes
	 * 
	 * @param jarFile The {@link File} to load
	 * @param annotation The {@link Annotation} to look for
	 * @return The {@link LoadedJar} that contains the created {@link ClassLoader} and list of annotated classes
	 */
	public static final @Nullable LoadedJar loadAndGetWhereAnnotatedWith(@NotNull final File jarFile, @NotNull final Class<? extends Annotation> annotation) {
		try {
			final Set<Class<?>> annotatedClasses = new HashSet<Class<?>>();
			final JarFile jar = handle(()->new JarFile(jarFile));
			final URLClassLoader child = new URLClassLoader(new URL[] { handle(()->jarFile.toURI().toURL()) }, JarLoader.class.getClassLoader());
			final Enumeration<JarEntry> e = jar.entries();
			while (e.hasMoreElements()) {
				final JarEntry jarEntry = e.nextElement();
				if (jarEntry.getName().endsWith(".class")) {
					Class<?> clazz = handle(()->Class.forName(jarEntry.getName().replace("/", ".").replace(".class", ""), true, child));
					if (clazz.isAnnotationPresent(annotation)) {
						annotatedClasses.add(clazz);
					}
				}
			}
			jar.close();
			return new LoadedJar(jarFile.getAbsolutePath(), child, annotatedClasses.toArray(new Class<?>[annotatedClasses.size()]));
		} catch (Throwable t) {
			return null;
		}
	}
	/**
	 * Loads a jar file into memory and checks it for extended classes
	 * 
	 * @param jarFile The {@link File} to load
	 * @param extendedClass The class that should be extended
	 * @return The {@link LoadedJar} that contains the created {@link ClassLoader} and list of extended classes
	 */
	public static final @Nullable LoadedJar loadAndGetWhereExtends(@NotNull final File jarFile, @NotNull final Class<?> extendedClass) {
		try {
			final Set<Class<?>> extendingClasses = new HashSet<Class<?>>();
			final JarFile jar = handle(()->new JarFile(jarFile));
			final URLClassLoader child = new URLClassLoader(new URL[] { handle(()->jarFile.toURI().toURL()) }, JarLoader.class.getClassLoader());
			final Enumeration<JarEntry> e = jar.entries();
			while (e.hasMoreElements()) {
				final JarEntry jarEntry = e.nextElement();
				if (jarEntry.getName().endsWith(".class")) {
					Class<?> clazz = handle(()->Class.forName(jarEntry.getName().replace("/", ".").replace(".class", ""), true, child));
					if (ClassUtils.doesClassExtend(clazz, extendedClass)) extendingClasses.add(clazz);
				}
			}
			jar.close();
			return new LoadedJar(jarFile.getAbsolutePath(), child, extendingClasses.toArray(new Class<?>[extendingClasses.size()]));
		} catch (Throwable t) {
			return null;
		}
	}
	/**
	 * Loads a jar file into memory and checks it for implemented classes
	 * 
	 * @param jarFile The {@link File} to load
	 * @param extendedClass The interface that should be implemented
	 * @return The {@link LoadedJar} that contains the created {@link ClassLoader} and list of implemented classes
	 */
	public static final @Nullable LoadedJar loadAndGetWhereImplements(@NotNull final File jarFile, @NotNull final Class<?> implementedClass) {
		try {
			final Set<Class<?>> implementedClasses = new HashSet<Class<?>>();
			final JarFile jar = handle(()->new JarFile(jarFile));
			final URLClassLoader child = new URLClassLoader(new URL[] { handle(()->jarFile.toURI().toURL()) }, JarLoader.class.getClassLoader());
			final Enumeration<JarEntry> e = jar.entries();
			while (e.hasMoreElements()) {
				final JarEntry jarEntry = e.nextElement();
				if (jarEntry.getName().endsWith(".class")) {
					Class<?> clazz = handle(()->Class.forName(jarEntry.getName().replace("/", ".").replace(".class", ""), true, child));
					if (clazz.isInstance(implementedClass)) implementedClasses.add(clazz);
				}
			}
			jar.close();
			return new LoadedJar(jarFile.getAbsolutePath(), child, implementedClasses.toArray(new Class<?>[implementedClasses.size()]));
		} catch (Throwable t) {
			return null;
		}
	}
	
}
