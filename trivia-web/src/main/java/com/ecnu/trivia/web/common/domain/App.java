package com.ecnu.trivia.web.common.domain;

import java.io.Serializable;

/**
 * APP
 */
public class App implements Serializable{

	
	/**  
	 */
	private static final long serialVersionUID = 796823297190678568L;
	private Integer id;
	private String appName;
	private String logo;
	

	public App(){
		super();
	}


	public String getAppName() {
		return appName;
	}


	public void setAppName(String appName) {
		this.appName = appName;
	}


	public String getLogo() {
		return logo;
	}


	public void setLogo(String logo) {
		this.logo = logo;
	}


	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}



}
