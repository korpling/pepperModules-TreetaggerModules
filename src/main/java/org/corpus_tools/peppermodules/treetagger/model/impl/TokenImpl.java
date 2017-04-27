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

import com.google.common.base.Objects;
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
	protected long line = 0;

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
	public long getLine() {
		return line;
	}

	@Override
	public void setLine(long line) {
		this.line = line;
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

	@Override
	public int hashCode() {
		return Objects.hashCode(this.line, this.text, super.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		
		if(obj == null) return false;
		if(getClass() != obj.getClass()) return false;
		if(!super.equals(obj)) return false;
		
		final TokenImpl other = (TokenImpl) obj;
		
		
		return Objects.equal(this.line, other.line)
			&& Objects.equal(this.text, other.text);
	}

	@Override
	public String toString() {
		return text + super.toString();
	}

} // TokenImpl
