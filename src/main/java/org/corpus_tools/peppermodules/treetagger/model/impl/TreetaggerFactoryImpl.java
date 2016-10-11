/**
 * Copyright 2009 Humboldt-Universität zu Berlin, INRIA.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */
package org.corpus_tools.peppermodules.treetagger.model.impl;

import org.corpus_tools.peppermodules.treetagger.model.Annotation;
import org.corpus_tools.peppermodules.treetagger.model.AnyAnnotation;
import org.corpus_tools.peppermodules.treetagger.model.Document;
import org.corpus_tools.peppermodules.treetagger.model.LemmaAnnotation;
import org.corpus_tools.peppermodules.treetagger.model.POSAnnotation;
import org.corpus_tools.peppermodules.treetagger.model.Span;
import org.corpus_tools.peppermodules.treetagger.model.Token;
import org.corpus_tools.peppermodules.treetagger.model.TreetaggerFactory;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory</b>. <!--
 * end-user-doc -->
 * 
 * @generated
 */
public class TreetaggerFactoryImpl implements TreetaggerFactory {
	public static TreetaggerFactory init() {
		return new TreetaggerFactoryImpl();
	}

	public TreetaggerFactoryImpl() {
		super();
	}

	@Override
	public Document createDocument() {
		DocumentImpl document = new DocumentImpl();
		return document;
	}

	@Override
	public Token createToken() {
		TokenImpl token = new TokenImpl();
		return token;
	}

	@Override
	public Annotation createAnnotation() {
		AnnotationImpl annotation = new AnnotationImpl();
		return annotation;
	}

	@Override
	public POSAnnotation createPOSAnnotation() {
		POSAnnotationImpl posAnnotation = new POSAnnotationImpl();
		return posAnnotation;
	}

	@Override
	public LemmaAnnotation createLemmaAnnotation() {
		LemmaAnnotationImpl lemmaAnnotation = new LemmaAnnotationImpl();
		return lemmaAnnotation;
	}

	@Override
	public AnyAnnotation createAnyAnnotation() {
		AnyAnnotationImpl anyAnnotation = new AnyAnnotationImpl();
		return anyAnnotation;
	}

	@Override
	public Span createSpan() {
		SpanImpl span = new SpanImpl();
		return span;
	}

}
