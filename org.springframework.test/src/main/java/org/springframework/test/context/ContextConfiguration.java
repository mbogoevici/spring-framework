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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@code ContextConfiguration} defines class-level metadata that is
 * used to determine how to load and configure an
 * {@link org.springframework.context.ApplicationContext ApplicationContext}
 * for test classes.
 * 
 * <p>Prior to Spring 3.1, only path-based resource locations were supported.
 * As of Spring 3.1, {@link #loader context loaders} may choose to support
 * either path-based or class-based resources (but not both). Consequently
 * {@code @ContextConfiguration} can be used to declare either path-based
 * resource locations (via the {@link #locations} or {@link #value}
 * attribute) <i>or</i> configuration classes (via the {@link #classes}
 * attribute).
 * 
 * @author Sam Brannen
 * @since 2.5
 * @see ContextLoader
 * @see SmartContextLoader
 * @see org.springframework.context.ApplicationContext
 * @see ActiveProfiles
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ContextConfiguration {

	/**
	 * Alias for {@link #locations() locations}.
	 * 
	 * <p>This attribute may <strong>not</strong> be used in conjunction
	 * with {@link #locations} or {@link #classes}, but it may be used
	 * instead of {@link #locations}.
	 * @since 3.0
	 */
	String[] value() default {};

	/**
	 * The resource locations to use for loading an
	 * {@link org.springframework.context.ApplicationContext ApplicationContext}.
	 * 
	 * <p>Check out the javadoc for
	 * {@link org.springframework.test.context.support.AbstractContextLoader#modifyLocations
	 * AbstractContextLoader.modifyLocations()} for details on how a location
	 * String will be interpreted at runtime, in particular in case of a relative
	 * path. Also, check out the documentation on
	 * {@link org.springframework.test.context.support.AbstractContextLoader#generateDefaultLocations
	 * AbstractContextLoader.generateDefaultLocations()} for details on the default
	 * locations that are going to be used if none are specified.
	 * 
	 * <p>Note that the above-mentioned default rules only apply for a standard
	 * {@link org.springframework.test.context.support.AbstractContextLoader
	 * AbstractContextLoader} subclass such as
	 * {@link org.springframework.test.context.support.GenericXmlContextLoader
	 * GenericXmlContextLoader} which is the effective default implementation
	 * used at runtime.
	 * 
	 * <p>This attribute may <strong>not</strong> be used in conjunction with
	 * {@link #value} or {@link #classes}, but it may be used instead of
	 * {@link #value}.
	 * @since 2.5
	 */
	String[] locations() default {};

	/**
	 * The {@link org.springframework.context.annotation.Configuration configuration classes}
	 * to use for loading an
	 * {@link org.springframework.context.ApplicationContext ApplicationContext}.
	 * 
	 * <p>To enable support for configuration class processing, an appropriate
	 * {@link SmartContextLoader} must be {@link #loader configured}.
	 * {@link org.springframework.test.context.support.AnnotationConfigContextLoader
	 * AnnotationConfigContextLoader} is one such loader provided by the Spring Framework.
	 * 
	 * <p>Check out the javadoc for
	 * {@link org.springframework.test.context.support.AnnotationConfigContextLoader#generateDefaultConfigurationClasses
	 * AnnotationConfigContextLoader.generateDefaultConfigurationClasses()}
	 * for details on the default configuration classes that will be used if none
	 * are specified.
	 * 
	 * <p>Note that this attribute may <strong>not</strong> be used in
	 * conjunction with {@link #locations} or {@link #value}.
	 * @since 3.1
	 * @see org.springframework.context.annotation.Configuration
	 * @see org.springframework.test.context.support.AnnotationConfigContextLoader
	 */
	Class<?>[] classes() default {};

	/**
	 * Whether or not {@link #locations resource locations} or
	 * {@link #classes configuration classes} from superclasses should be
	 * <em>inherited</em>.
	 * 
	 * <p>The default value is <code>true</code>, which means that an annotated
	 * class will <em>inherit</em> the resource locations or configuration
	 * classes defined by an annotated superclass. Specifically, the resource
	 * locations or configuration classes for an annotated class will be
	 * appended to the list of resource locations or configuration classes
	 * defined by an annotated superclass. Thus, subclasses have the option of
	 * <em>extending</em> the list of resource locations or configuration
	 * classes.
	 * 
	 * <p>If <code>inheritLocations</code> is set to <code>false</code>, the
	 * resource locations or configuration classes for the annotated class
	 * will <em>shadow</em> and effectively replace any resource locations 
	 * or configuration classes defined by a superclass.
	 * 
	 * <p>In the following example that uses path-based resource locations, the
	 * {@link org.springframework.context.ApplicationContext ApplicationContext}
	 * for {@code ExtendedTest} will be loaded from
	 * &quot;base-context.xml&quot; <strong>and</strong>
	 * &quot;extended-context.xml&quot;, in that order. Beans defined in
	 * &quot;extended-context.xml&quot; may therefore override those defined in
	 * &quot;base-context.xml&quot;.
	 * <pre class="code">
	 * &#064;ContextConfiguration(&quot;base-context.xml&quot;)
	 * public class BaseTest {
	 *     // ...
	 * }
	 * 
	 * &#064;ContextConfiguration(&quot;extended-context.xml&quot;)
	 * public class ExtendedTest extends BaseTest {
	 *     // ...
	 * }
	 * </pre>
	 * 
	 * <p>Similarly, in the following example that uses configuration
	 * classes, the
	 * {@link org.springframework.context.ApplicationContext ApplicationContext}
	 * for {@code ExtendedTest} will be loaded from the
	 * {@code BaseConfig} <strong>and</strong> {@code ExtendedConfig} 
	 * configuration classes, in that order. Beans defined in
	 * {@code ExtendedConfig} may therefore override those defined in
	 * {@code BaseConfig}.
	 * <pre class="code">
	 * &#064;ContextConfiguration(classes=BaseConfig.class, loader=AnnotationConfigContextLoader.class)
	 * public class BaseTest {
	 *     // ...
	 * }
	 * 
	 * &#064;ContextConfiguration(classes=ExtendedConfig.class, loader=AnnotationConfigContextLoader.class)
	 * public class ExtendedTest extends BaseTest {
	 *     // ...
	 * }
	 * </pre>
	 * @since 2.5
	 */
	boolean inheritLocations() default true;

	// TODO Update regarding default --> DelegatingSmartContextLoader
	/**
	 * The type of {@link ContextLoader} (or {@link SmartContextLoader}) to use
	 * for loading an {@link org.springframework.context.ApplicationContext
	 * ApplicationContext}.
	 * 
	 * <p>If not specified, the loader will be inherited from the first superclass
	 * that is annotated with {@code @ContextConfiguration} and specifies an
	 * explicit loader. If no class in the hierarchy specifies an explicit
	 * loader, a default loader will be used instead.
	 * 
	 * <p>The default concrete implementation chosen at runtime will be
	 * {@link org.springframework.test.context.support.GenericXmlContextLoader
	 * GenericXmlContextLoader}. Also check out
	 * {@link org.springframework.test.context.support.AbstractContextLoader
	 * AbstractContextLoader}'s javadoc for details on the default behavior there.
	 * @since 2.5
	 */
	Class<? extends ContextLoader> loader() default ContextLoader.class;

}
