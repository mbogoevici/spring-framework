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

package org.springframework.web.servlet.mvc.method.annotation.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.TestBean;
import org.springframework.core.MethodParameter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.ModelAndViewResolver;

/**
 * Test fixture with {@link DefaultMethodReturnValueHandler}.
 * 
 * @author Rossen Stoyanchev
 */
public class DefaultMethodReturnValueHandlerTests {

	private DefaultMethodReturnValueHandler handler;

	private List<ModelAndViewResolver> mavResolvers;

	private ModelAndViewContainer mavContainer;

	private ServletWebRequest request;

	@Before
	public void setUp() {
		mavResolvers = new ArrayList<ModelAndViewResolver>();
		handler = new DefaultMethodReturnValueHandler(mavResolvers);
		mavContainer = new ModelAndViewContainer(new ExtendedModelMap());
		request = new ServletWebRequest(new MockHttpServletRequest());
	}

	@Test
	public void modelAndViewResolver() throws Exception {
		MethodParameter testBeanType = new MethodParameter(getClass().getDeclaredMethod("testBeanReturnValue"), -1);
		mavResolvers.add(new TestModelAndViewResolver(TestBean.class));
		TestBean testBean = new TestBean("name");

		handler.handleReturnValue(testBean, testBeanType, mavContainer, request);
		
		assertEquals("viewName", mavContainer.getViewName());
		assertSame(testBean, mavContainer.getAttribute("modelAttrName"));
		assertTrue(mavContainer.isResolveView());
	}

	@Test(expected=UnsupportedOperationException.class)
	public void modelAndViewResolverUnresolved() throws Exception {
		MethodParameter testBeanType = new MethodParameter(getClass().getDeclaredMethod("testBeanReturnValue"), -1);
		mavResolvers.add(new TestModelAndViewResolver(TestBean.class));
		handler.handleReturnValue(99, testBeanType, mavContainer, request);
	}

	@Test
	public void handleNull() throws Exception {
		handler.handleReturnValue(null, null, mavContainer, request);
		
		assertNull(mavContainer.getView());
		assertNull(mavContainer.getViewName());
		assertTrue(mavContainer.getModel().isEmpty());
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void handleSimpleType() throws Exception {
		MethodParameter intType = new MethodParameter(getClass().getDeclaredMethod("intReturnValue"), -1);
		handler.handleReturnValue(55, intType, mavContainer, request);
	}

	@Test
	public void handleNonSimpleType() throws Exception{
		MethodParameter testBeanType = new MethodParameter(getClass().getDeclaredMethod("testBeanReturnValue"), -1);
		handler.handleReturnValue(new TestBean(), testBeanType, mavContainer, request);
		
		assertTrue(mavContainer.containsAttribute("testBean"));
	}

	@SuppressWarnings("unused")
	private int intReturnValue() {
		return 0;
	}

	@SuppressWarnings("unused")
	private TestBean testBeanReturnValue() {
		return null;
	}
	
	private static class TestModelAndViewResolver implements ModelAndViewResolver {
		
		private Class<?> returnValueType;
		
		public TestModelAndViewResolver(Class<?> returnValueType) {
			this.returnValueType = returnValueType;
		}

		@SuppressWarnings("rawtypes")
		public ModelAndView resolveModelAndView(Method method, Class handlerType, Object returnValue,
				ExtendedModelMap model, NativeWebRequest request) {
			if (returnValue != null && returnValue.getClass().equals(returnValueType)) {
				return new ModelAndView("viewName", "modelAttrName", returnValue);
			}
			else {
				return ModelAndViewResolver.UNRESOLVED;
			}
		}
	}
}