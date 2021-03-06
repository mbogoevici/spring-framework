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

package org.springframework.test.context;

import org.springframework.context.ApplicationContext;

/**
 * <p>Strategy interface for loading an {@link ApplicationContext application context}
 * for an integration test managed by the Spring TestContext Framework.
 * 
 * <p>The <code>SmartContextLoader</code> SPI supersedes the {@link ContextLoader}
 * SPI introduced in Spring 2.5: <code>SmartContextLoaders</code> can process both
 * resource locations and configuration classes. Furthermore, a
 * <code>SmartContextLoader</code> can set active bean definition profiles
 * in the context that it loads (see {@link MergedContextConfiguration#getActiveProfiles()}
 * and {@link #loadContext(MergedContextConfiguration)}).
 *
 * <p>Clients of a <code>SmartContextLoader</code> should call
 * {@link #processContextConfiguration(ContextConfigurationAttributes) processContextConfiguration()}
 * prior to calling {@link #loadContext(MergedContextConfiguration) loadContext()}
 * in case the <code>SmartContextLoader</code> provides custom support for modifying
 * or generating resource locations or configuration classes. The results of
 * {@link #processContextConfiguration(ContextConfigurationAttributes) processContextConfiguration()}
 * should be merged for all classes in the hierarchy of the root test class and
 * then supplied to {@link #loadContext(MergedContextConfiguration) loadContext()}.
 * Even though <code>SmartContextLoader</code> extends <code>ContextLoader</code>,
 * clients should favor <code>SmartContextLoader</code>-specific methods over those
 * defined in <code>ContextLoader</code>, particularly because a
 * <code>SmartContextLoader</code> may choose not to support methods defined in
 * the <code>ContextLoader</code> SPI.
 *
 * <p>Concrete implementations must provide a <code>public</code> no-args constructor.
 *
 * <p>Spring provides the following out-of-the-box implementations:
 * <ul>
 * <li>{@link org.springframework.test.context.support.GenericXmlContextLoader GenericXmlContextLoader}</li>
 * <li>{@link org.springframework.test.context.support.AnnotationConfigContextLoader AnnotationConfigContextLoader}</li>
 * <li>{@link org.springframework.test.context.support.GenericPropertiesContextLoader GenericPropertiesContextLoader}</li>
 * </ul>
 *
 * @author Sam Brannen
 * @since 3.1
 * @see ContextConfiguration
 * @see ActiveProfiles
 * @see ContextConfigurationAttributes
 * @see MergedContextConfiguration
 */
public interface SmartContextLoader extends ContextLoader {

	/**
	 * Determines if this <code>SmartContextLoader</code> generates default resource
	 * locations or
	 * {@link org.springframework.context.annotation.Configuration configuration classes}
	 * if the <code>locations</code> or <code>classes</code>
	 * present in the {@link ContextConfigurationAttributes} provided to
	 * {@link #processContextConfiguration()} are <code>null</code> or empty.
	 * <p>Returning a value of <code>true</code> signals not only that this
	 * <code>SmartContextLoader</code> generates defaults but also that it will
	 * <em>preemptively</em> verify that a generated default actually exists.
	 * @return <code>true</code> if this <code>SmartContextLoader</code>
	 * generates default configuration locations or classes
	 * @see #processContextConfiguration(ContextConfigurationAttributes)
	 */
	boolean generatesDefaults();

	/**
	 * Processes the {@link ContextConfigurationAttributes} for a given test class.
	 * <p>Concrete implementations may choose to <em>modify</em> the <code>locations</code>
	 * or <code>classes</code> in the supplied {@link ContextConfigurationAttributes}
	 * or <em>generate default</em> configuration locations or classes if the
	 * supplied values are <code>null</code> or empty.
	 * <p><b>Note</b>: If {@link #generatesDefaults()} returns <code>true</code>,
	 * this method <b>must</b> <em>preemptively</em> verify that a generated default
	 * actually exists before setting the corresponding <code>locations</code>
	 * or <code>classes</code> property in the supplied
	 * {@link ContextConfigurationAttributes}. Consequently, leaving the
	 * <code>locations</code> or <code>classes</code> property empty signals that
	 * this <code>SmartContextLoader</code> was not able to generate defaults.
	 * @param configAttributes the context configuration attributes to process
	 * @see #generatesDefaults()
	 */
	void processContextConfiguration(ContextConfigurationAttributes configAttributes);

	/**
	 * TODO Document supports(MergedContextConfiguration).
	 *
	 * @param mergedConfig
	 * @return
	 */
	boolean supports(MergedContextConfiguration mergedConfig);

	/**
	 * Loads a new {@link ApplicationContext context} based on the supplied
	 * {@link MergedContextConfiguration merged context configuration},
	 * configures the context, and finally returns the context in a fully
	 * <em>refreshed</em> state.
	 * <p>Concrete implementations should register annotation configuration
	 * processors with bean factories of
	 * {@link ApplicationContext application contexts} loaded by this
	 * <code>SmartContextLoader</code>. Beans will therefore automatically be
	 * candidates for annotation-based dependency injection using
	 * {@link org.springframework.beans.factory.annotation.Autowired @Autowired},
	 * {@link javax.annotation.Resource @Resource}, and
	 * {@link javax.inject.Inject @Inject}. In addition, concrete implementations
	 * should set the active bean definition profiles in the context's 
	 * {@link org.springframework.core.env.Environment Environment}.
	 * <p>Any <code>ApplicationContext</code> loaded by a
	 * <code>SmartContextLoader</code> <strong>must</strong> register a JVM
	 * shutdown hook for itself. Unless the context gets closed early, all context
	 * instances will be automatically closed on JVM shutdown. This allows for
	 * freeing external resources held by beans within the context (e.g.,
	 * temporary files).
	 * @param mergedConfig the merged context configuration to use to load the
	 * application context
	 * @return a new application context
	 * @throws Exception if context loading failed
	 * @see #processContextConfiguration(ContextConfigurationAttributes)
	 * @see org.springframework.context.annotation.AnnotationConfigUtils#registerAnnotationConfigProcessors()
	 * @see org.springframework.test.context.MergedContextConfiguration#getActiveProfiles()
	 * @see org.springframework.context.ConfigurableApplicationContext#getEnvironment()
	 */
	ApplicationContext loadContext(MergedContextConfiguration mergedConfig) throws Exception;

}
