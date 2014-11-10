package ui;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author A0111770R
 *
 */
public class FlexiPlannerUITest {
	FlexiPlannerUI userInterface = new FlexiPlannerUI();
	/**
	*@author A0111770R
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		userInterface.loadUI();
	}

	/**
	*@author A0111770R
	 * @throws Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	
	/**
	*@author A0111770R
	 */
	@Test 
	public void testSchedulerFrame(){
		assertTrue(userInterface.getJFrame().isVisible());
	}
	
	/**
	*@author A0111770R
	 */
	@Test
	public void testTextField() {
		assertEquals("",userInterface.getTextField());
		userInterface.setTextField("testing");
		assertEquals("testing",userInterface.getTextField());
		userInterface.setTextField("testing 12345");
		assertEquals("testing 12345",userInterface.getTextField());	
	}
	
	/**
	*@author A0111770R
	 */
	@Test
	public void testShowUserGuideCollapsePane(){
		assertFalse(userInterface.getUserGuideCollapseOrNot());
	}
	
	/**
	*@author A0111770R
	 */
	@Test
	public void testBlockedCollapsePane(){
		assertTrue(userInterface.getBlockedCollapseOrNot());
	}
	
	/**
	*@author A0111770R
	 */
	@Test
	public void testShowCategoryLabel(){
		assertEquals("Categories",userInterface.getCategoryLabel());
	}
	
	/**
	*@author A0111770R
	 */
	@Test
	public void testCommandFeedback(){
		assertEquals("",userInterface.getCommandFeedback());
	}
	
	/**
	*@author A0111770R
	 */
	@Test
	public void testSelectYear(){
		assertEquals("2014",userInterface.getSelectYear().toString());
	}
	
}
