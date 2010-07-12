/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jctn.bulkupload.model;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;

/**
 * POJO representing a single user. This will likely be a row in some CSV input file.
 * @author martin
 */
public class User {

	private String firstName;
	private String lastName;
	private String email;
	private Integer extension;
	private boolean addVoicemail;
	private String password;
	private boolean userAdded;
	private Long userId;
	private String error;
	private boolean extensionAdded;

	public String getError() {
		return error;
	}

	public Long getUserId() {
		return userId;
	}

	public boolean isUserAdded() {
		return userAdded;
	}

	public void setUserAdded(boolean userAdded) {
		this.userAdded = userAdded;
	}

	public boolean isAddVoicemail() {
		return addVoicemail;
	}

	public void setAddVoicemail(boolean addVoicemail) {
		this.addVoicemail = addVoicemail;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getExtension() {
		return extension;
	}

	public void setExtension(Integer extension) {
		this.extension = extension;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Override
	public String toString() {
		return "User{" + "firstName=" + firstName + "lastName=" + lastName + "email=" + email + "extension=" + extension + "addVoicemail=" + addVoicemail + '}';
	}

	public String getUsername() {
		return StringUtils.lowerCase(firstName + "_" + lastName);
	}

	public String getAuthUsername() {
		return getUsername();
	}

	public String getPassword() {
		if (password == null) {
			password = RandomStringUtils.randomAlphanumeric(8);
		}
		return password;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public void setError(String errorString) {
		this.error = errorString;
	}

	public String getFullName() {
		return firstName + " " + lastName;
	}

	public void setExtensionAdded(boolean extAdded) {
		this.extensionAdded = extAdded;
	}
}
