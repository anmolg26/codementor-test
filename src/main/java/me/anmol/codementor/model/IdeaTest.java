package me.anmol.codementor.model;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
//A sample J unit test case which could have been written for other classes as well
@RunWith(DataProviderRunner.class)
public class IdeaTest extends TestParent {

	@DataProvider
	public static Object[][] dataSet() {

		List<List<Object>> dataSet = new ArrayList<>();

		dataSet.add(Arrays.asList("This is my idea", 1, 2, 3, 2.0, null));
		dataSet.add(Arrays.asList("This is my idea 2", 1, 1, 1, 1.0, null));
		dataSet.add(Arrays.asList("This is my idea 3", 10, 10, 10, 10.0, null));
		dataSet.add(Arrays.asList("This is my idea 3", 4, 6, 7, 5.666666666666667, null));

		dataSet.add(Arrays.asList(null, 10, 10, 10, 0, InvalidDataException.class));

		dataSet.add(Arrays.asList("This is my idea 4", 0, 2, 3, 0, InvalidDataException.class));
		dataSet.add(Arrays.asList("This is my idea 5", 11, 2, 3, 0, InvalidDataException.class));
		dataSet.add(Arrays.asList("This is my idea 5", 8, 0, 3, 0, InvalidDataException.class));
		dataSet.add(Arrays.asList("This is my idea 6", 8, 11, 3, 0, InvalidDataException.class));
		dataSet.add(Arrays.asList("This is my idea 7", 8, 7, 0, 0, InvalidDataException.class));
		dataSet.add(Arrays.asList("This is my idea 8", 8, 7, 11, 0, InvalidDataException.class));
		dataSet.add(Arrays.asList("", 10, 10, 10, 0, InvalidDataException.class));

		dataSet.add(Arrays.asList(new String(new char[255]).replace('\0', ' '), 10, 10, 10, 10.0, null));
		dataSet.add(Arrays.asList(new String(new char[256]).replace('\0', ' '), 10, 10, 10, 0,
				InvalidDataException.class));

		return listToArray(dataSet);

	}

	@Test
	@UseDataProvider("dataSet")
	public void testIdea(String content, int impact, int ease, int confidence, double average,
			Class<? extends RuntimeException> expectedExceptionClass) {
		try {			
			Idea idea = new Idea(content, impact, ease, confidence, mock(IdeaPoolUser.class));
			isExceptionExpected(expectedExceptionClass);
			assertEquals(content, idea.getContent());
			assertEquals(impact, idea.getImpact());
			assertEquals(ease, idea.getEase());
			assertEquals(confidence, idea.getConfidence());
			assertEquals(average, idea.getAverage_score(), 0.0000000);			
			assertTrue((System.currentTimeMillis() - idea.getCreated_at()) < 100);
		} catch (RuntimeException exception) {
			exception.printStackTrace();
			assertEquals(expectedExceptionClass, exception.getClass());
		}
	}

	@Test
	public void testUpdateSelf() {
		Idea idea = new Idea("My content", 3, 4, 5, mock(IdeaPoolUser.class));
		assertEquals("My content", idea.getContent());
		assertEquals(3, idea.getImpact());
		assertEquals(4, idea.getEase());
		assertEquals(5, idea.getConfidence());
		assertEquals(4.0, idea.getAverage_score(), 0.0000);		
		Idea updatedIdea = idea.updateSelf("My new content",9,6,3);
		assertSame(updatedIdea, idea);
		assertEquals("My new content", idea.getContent());
		assertEquals(9, idea.getImpact());
		assertEquals(6, idea.getEase());
		assertEquals(3, idea.getConfidence());
		assertEquals(6.0, idea.getAverage_score(), 0.0000000);		
	}

}
