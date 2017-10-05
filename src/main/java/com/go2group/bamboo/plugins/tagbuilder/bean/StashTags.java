package com.go2group.bamboo.plugins.tagbuilder.bean;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;

@Generated("org.jsonschema2pojo")
public class StashTags {

	@Expose
	private Integer size;
	@Expose
	private Integer limit;
	@Expose
	private Boolean isLastPage;
	@Expose
	private List<Value> values = new ArrayList<Value>();
	@Expose
	private Integer start;
	@Expose
	private Integer nextPageStart;

	/**
	 * 
	 * @return The size
	 */
	public Integer getSize() {
		return size;
	}

	/**
	 * 
	 * @param size
	 *            The size
	 */
	public void setSize(Integer size) {
		this.size = size;
	}

	/**
	 * 
	 * @return The limit
	 */
	public Integer getLimit() {
		return limit;
	}

	/**
	 * 
	 * @param limit
	 *            The limit
	 */
	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	/**
	 * 
	 * @return The isLastPage
	 */
	public Boolean getIsLastPage() {
		return isLastPage;
	}

	/**
	 * 
	 * @param isLastPage
	 *            The isLastPage
	 */
	public void setIsLastPage(Boolean isLastPage) {
		this.isLastPage = isLastPage;
	}

	/**
	 * 
	 * @return The values
	 */
	public List<Value> getValues() {
		return values;
	}

	/**
	 * 
	 * @param values
	 *            The values
	 */
	public void setValues(List<Value> values) {
		this.values = values;
	}

	/**
	 * 
	 * @return The start
	 */
	public Integer getStart() {
		return start;
	}

	/**
	 * 
	 * @param start
	 *            The start
	 */
	public void setStart(Integer start) {
		this.start = start;
	}

	/**
	 * 
	 * @return The nextPageStart
	 */
	public Integer getNextPageStart() {
		return nextPageStart;
	}

	/**
	 * 
	 * @param nextPageStart
	 *            The nextPageStart
	 */
	public void setNextPageStart(Integer nextPageStart) {
		this.nextPageStart = nextPageStart;
	}

}
