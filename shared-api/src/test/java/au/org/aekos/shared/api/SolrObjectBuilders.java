package au.org.aekos.shared.api;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.GroupResponse;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;

/**
 * Building Solr objects for test data doesn't so much suck as it is messy so this provides a mini-DSL
 * to make it look better. Of course you could run an embedded Solr and base tests off that but setting
 * that up also sucks.
 */
public class SolrObjectBuilders {
	public static void createSolrTestData(SolrServer server, SolrInputDocument...documents) throws Exception {
		for (SolrInputDocument currDoc : documents) {
			server.add(currDoc);
		}
		server.commit(true, true, true);
	}

	public static void createMultipleSolrDocumentsWithTheField(SolrServer solrServer, int copies, SolrFieldPlaceholder field) throws Exception {
		for (int i = 0; i<copies; i++) {
			SolrInputDocument currDoc = new SolrInputDocument();
			currDoc.setField("id", String.valueOf(i));
			currDoc.setField(field.fieldName, field.value);
			solrServer.add(currDoc);
		}
		solrServer.commit(true, true, true);
	}
	
	public static SolrInputDocument aDocument(SolrFieldPlaceholder...fields) {
		SolrInputDocument result = new SolrInputDocument();
		for (SolrFieldPlaceholder currField : fields) {
			result.addField(currField.fieldName, currField.value);
		}
		return result;
	}
	
	public static SolrDocumentList aSolrDocumentList(long start, long numFound, SolrDocument...documents) {
		SolrDocumentList result = new SolrDocumentList();
		result.setStart(start);
		result.setNumFound(numFound);
		for (SolrDocument currDoc : documents) {
			result.add(currDoc);
		}
		return result;
	}

	public static SolrFieldPlaceholder aField(String fieldName, Object value) {
		return new SolrFieldPlaceholder(fieldName, value);
	}

	public static class SolrFieldPlaceholder {
		private final String fieldName;
		private final Object value;
		
		public SolrFieldPlaceholder(String fieldName, Object value) {
			this.fieldName = fieldName;
			this.value = value;
		}
	}
	
	public static QueryResponse aQueryResponse(SolrDocumentList documentList) {
		QueryResponse result = mock(QueryResponse.class);
		when(result.getResults()).thenReturn(documentList);
		return result;
	}
	
	public static QueryResponse aQueryResponse(GroupResponse groupResponse) {
		QueryResponse result = mock(QueryResponse.class);
		when(result.getGroupResponse()).thenReturn(groupResponse);
		return result;
	}
	
	public static GroupResponse aGroupResponse(GroupCommand groupCommand) {
		GroupResponse result = new GroupResponse();
		result.add(groupCommand);
		return result;
	}
	
	public static Group aGroup(String name, long numFound) {
		SolrDocumentList docList = new SolrDocumentList();
		docList.setNumFound(numFound);
		return new Group(name, docList);
	}
	
	public static GroupCommand aGroupCommand(String name, Group...groups) {
		GroupCommand result = new GroupCommand(name, 0);
		for (Group currGroup : groups) {
			result.add(currGroup);
		}
		return result;
	}
}
