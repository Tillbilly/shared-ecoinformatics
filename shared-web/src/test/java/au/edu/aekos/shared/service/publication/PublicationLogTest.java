package au.edu.aekos.shared.service.publication;

import static org.junit.Assert.fail;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.Test;

public class PublicationLogTest {

	@Test
	@SuppressWarnings("null")
	public void testPrintStackTraceToString(){
		try{
			TestClass tc = null;
			tc.getFred();
		}catch(NullPointerException e){
			//e.printStackTrace();
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			System.out.println( sw.toString() );
			return;
		}
		fail();
	}
	
	class TestClass {
		String fred;

		public String getFred() {
			return fred;
		}

		public void setFred(String fred) {
			this.fred = fred;
		}
	}
}
