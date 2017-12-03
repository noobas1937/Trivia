/** Created by Jack Chen at 2014/5/5 */
package com.ecnu.trivia.common.util.classutils;

/** @author Jack Chen */
public class SubClassA2NoEmptyConstructor extends SubClassA {
	private String password;

	public SubClassA2NoEmptyConstructor(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
