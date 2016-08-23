package au.edu.aekos.shared.web.json.dynatree;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.google.gson.Gson;

public class DynatreeJsonStringTest {

	@Test
	public void testDynatreeJsonString(){
		Gson gson = new Gson();
		DynatreeNode dn = new DynatreeNode("One option");
		dn.addChild(new DynatreeNode("child1"));
		dn.addChild(new DynatreeNode("child2"));
		
		DynatreeNode dn2 = new DynatreeNode("2 option");
		dn2.addChild(new DynatreeNode("child1"));
		dn2.addChild(new DynatreeNode("child2"));
		
		List<DynatreeNode> dnList = new ArrayList<DynatreeNode>();
		dnList.add(dn);
		dnList.add(dn2);
		
		String str = gson.toJson(dnList);
		Assert.assertNotNull(str);
		System.out.println(str);
		
		
	}
	
	
}
