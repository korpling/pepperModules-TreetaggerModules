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
package org.corpus_tools.peppermodules.treetagger.model.serialization.deserializer;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.corpus_tools.peppermodules.treetagger.model.Document;
import org.corpus_tools.peppermodules.treetagger.model.Token;
import org.corpus_tools.peppermodules.treetagger.model.TreetaggerFactory;
import org.corpus_tools.peppermodules.treetagger.model.impl.Treetagger;
import org.eclipse.emf.common.util.URI;
import org.junit.Before;
import org.junit.Test;

public class DeserializerTest {

	private Deserializer fixture;

	@Before
	public void beforeEach() {
		fixture = new Deserializer();
	}

	@Test
	public void whenSettingColumnNamesToNull_thenAnnotationOrderShouldBeEmptyList() {
		fixture.setColumnNames(null);

		assertThat(fixture.columnNames).isEmpty();
	}

	@Test
	public void whenTupleHasLessColumnsThanAnnotationOrderList_thenColumnsShallBeListed() {
		fixture.setColumnNames(Arrays.asList("tok", "pos", "lemma"));
		fixture.lineNumber++;
		fixture.doesTupleHasExpectedNumOfColumns("The", "DT", "the");
		fixture.lineNumber++;
		fixture.doesTupleHasExpectedNumOfColumns("TreeTagger", "NP");
		fixture.lineNumber++;
		fixture.doesTupleHasExpectedNumOfColumns("is", "VBZ");
		fixture.lineNumber++;
		fixture.doesTupleHasExpectedNumOfColumns("easy", "JJ", "easy ");

		assertThat(fixture.rowsWithTooLessColumns).containsExactly(2, 3);
	}

	@Test
	public void whenTupleHasMoreColumnsThanAnnotationOrderList_thenColumnsShallBeListed() {
		fixture.setColumnNames(Arrays.asList("tok", "pos", "lemma"));
		fixture.lineNumber++;
		fixture.doesTupleHasExpectedNumOfColumns("The", "DT", "the");
		fixture.lineNumber++;
		fixture.doesTupleHasExpectedNumOfColumns("TreeTagger", "NP", "TreeTagger", "additionalColumn");
		fixture.lineNumber++;
		fixture.doesTupleHasExpectedNumOfColumns("is", "VBZ", "be ", "additionalColumn");
		fixture.lineNumber++;
		fixture.doesTupleHasExpectedNumOfColumns("easy", "JJ", "easy ");

		assertThat(fixture.rowsWithTooMuchColumns).containsExactly(2, 3);
	}

	@Test
	public void whenFindingColumnNameForLineNumber_thenReturnCorrectColumnName() {
		fixture.setColumnNames(Arrays.asList("tok", "pos", "lemma"));

		assertThat(fixture.findColumnName(0)).isEqualTo("tok");
		assertThat(fixture.findColumnName(1)).isEqualTo("pos");
		assertThat(fixture.findColumnName(2)).isEqualTo("lemma");
		assertThat(fixture.findColumnName(3)).isEqualTo(Deserializer.DEFAULT_ANNOTATION_NAME);
	}

	@Test
	public void whenCreatingAnnotationsForToken_thenTokenShouldContainAllAnnotations() {
		fixture.setColumnNames(Arrays.asList("tok", "pos", "lemma"));
		Token token = TreetaggerFactory.eINSTANCE.createToken();
		String[] tuple = { "TreeTagger", "NP", "TreeTagger", "additionalColumn1", "additionalColumn2" };

		fixture.createAnnotationsForToken(token, tuple);

		assertThat(token.getAnnotations()).hasSize(4);
		assertThat(token.getAnnotations().get(0).getName()).isEqualTo(Deserializer.COLUMN_POS);
		assertThat(token.getAnnotations().get(0).getValue()).isEqualTo("NP");
		assertThat(token.getAnnotations().get(1).getName()).isEqualTo(Deserializer.COLUMN_LEMMA);
		assertThat(token.getAnnotations().get(1).getValue()).isEqualTo("TreeTagger");
		assertThat(token.getAnnotations().get(2).getName()).isEqualTo(Deserializer.DEFAULT_ANNOTATION_NAME);
		assertThat(token.getAnnotations().get(2).getValue()).isEqualTo("additionalColumn1");
		assertThat(token.getAnnotations().get(3).getName()).isEqualTo(Deserializer.DEFAULT_ANNOTATION_NAME);
		assertThat(token.getAnnotations().get(3).getValue()).isEqualTo("additionalColumn2");
	}

	@Test
	public void whenDeserializingTreetaggerFile_thenModelShouldBeEqualToExpected() {
		final URI treetaggerFile = URI
				.createFileURI("./src/test/resources/deserializer/simpleDocument/onlyTokPosAndLemma.tt");
		final Document expectedModel = Treetagger.buildDocument().withName("onlyTokPosAndLemma")
				.withToken(Treetagger.buildToken().withLine(1).withText("The").withAnnotation("pos", "DT")
						.withAnnotation("lemma", "the").build())
				.withToken(Treetagger.buildToken().withLine(2).withText("TreeTagger").withAnnotation("pos", "NP")
						.withAnnotation("lemma", "TreeTagger").build())
				.withToken(Treetagger.buildToken().withLine(3).withText("is").withAnnotation("pos", "VBZ")
						.withAnnotation("lemma", "be").build())
				.withToken(Treetagger.buildToken().withLine(4).withText("easy").withAnnotation("pos", "JJ")
						.withAnnotation("lemma", "easy").build())
				.withToken(Treetagger.buildToken().withLine(5).withText("to").withAnnotation("pos", "TO")
						.withAnnotation("lemma", "to").build())
				.withToken(Treetagger.buildToken().withLine(6).withText("use").withAnnotation("pos", "VB")
						.withAnnotation("lemma", "use").build())
				.withToken(Treetagger.buildToken().withLine(7).withText(".").withAnnotation("pos", "SENT")
						.withAnnotation("lemma", ".").build())
				.build();

		final List<Document> actualModels = Treetagger.deserialize().from(treetaggerFile);

		assertThat(actualModels).hasSize(1);
		final Document actualModel = actualModels.get(0);
		assertThat(actualModel).isEqualTo(expectedModel);
	}

	@Test
	public void whenDeserializingTreetaggerFileWithMultipleDOcuments_thenModelShouldBeEqualToExpected() {
		final URI treetaggerFile = URI
				.createFileURI("./src/test/resources/deserializer/fileWithMultipleDocuments/englishGerman.tt");
		final Document expectedModel_en = Treetagger.buildDocument().withName("englishGerman_1")
				.withAnnotation("lang", "en")
				.withToken(Treetagger.buildToken().withLine(2).withText("The").withAnnotation("pos", "DT")
						.withAnnotation("lemma", "the").build())
				.withToken(Treetagger.buildToken().withLine(3).withText("TreeTagger").withAnnotation("pos", "NP")
						.withAnnotation("lemma", "TreeTagger").build())
				.withToken(Treetagger.buildToken().withLine(4).withText("is").withAnnotation("pos", "VBZ")
						.withAnnotation("lemma", "be").build())
				.withToken(Treetagger.buildToken().withLine(5).withText("easy").withAnnotation("pos", "JJ")
						.withAnnotation("lemma", "easy").build())
				.withToken(Treetagger.buildToken().withLine(6).withText("to").withAnnotation("pos", "TO")
						.withAnnotation("lemma", "to").build())
				.withToken(Treetagger.buildToken().withLine(7).withText("use").withAnnotation("pos", "VB")
						.withAnnotation("lemma", "use").build())
				.withToken(Treetagger.buildToken().withLine(8).withText(".").withAnnotation("pos", "SENT")
						.withAnnotation("lemma", ".").build())
				.build();
		final Document expectedModel_de = Treetagger.buildDocument().withName("englishGerman_2")
				.withAnnotation("lang", "de")
				.withToken(Treetagger.buildToken().withLine(11).withText("Der").withAnnotation("pos", "NP")
						.withAnnotation("lemma", "Der").build())
				.withToken(Treetagger.buildToken().withLine(12).withText("TreeTagger").withAnnotation("pos", "NP")
						.withAnnotation("lemma", "TreeTagger").build())
				.withToken(Treetagger.buildToken().withLine(13).withText("ist").withAnnotation("pos", "NP")
						.withAnnotation("lemma", "sein").build())
				.withToken(Treetagger.buildToken().withLine(14).withText("einfach").withAnnotation("pos", "NP")
						.withAnnotation("lemma", "einfach").build())
				.withToken(Treetagger.buildToken().withLine(15).withText("zu").withAnnotation("pos", "NP")
						.withAnnotation("lemma", "zu").build())
				.withToken(Treetagger.buildToken().withLine(16).withText("nutzen").withAnnotation("pos", "NP")
						.withAnnotation("lemma", "nutzen").build())
				.withToken(Treetagger.buildToken().withLine(17).withText(".").withAnnotation("pos", "SENT")
						.withAnnotation("lemma", ".").build())
				.build();

		final List<Document> actualModels = Treetagger.deserialize().from(treetaggerFile);

		assertThat(actualModels).hasSize(2);
		assertThat(actualModels.get(0)).isEqualTo(expectedModel_en);
		assertThat(actualModels.get(1)).isEqualTo(expectedModel_de);
	}
}
