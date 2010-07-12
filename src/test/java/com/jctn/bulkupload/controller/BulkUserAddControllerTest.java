/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jctn.bulkupload.controller;

import com.jctn.bulkupload.model.json.UserAliasAddResponse;
import com.jctn.bulkupload.service.ws.UserAliasAdd;
import com.jctn.bulkupload.model.User;
import com.jctn.bulkupload.service.ws.AbstractJunctionWSTest;
import com.jctn.bulkupload.service.ws.HttpConnector;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.commons.lang.StringUtils;
import static org.mockito.Mockito.*;

/**
 *
 * @author martin
 */
public class BulkUserAddControllerTest extends AbstractJunctionWSTest {

	private BulkUserAddController controller;
	private User testUser;

	public BulkUserAddControllerTest(String testName) {
		super(testName);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		controller = new BulkUserAddController("mccomputerconsulting", "ons1pAcc3s5", "mccomputerconsulting.onsip.com", false);
		//controller.setAdminUsername("mccomputerconsulting");
		//controller.setAdminPassword("ons1pAcc3s5");
		//controller.setAdminDomain("mccomputerconsulting.onsip.com");
		testUser = mock(User.class);
		testUser.setEmail("testuser1@mccomputerconsulting.onsip.com");
		testUser.setFirstName("Test2");
		testUser.setLastName("User2");
		testUser.setExtension(7002);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		controller = null;
		testUser = null;
	}

	public void testInitialize() throws Exception {
		System.out.println("testInitialize");
		controller.initSession();
		assertNotNull(controller.getSessionId());
		assertTrue(StringUtils.isNotBlank(controller.getSessionId()));
	}

	/**
	 * Test of parseCsv method, of class BulkUserAddController.
	 */
	public void testParseCsv() {
//		System.out.println("parseCsv");
//		File csvFile = null;
//		BulkUserAddController instance = null;
//		Collection expResult = null;
//		Collection result = instance.parseCsv(csvFile);
//		assertEquals(expResult, result);
//		// TODO review the generated test code and remove the default call to fail.
//		fail("The test case is a prototype.");
	}

	/**
	 * Test of bulkUpload method, of class BulkUserAddController.
	 */
	public void testBulkUpload() {
		System.out.println("bulkUpload");
		Collection<User> users = new ArrayList<User>(1);
		users.add(testUser);

		//This is really an integration test.
		//controller.bulkUpload(users);
	}

	public void testExecUserAliasAddGood() throws Exception {
		System.out.println("execUserAliasAddGood");
		String goodUserAliasResponse = loadTestResource("/useraliasadd_good_response.json");
		HttpConnector connector = mock(HttpConnector.class);
		when(connector.sendRequest(anyString(), anyMap())).thenReturn(goodUserAliasResponse);

		UserAliasAdd realUserAliasAdd = new UserAliasAdd();
		UserAliasAddResponse response = realUserAliasAdd.mapJson(goodUserAliasResponse);

		UserAliasAdd mockUserAliasAdd = mock(UserAliasAdd.class);
		mockUserAliasAdd.setHttpConnector(connector);
		when(mockUserAliasAdd.sendRequest(anyMap())).thenReturn(response);

		controller.execUserAliasAdd(testUser, mockUserAliasAdd);
		verify(testUser, times(2)).setExtension(7002);


	}

	public void testExecUserAliasAddError() throws Exception {
		System.out.println("execUserAliasAddError");
		String badResponseString = loadTestResource("/useraliasadd_error_response.json");
		HttpConnector connector = mock(HttpConnector.class);
		when(connector.sendRequest(anyString(), anyMap())).thenReturn(badResponseString);

		UserAliasAdd realUserAliasAdd = new UserAliasAdd();
		UserAliasAddResponse response = realUserAliasAdd.mapJson(badResponseString);

		UserAliasAdd userAliasAdd = mock(UserAliasAdd.class);
		userAliasAdd.setHttpConnector(connector);
		when(userAliasAdd.sendRequest(anyMap())).thenReturn(response);

		controller.execUserAliasAdd(testUser, userAliasAdd);
		verify(testUser).setExtensionAdded(false);
		verify(testUser, atLeastOnce()).setError(anyString());
	}
}
