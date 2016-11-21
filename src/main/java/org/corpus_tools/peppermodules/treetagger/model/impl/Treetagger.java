package org.corpus_tools.peppermodules.treetagger.model.impl;

import org.corpus_tools.peppermodules.treetagger.model.Annotation;
import org.corpus_tools.peppermodules.treetagger.model.Document;
import org.corpus_tools.peppermodules.treetagger.model.Token;
import org.corpus_tools.peppermodules.treetagger.model.TreetaggerFactory;
import org.corpus_tools.peppermodules.treetagger.model.serialization.deserializer.Deserializer;
import org.corpus_tools.peppermodules.treetagger.model.serialization.deserializer.Deserializer.Builder;

public class Treetagger {

	public static DocumentBuilder buildDocument() {
		return new DocumentBuilder();
	}

	public static TokenBuilder buildToken() {
		return new TokenBuilder();
	}

	public static Builder deserialize() {
		return new Deserializer.Builder();
	}

	public static class DocumentBuilder {
		private final Document document = TreetaggerFactory.eINSTANCE.createDocument();

		public DocumentBuilder withAnnotation(String name, String value) {
			document.getAnnotations().add(TreetaggerFactory.eINSTANCE.createAnnotation(name, value));
			return this;
		}

		public DocumentBuilder withName(String name) {
			document.setName(name);
			return this;
		}

		public DocumentBuilder withToken(Token token) {
			document.getTokens().add(token);
			return this;
		}

		public Document build() {
			return document;
		}
	}

	public static class TokenBuilder {
		private final Token token = TreetaggerFactory.eINSTANCE.createToken();

		public TokenBuilder withText(String text) {
			token.setText(text);
			return this;
		}

		public TokenBuilder withAnnotation(Annotation annotation) {
			token.getAnnotations().add(annotation);
			return this;
		}

		public TokenBuilder withAnnotation(String name, String value) {
			return withAnnotation(TreetaggerFactory.eINSTANCE.createAnnotation(name, value));
		}

		public Token build() {
			return token;
		}
	}

}
