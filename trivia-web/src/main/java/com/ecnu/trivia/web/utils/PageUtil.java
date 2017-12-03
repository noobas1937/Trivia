package com.ecnu.trivia.web.utils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class PageUtil implements Serializable{

	/**
	 * 分页查询请求工具VO
	 */
	private static final long serialVersionUID = -2138605739048508091L;
	private Integer currentPage;		//当前页
	private Integer pageSize;			//每页显示条数
	private Integer pageCount;			//总页数
	private Integer count;				//总条数
	private Object list;			//数据
	private Map<String,Object> param = new HashMap<String,Object>();	//参数条件
	private String sort;
	private String sortby;
	
	public PageUtil(){
		super();
	}
	
	public Integer getCurrentPage() {
		return currentPage == null ? 1 : currentPage;
	}
	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}
	public Integer getPageSize() {
		return this.pageSize == null ? 10 : this.pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	public Integer getPageCount() {
		if(this.count == null){
			return 1;
		}
		if(this.count%this.pageSize == 0){
			return this.count/this.pageSize;
		}
		return this.count/this.pageSize + 1;
	}
	public void setPageCount(Integer pageCount) {
		this.pageCount =  pageCount;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}

	public Map<String,Object> getParam() {
		return param;
	}

	public void setParam(Map<String,Object> param) {
		this.param = param;
	}

	public Integer getStartIndex() {
		if(this.currentPage == null){
			this.currentPage = getCurrentPage();
		}
		if(this.pageSize == null){
			this.pageSize = getPageSize();
		}
		return (this.currentPage-1)*this.pageSize;
	}

	public Object getList() {
		return list;
	}

	public void setList(Object list) {
		this.list = list;
	}
	
	public void addParam(String key,Object value){
		if(key == null || value == null){
			return;
		}
		this.param.put(key, value);
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getSortby() {
		return sortby;
	}

	public void setSortby(String sortby) {
		this.sortby = sortby;
	}

	@Override
	public String toString() {
		return "PageUtil{" +
					 "currentPage=" + currentPage +
					 ", pageSize=" + pageSize +
					 ", pageCount=" + pageCount +
					 ", count=" + count +
					 '}';
	}
	
	
}
