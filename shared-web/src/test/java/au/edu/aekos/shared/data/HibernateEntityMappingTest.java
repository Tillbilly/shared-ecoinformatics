package au.edu.aekos.shared.data;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:test-data-context-hssql.xml"})
public class HibernateEntityMappingTest {

	/**
	 * Not a mistake, this triggers the context to start and will fail if the context isn't correctly configured.
	 */
	@Test
	public void testContextConfig(){
		assertTrue(true);
	}
	
}
