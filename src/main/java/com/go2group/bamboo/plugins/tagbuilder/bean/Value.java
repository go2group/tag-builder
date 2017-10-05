package com.go2group.bamboo.plugins.tagbuilder.bean;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;

@Generated("org.jsonschema2pojo")
public class Value {

	@Expose
	private String id;
	@Expose
	private String displayId;
	@Expose
	private String latestChangeset;
	@Expose
	private String hash;

	/**
	 * 
	 * @return The id
	 */
	public String getId() {
		return id;
	}

	/**
	 * 
	 * @param id
	 *            The id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 
	 * @return The displayId
	 */
	public String getDisplayId() {
		return displayId;
	}

	/**
	 * 
	 * @param displayId
	 *            The displayId
	 */
	public void setDisplayId(String displayId) {
		this.displayId = displayId;
	}

	/**
	 * 
	 * @return The latestChangeset
	 */
	public String getLatestChangeset() {
		return latestChangeset;
	}

	/**
	 * 
	 * @param latestChangeset
	 *            The latestChangeset
	 */
	public void setLatestChangeset(String latestChangeset) {
		this.latestChangeset = latestChangeset;
	}

	/**
	 * 
	 * @return The hash
	 */
	public String getHash() {
		return hash;
	}

	/**
	 * 
	 * @param hash
	 *            The hash
	 */
	public void setHash(String hash) {
		this.hash = hash;
	}

}
