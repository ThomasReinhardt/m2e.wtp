/*******************************************************************************
 * Copyright (c) 2011-2012 Red Hat, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
  *     Fred Bricon (Red Hat, Inc.) - initial API and implementation
 ******************************************************************************/
package org.eclipse.m2e.wtp.jsf.internal.utils;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.jst.jee.util.internal.JavaEEQuickPeek;
import org.eclipse.jst.jee.util.internal.XMLRootHandler;
import org.xml.sax.InputSource;

@SuppressWarnings("restriction")
public class FacesConfigQuickPeek {

	private static final String FACES_CONFIG_PUBLIC_ID_1_0 = "-//Sun Microsystems, Inc.//DTD JavaServer Faces Config 1.0//EN"; //$NON-NLS-1$
	private static final String FACES_CONFIG_SYSTEM_ID_1_0 = "http://java.sun.com/dtd/web-facesconfig_1_0.dtd"; //$NON-NLS-1$
	private static final String FACES_CONFIG_PUBLIC_ID_1_1 = "-//Sun Microsystems, Inc.//DTD JavaServer Faces Config 1.1//EN"; //$NON-NLS-1$
	private static final String FACES_CONFIG_SYSTEM_ID_1_1 = "http://java.sun.com/dtd/web-facesconfig_1_1.dtd"; //$NON-NLS-1$
	private static final String FACES_CONFIG_SCHEMA_ID_1_2 = "http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-facesconfig_1_2.xsd"; //$NON-NLS-1$
	private static final String FACES_CONFIG_SCHEMA_ID_2_0 = "http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-facesconfig_2_0.xsd"; //$NON-NLS-1$
	private static final String FACES_CONFIG_SCHEMA_ID_2_1 = "http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-facesconfig_2_1.xsd"; //$NON-NLS-1$
	private static final String FACES_CONFIG_ID_1_0 = "1.0"; //$NON-NLS-1$
	private static final String FACES_CONFIG_ID_1_1 = "1.1"; //$NON-NLS-1$
	private static final String FACES_CONFIG_ID_1_2 = "1.2"; //$NON-NLS-1$
	private static final String FACES_CONFIG_ID_2_0 = "2.0"; //$NON-NLS-1$
	private static final String FACES_CONFIG_ID_2_1 = "2.1"; //$NON-NLS-1$
	
	private XMLRootHandler handler;

	private String storedVersion = null;
	
	private boolean versionSet = false;
	
	public FacesConfigQuickPeek(InputStream in) {
		if (in != null) {
			try {
				InputSource inputSource = new InputSource(in);
				handler = new XMLRootHandler();
				handler.parseContents(inputSource);
			} catch (Exception ex) {
				// ignore
			} finally {
				try {
					in.reset();
				} catch (IOException ex) {
					// ignore
				}
			}
		}
	}
	
	public String getVersion() {
		if (!versionSet) {
			if (handler != null && "faces-config".equals(handler.getRootName())) { //$NON-NLS-1$
				String version = null;
				if (handler.getRootAttributes() != null) {
					version = handler.getRootAttributes().getValue("version"); //$NON-NLS-1$
				}
				if (version == null || version.trim().length() == 0) {
					version = getVersionFromDtdSchema();
				}
				storedVersion = version;
				versionSet = true;
			}
			
		}
		return storedVersion;
	}

	private String getVersionFromDtdSchema() {
		//Algorithm copied from org.eclipse.jst.jee.util.internal.JavaEEQuickPeek
		if (handler == null) {
			return null;
		}
		String publicID = handler.getDtdPublicID();
		String systemID = handler.getDtdSystemID();
		String schemaName = null;
		if (publicID == null || systemID == null) {
			if (handler.getRootAttributes() != null) {
				schemaName = JavaEEQuickPeek.normalizeSchemaLocation(handler.getRootAttributes().getValue("xsi:schemaLocation")); //$NON-NLS-1$
			}
			if (schemaName == null) {
				return null;
			}
		}
		String version = null;
		if (publicID != null && systemID != null) {
			if (publicID.equals(FACES_CONFIG_PUBLIC_ID_1_0) && (systemID.equals(FACES_CONFIG_SYSTEM_ID_1_0))) {
				version = FACES_CONFIG_ID_1_0;
			} else if (publicID.equals(FACES_CONFIG_PUBLIC_ID_1_1) && systemID.equals(FACES_CONFIG_SYSTEM_ID_1_1)) {
				version = FACES_CONFIG_ID_1_1;
			}
		} else if (schemaName != null) {
			if (schemaName.equals(FACES_CONFIG_SCHEMA_ID_1_2)) {
				version = FACES_CONFIG_ID_1_2;
			} else if (schemaName.equals(FACES_CONFIG_SCHEMA_ID_2_0)) {
				version = FACES_CONFIG_ID_2_0;
			} else if (schemaName.equals(FACES_CONFIG_SCHEMA_ID_2_1)) {
				version = FACES_CONFIG_ID_2_1;
			} 
		}
		return version;
	}
}
