import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

public class FactorsTester {

	@Test
	public void testPerfect1()
	{	
		// TEST 1: should throw the exception because the parameter value is less than 1
		assertThrows(IllegalArgumentException.class, () -> FactorsUtility.perfect(0));
	}
	
	@Test
	public void testPerfect2()
	{	
		// TEST 2: should succeed because 1 is a valid parameter value, but is not a perfect number
		assertFalse(FactorsUtility.perfect(1));
	}
	
	@Test
	public void testPerfect3()
	{	
		// TEST 3: should succeed because 6 is a valid parameter value, and is a perfect number
		assertTrue(FactorsUtility.perfect(6));
	}
	
	@Test
	public void testPerfect4()
	{	
		// TEST 4: should succeed because 7 is a valid parameter value, but is not a perfect number
		// I've coded this using assertEquals to show that there's often more than one way to write a test 
		boolean expected = false;
		assertEquals(expected, FactorsUtility.perfect(7));
	}
	
	@Test
	public void testGetFactors1()
	{
		ArrayList<Integer> expected = new ArrayList<>(Arrays.asList(1));
		assertEquals(expected, FactorsUtility.getFactors(2));
	}


	@Test
	public void testGetFactors2() 
	{
		assertEquals(0, FactorsUtility.getFactors(1).size());
	}

	@Test
	public void testGetFactors3() 
	{
		assertEquals(0, FactorsUtility.getFactors(0).size());
	}

	@Test
	public void testGetFactors4() 
	{
		assertThrows(IllegalArgumentException.class, () -> FactorsUtility.getFactors(-1));
	}

	@Test
	public void testGetFactors5() 
	{
		ArrayList<Integer> expected = new ArrayList<>(Arrays.asList(1, 2, 4, 5, 10, 20, 25, 50));
		assertEquals(expected, FactorsUtility.getFactors(100));
	}

	@Test
	public void testFactor1()
	{
		//TEST 1: should succeed because 1 is a factor of 2
		assertTrue(FactorsUtility.factor(2, 1));
	}
	
	@Test
	public void testFactor2()
	{
		//TEST 2: should succeed because 2 is NOT a factor of 3
		assertFalse(FactorsUtility.factor(3, 2));
	}
	
	@Test
	public void testFactor3()
	{
		//TEST 3: should throw the exception because 0 is not a valid parameter value ( b < 1 )
		assertThrows(IllegalArgumentException.class, () -> FactorsUtility.factor(2, 0));
	}
	
	@Test
	public void testFactor4()
	{
		//TEST 4: should throw the exception because -1 is not a valid parameter value (a < 0)
		assertThrows(IllegalArgumentException.class, () -> FactorsUtility.factor(-1, 1));
	}
	
	@Test
	public void testFactor5()
	{
		//TEST 5: should succeed because 7 is a factor of itself
		assertTrue(FactorsUtility.factor(7, 7));
	}
}
