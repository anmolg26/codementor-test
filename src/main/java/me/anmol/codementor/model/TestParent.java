package me.anmol.codementor.model;

import static org.junit.Assert.fail;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.After;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class TestParent {

	protected final static Logger logger = LoggerFactory.getLogger(TestParent.class);
	
	public static final double delta = 0.0000001d; 

	@Rule
	public TestRule watcher = new TestWatcher() {
		@Override
		protected void starting(Description description) {
			logger.debug("Starting test in class: " + description.getClassName() 
					+ ", method: " + description.getMethodName());		
		}
	};

	@After
	public void afterMethodIsOver() {
		logger.debug("Test ended");
	}
	
	public final void logDefaultInstanceCreation(Class<?> clazz) {
		logger.debug("Creating instance of " + clazz + " using default constructor");
	}

	public Logger getLogger() {
		return logger;
	}

	public TestRule getWatcher() {
		return watcher;
	}

	public static Object[][] listToArray(List<List<Object>> dataSet){
		Object[][] dataSetArray = new Object[dataSet.size()][];
		for (int i=0; i < dataSet.size(); i++) {
			List<Object> row = dataSet.get(i);
			for(int j=0; j<row.size();j++) {
				dataSetArray[i] = row.toArray(new Object[row.size()]);
			}
		}
		return dataSetArray;
	}
	
	public static int randomNumber(int minimum, int maximum) {
		return ThreadLocalRandom.current().nextInt(minimum, maximum+1);
	}
		
	public static void isExceptionExpected(Class<? extends Exception> expectedExceptionClass){
		if(expectedExceptionClass != null){
			fail("Following exception was expected here. " + expectedExceptionClass);	
		}		
	}
		
}
