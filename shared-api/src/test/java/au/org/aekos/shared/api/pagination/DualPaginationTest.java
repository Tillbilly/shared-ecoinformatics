package au.org.aekos.shared.api.pagination;

import org.junit.Assert;

import org.junit.Test;

public class DualPaginationTest {

	class Offset {
		//a -1 offset means no results required for that index.
		public int index1 = -1;
		public int index2 = -1;
		
		public Offset(int index1, int index2) {
			super();
			this.index1 = index1;
			this.index2 = index2;
		}
	}
	
	@Test
	public void testDualPagination(){
		//Shared and AEKOS have to combine search results.  Need to ensure pagination is correct 
		//when one set of results ends.
		
		int totalResultsPerPage = 20;
		int search1Results = 25;
		int search2Results = 100;
		int contribPerPage = totalResultsPerPage / 2;
		int numPages = (int) Math.ceil( ( search1Results + search2Results ) /  totalResultsPerPage ) ;
		System.out.println("contrib " + contribPerPage + " numPages " + numPages);
		
		//Need to calculate the offset for each search for a given page
		Offset easyCaseOffset = calculateOffsetForSearch(5, 48, 49, 20);
		Assert.assertNotNull(easyCaseOffset);
		Assert.assertEquals(40,easyCaseOffset.index1);
		Assert.assertEquals(40,easyCaseOffset.index2);
		
		Offset offset2 = calculateOffsetForSearch(5, 49, 79, 20);
		Assert.assertNotNull(offset2);
		Assert.assertEquals(40,offset2.index1);
		Assert.assertEquals(40,offset2.index2);
		
		Offset offset3 = calculateOffsetForSearch(6, 79, 49, 20);
		Assert.assertNotNull(offset3);
		Assert.assertEquals(-1, offset3.index2);
		Assert.assertEquals(51, offset3.index1);
		
		
	}
	
	@Test
	public void testBoundaryCases(){
		Offset offset4 = calculateOffsetForSearch(7, 79, 49, 20);
		Assert.assertNotNull(offset4);
		Assert.assertEquals(-1, offset4.index2);
		Assert.assertEquals(71, offset4.index1);
		//Does'nt matter if the offset goes over - just get 0 results!
		
		Offset offset5 = calculateOffsetForSearch(7, 49, 79, 20);
		Assert.assertNotNull(offset5);
		Assert.assertEquals(-1, offset5.index1);
		Assert.assertEquals(71, offset5.index2);
		
		Offset offset6 = calculateOffsetForSearch(2, 5, 50, 20);
		Assert.assertNotNull(offset6);
		Assert.assertEquals(-1, offset6.index1);
		Assert.assertEquals(15, offset6.index2);
		
		Offset offset7 = calculateOffsetForSearch(2, 0, 50, 20);
		Assert.assertNotNull(offset7);
		Assert.assertEquals(-1, offset7.index1);
		Assert.assertEquals(20, offset7.index2);
		
		Offset offset8 = calculateOffsetForSearch(2, 50, 0, 20);
		Assert.assertNotNull(offset8);
		Assert.assertEquals(20, offset8.index1);
		Assert.assertEquals(-1, offset8.index2);
	}
	
	//Assumes page numbering starts at 1
	public Offset calculateOffsetForSearch(int pageNumber, int numIndex1Results, int numIndex2Results, int numResultsPerPage){
		int contribPerPage = numResultsPerPage / 2;
		int fullContribPerIndex = pageNumber * contribPerPage;
		//The easy case
		if( numIndex1Results <= fullContribPerIndex &&  numIndex2Results <= fullContribPerIndex){
			int fullOffset = contribPerPage * ( pageNumber - 1 );
			return new Offset(fullOffset, fullOffset);
		}
		//Find the problem page, and determine which index's search results finish first.
		int index1LastPage = (int) Math.ceil((double)numIndex1Results/(double)contribPerPage);
		int index2LastPage = (int) Math.ceil((double)numIndex2Results/(double)contribPerPage);
		//The case where these are equal should already have been handled
		if(pageNumber <= index1LastPage && pageNumber <= index2LastPage ){
			int offset = contribPerPage * ( pageNumber - 1 );
			return new Offset(offset, offset);
		}
		if(pageNumber > index2LastPage){
			int offset= calculateOffset(numIndex2Results, contribPerPage, pageNumber, index2LastPage , numResultsPerPage );
			return new Offset(offset, -1);
		}else if(pageNumber > index1LastPage ){
			int offset= calculateOffset(numIndex1Results, contribPerPage, pageNumber, index1LastPage , numResultsPerPage );
			return new Offset(-1, offset);
		}
		return null;
	}
	
	//After the last page for the index with the least results, need to take into account the extra records
	//from the index with more results, plus the full amount of number of results per page - per page.		
	public int calculateOffset(int leastIndexNumResults, int contribPerPage, int pageNumber, int leastIndexLastPage, int numResultsPerPage){
		int numResultsOnLastPage = leastIndexNumResults % contribPerPage;
		int offsetShift = numResultsOnLastPage == 0 ? 0 : contribPerPage - numResultsOnLastPage;
		int offset = ( leastIndexLastPage * contribPerPage ) + offsetShift + ((pageNumber -1 - leastIndexLastPage) * numResultsPerPage) ;
		return offset;
	}
	
	
}
