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

import java.util.ArrayList;
import java.util.List;

import org.corpus_tools.peppermodules.treetagger.model.Annotation;
import org.corpus_tools.peppermodules.treetagger.model.Document;
import org.corpus_tools.peppermodules.treetagger.model.LemmaAnnotation;
import org.corpus_tools.peppermodules.treetagger.model.POSAnnotation;
import org.corpus_tools.peppermodules.treetagger.model.Span;
import org.corpus_tools.peppermodules.treetagger.model.Token;

public class TokenImpl extends AnnotatableElementImpl implements Token {

	protected String text = null;

	protected List<Span> spans = new ArrayList<>();
	protected Document document = null;

	protected TokenImpl() {
		super();
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public void setText(String newText) {
		text = newText;
	}

	@Override
	public POSAnnotation getPosAnnotation() {
		POSAnnotation posAnno = null;
		for (Annotation anno : this.getAnnotations()) {
			if (anno instanceof POSAnnotation)
				posAnno = (POSAnnotation) anno;
		}
		return (posAnno);
	}

	@Override
	public void setPosAnnotation(POSAnnotation newPosAnnotation) {
		this.getAnnotations().add(newPosAnnotation);
	}

	@Override
	public LemmaAnnotation getLemmaAnnotation() {
		LemmaAnnotation lemmaAnno = null;
		for (Annotation anno : this.getAnnotations()) {
			if (anno instanceof LemmaAnnotation)
				lemmaAnno = (LemmaAnnotation) anno;
		}
		return (lemmaAnno);
	}

	@Override
	public void setLemmaAnnotation(LemmaAnnotation newLemmaAnnotation) {
		this.getAnnotations().add(newLemmaAnnotation);
	}

	@Override
	public Document getDocument() {
		return document;
	}

	@Override
	public void setDocument(Document newDocument) {
		document = newDocument;
	}

	@Override
	public List<Span> getSpans() {
		return spans;
	}

	/**
	 * Checks this and given object for equality. Conditions for equality:
	 * Object must be instance of Span, have the same name as this,
	 * getSpans().size() must be equal, all Spans must correspond and
	 * annotations must be equal.
	 * 
	 * @param obj
	 *            An object
	 * @return true or false
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Token)) {
			return false;
		}
		Token tok = (Token) obj;

		// ##### compare text #####
		if (((this.getText() != null) && (!(this.getText().equals(tok.getText()))))
				|| ((tok.getText() != null) && (!(tok.getText().equals(this.getText()))))) {
			return false;
		}

		// ##### compare span count #####
		if (this.getSpans().size() != tok.getSpans().size()) {
			return false;
		}

		// ##### compare spans #####
		for (int i = 0; i < this.getSpans().size(); i++) {
			if (!(this.getSpans().get(i).equals(tok.getSpans().get(i)))) {
				return false;
			}
		}

		// okay fine, check super to compare Annotations
		return super.equals(obj);
	}

} // TokenImpl
