/*
 * Copyright 2002-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.test.context.support;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfigurationAttributes;
import org.springframework.test.context.ContextLoader;
import org.springframework.test.context.MergedContextConfiguration;
import org.springframework.test.context.SmartContextLoader;
import org.springframework.util.ObjectUtils;

/**
 * TODO Document DelegatingSmartContextLoader.
 * 
 * @author Sam Brannen
 * @since 3.1
 * @see SmartContextLoader
 * @see GenericXmlContextLoader
 * @see AnnotationConfigContextLoader
 */
public class DelegatingSmartContextLoader implements SmartContextLoader {

	private static final Log logger = LogFactory.getLog(DelegatingSmartContextLoader.class);

	private final List<? extends SmartContextLoader> candidates = Arrays.asList(new GenericXmlContextLoader(),
		new AnnotationConfigContextLoader());


	// --- SmartContextLoader --------------------------------------------------

	/**
	 * TODO Document generatesDefaults() implementation.
	 */
	public boolean generatesDefaults() {
		for (SmartContextLoader loader : candidates) {
			if (loader.generatesDefaults()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * TODO Document processContextConfiguration() implementation.
	 */
	public void processContextConfiguration(ContextConfigurationAttributes configAttributes) {

		final String[] originalLocations = configAttributes.getLocations();
		final Class<?>[] originalClasses = configAttributes.getClasses();
		final boolean emptyResources = ObjectUtils.isEmpty(originalLocations) && ObjectUtils.isEmpty(originalClasses);

		for (SmartContextLoader loader : candidates) {
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("Delegating to %s to process context configuration [%s].",
					loader.getClass().getName(), configAttributes));
			}

			// TODO Implement processContextConfiguration().
			//
			// If the original locations and classes are not empty, there's no
			// need to bother with default generation checks; just let each
			// loader process the configuration.
			if (!emptyResources) {
				loader.processContextConfiguration(configAttributes);
			}
			// Otherwise, if a loader claims to generate defaults, let it
			// process the configuration, and then verify that it actually did
			// generate defaults.
			//
			// If it generated defaults, there's no need to delegate to
			// additional candidates. So:
			// 1) stop iterating
			// 2) mark the current loader as the winning candidate (?)
			// 3) log an info message.
			else {
				if (loader.generatesDefaults()) {
					loader.processContextConfiguration(configAttributes);
				}
			}
		}
		// If any loader claims to generate defaults but none actually did,
		// throw an exception.
	}

	/**
	 * TODO Document supports(MergedContextConfiguration) implementation.
	 */
	public boolean supports(MergedContextConfiguration mergedConfig) {
		for (SmartContextLoader loader : candidates) {
			if (loader.supports(mergedConfig)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * TODO Document loadContext(MergedContextConfiguration) implementation.
	 */
	public ApplicationContext loadContext(MergedContextConfiguration mergedConfig) throws Exception {

		for (SmartContextLoader loader : candidates) {
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("Delegating to %s to load context from [%s].", loader.getClass().getName(),
					mergedConfig));
			}

			// Ask each loader if it can load a context from the mergedConfig.
			// If a loader can, let it; otherwise, continue iterating over all
			// remaining candidates.
			if (loader.supports(mergedConfig)) {
				return loader.loadContext(mergedConfig);
			}
		}

		throw new IllegalStateException(String.format("None of the candidate SmartContextLoaders %s "
				+ "was able to load an ApplicationContext from [%s].", candidates, mergedConfig));
	}

	// --- ContextLoader -------------------------------------------------------

	/**
	 * {@code DelegatingSmartContextLoader} does not support the
	 * {@link ContextLoader#processLocations(Class, String...)} method. Call
	 * {@link #processContextConfiguration(ContextConfigurationAttributes)} instead.
	 * @throws UnsupportedOperationException
	 */
	public String[] processLocations(Class<?> clazz, String... locations) {
		throw new UnsupportedOperationException("DelegatingSmartContextLoader does not support the ContextLoader API. "
				+ "Call processContextConfiguration(ContextConfigurationAttributes) instead.");
	}

	/**
	 * {@code DelegatingSmartContextLoader} does not support the
	 * {@link ContextLoader#loadContext(String...) } method. Call
	 * {@link #loadContext(MergedContextConfiguration)} instead.
	 * @throws UnsupportedOperationException
	 */
	public ApplicationContext loadContext(String... locations) throws Exception {
		throw new UnsupportedOperationException("DelegatingSmartContextLoader does not support the ContextLoader API. "
				+ "Call loadContext(MergedContextConfiguration) instead.");
	}

}
