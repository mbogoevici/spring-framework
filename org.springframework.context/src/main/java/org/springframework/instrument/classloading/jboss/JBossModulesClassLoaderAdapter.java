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

package org.springframework.instrument.classloading.jboss;

import java.lang.instrument.ClassFileTransformer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

/** @author Marius Bogoevici */
public class JBossModulesClassLoaderAdapter implements JBossClassLoaderAdapter {

	private static final String TRANSFORMER_FIELD_NAME = "transformer";

	private static final String TRANSFORMER_ADD_METHOD_NAME = "addTransformer";

	private static final String DELEGATING_TRANSFORMER_CLASS_NAME =
			"org.jboss.as.server.deployment.module.DelegatingClassFileTransformer";

	private ClassLoader loader;

	public JBossModulesClassLoaderAdapter(ClassLoader loader) {
		this.loader = loader;
	}

	public void addTransformer(ClassFileTransformer transformer) {
        try {
            Field transformers = ReflectionUtils.findField(loader.getClass(), TRANSFORMER_FIELD_NAME);
            transformers.setAccessible(true);
            Object delegatingTransformer = transformers.get(loader);
			Assert.state(delegatingTransformer.getClass().getName().equals(DELEGATING_TRANSFORMER_CLASS_NAME),
					"Transformer not of the expected type: "  + delegatingTransformer.getClass().getName());
            Method addTransformer = ReflectionUtils.findMethod(delegatingTransformer.getClass(),
					TRANSFORMER_ADD_METHOD_NAME, ClassFileTransformer.class);
            addTransformer.setAccessible(true);
            addTransformer.invoke(delegatingTransformer, transformer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

	public ClassLoader getClassLoader() {
		return loader;
	}
}
