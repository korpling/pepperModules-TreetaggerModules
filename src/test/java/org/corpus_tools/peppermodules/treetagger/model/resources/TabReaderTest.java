package org.corpus_tools.peppermodules.treetagger.model.resources;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.corpus_tools.peppermodules.treetagger.model.Token;
import org.corpus_tools.peppermodules.treetagger.model.TreetaggerFactory;
import org.junit.Before;
import org.junit.Test;

public class TabReaderTest {

	private TabReader fixture;

	@Before
	public void beforeEach() {
		fixture = new TabReader();
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
		assertThat(fixture.findColumnName(3)).isEqualTo(TabReader.DEFAULT_ANNOTATION_NAME);
	}

	@Test
	public void whenCreatingAnnotationsForToken_thenTokenShouldContainAllAnnotations() {
		fixture.setColumnNames(Arrays.asList("tok", "pos", "lemma"));
		Token token = TreetaggerFactory.eINSTANCE.createToken();
		String[] tuple = { "TreeTagger", "NP", "TreeTagger", "additionalColumn1", "additionalColumn2" };

		fixture.createAnnotationsForToken(token, tuple);

		assertThat(token.getAnnotations()).hasSize(4);
		assertThat(token.getAnnotations().get(0).getName()).isEqualTo(TabReader.NAME_POS);
		assertThat(token.getAnnotations().get(0).getValue()).isEqualTo("NP");
		assertThat(token.getAnnotations().get(1).getName()).isEqualTo(TabReader.NAME_LEMMA);
		assertThat(token.getAnnotations().get(1).getValue()).isEqualTo("TreeTagger");
		assertThat(token.getAnnotations().get(2).getName()).isEqualTo(TabReader.DEFAULT_ANNOTATION_NAME);
		assertThat(token.getAnnotations().get(2).getValue()).isEqualTo("additionalColumn1");
		assertThat(token.getAnnotations().get(3).getName()).isEqualTo(TabReader.DEFAULT_ANNOTATION_NAME);
		assertThat(token.getAnnotations().get(3).getValue()).isEqualTo("additionalColumn2");
	}
}
