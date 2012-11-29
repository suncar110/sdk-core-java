package com.paypal.core;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.paypal.core.credential.CertificateCredential;
import com.paypal.core.credential.ICredential;
import com.paypal.core.credential.SignatureCredential;
import com.paypal.exception.InvalidCredentialException;
import com.paypal.exception.MissingCredentialException;

public class CredentialManagerTest {
	CredentialManager cred;
	ICredential credential;

	@BeforeClass
	public void beforeClass() {
		cred = CredentialManager.getInstance();
	}

	@Test(dataProvider = "configParams", dataProviderClass = DataProviderClass.class, priority = 0)
	public void getCredentialObjectTest(ConfigManager conf)
			throws InvalidCredentialException, MissingCredentialException {
		credential = cred.getCredentialObject(UnitTestConstants.API_USER_NAME);
		Assert.assertNotNull(credential);
	}

	@Test(expectedExceptions = MissingCredentialException.class, dataProvider = "configParams", dataProviderClass = DataProviderClass.class, priority = 1)
	public void getInvalidCredentialObjectTest(ConfigManager conf)
			throws Exception {
		cred.getCredentialObject("arumugam-biz_api1.paypal.com");
	}

	@Test(dataProvider = "configParams", dataProviderClass = DataProviderClass.class, priority = 2)
	public void checkDefaultCredentialObjectTest(ConfigManager conf)
			throws InvalidCredentialException, MissingCredentialException {
		credential = cred.getCredentialObject(null);
		Assert.assertNotNull(credential);
	}

	@Test(dataProvider = "configParams", dataProviderClass = DataProviderClass.class, expectedExceptions = InvocationTargetException.class, priority = 3)
	public void returnCredentialForInvalidCredentialExceptionTest(
			ConfigManager conf) throws NoSuchMethodException,
			SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		Map<String, String> credMap = new HashMap<String, String>();
		credMap.put("acct1.UserName", "jb_us_seller");
		credMap.put("acct1.Password", "password1");
		credMap.put("acct2.Password", "password2");
		Class<?> credClass = cred.getClass();
		Method method = credClass.getDeclaredMethod("returnCredential",
				Map.class, String.class);
		method.setAccessible(true);
		credential = (ICredential) method.invoke(cred, credMap, "acct1");

	}

	@Test(dataProvider = "configParams", dataProviderClass = DataProviderClass.class, priority = 4)
	public void returnCredentialForSignatureTest(ConfigManager conf)
			throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		Map<String, String> credMap = new HashMap<String, String>();
		credMap.put("acct1.UserName", "jb_us_seller");
		credMap.put("acct1.Password", "password1");
		credMap.put("acct1.Signature", "signature1");
		credMap.put("acct1.Subject", "subject1");
		credMap.put("acct2.Password", "password2");
		Class<?> credClass = cred.getClass();
		Method method = credClass.getDeclaredMethod("returnCredential",
				Map.class, String.class);
		method.setAccessible(true);
		credential = (ICredential) method.invoke(cred, credMap, "acct1");
		Assert.assertNotNull(credential);
		Assert.assertEquals(credential.getClass(), SignatureCredential.class);
	}

	@Test(dataProvider = "configParams", dataProviderClass = DataProviderClass.class, priority = 5)
	public void returnCredentialForCertificateTest(ConfigManager conf)
			throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		Map<String, String> credMap = new HashMap<String, String>();
		credMap.put("acct1.UserName", "jb_us_seller");
		credMap.put("acct1.Password", "password1");
		credMap.put("acct1.CertPath", "certPath1");
		credMap.put("acct1.CertKey", "certKey1");
		credMap.put("acct1.Subject", "subject1");
		credMap.put("acct2.Password", "password2");
		Class<?> credClass = cred.getClass();
		Method method = credClass.getDeclaredMethod("returnCredential",
				Map.class, String.class);
		method.setAccessible(true);
		credential = (ICredential) method.invoke(cred, credMap, "acct1");
		Assert.assertNotNull(credential);
		Assert.assertEquals(credential.getClass(), CertificateCredential.class);
	}

	@Test(expectedExceptions = MissingCredentialException.class, priority = 6)
	public void getCredentialObjectMissingCredentialFromEmptyConfigFile()
			throws IOException, MissingCredentialException,
			InvalidCredentialException {
		ConfigManager.getInstance().load(
				this.getClass().getResourceAsStream(
						"/sdk_config_empty.properties"));
		cred.getCredentialObject(null);

	}

	@Test(expectedExceptions = MissingCredentialException.class, priority = 6)
	public void getCredentialObjectMissingCredentialFromWithoutDefaultAccountValue()
			throws IOException, MissingCredentialException,
			InvalidCredentialException {
		ConfigManager.getInstance().load(
				this.getClass().getResourceAsStream(
						"/sdk_config_withoutdefaultaccount.properties"));
		cred.getCredentialObject(null);

	}

	@AfterClass
	public void afterClass() {
		cred = null;
	}

}
