package com.go2group.bamboo.plugins.tagbuilder.bean;

public class TBTags {

	private String name;
	private Long svnRevision;
	
	public TBTags() {}
	
	public TBTags(String name) {
		super();
		this.name = name;
	}
	
	public TBTags(String name, Long svnRevision) {
		super();
		this.name = name;
		this.svnRevision = svnRevision;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public Long getSvnRevision() {
		return svnRevision;
	}
	public void setSvnRevision(Long svnRevision) {
		this.svnRevision = svnRevision;
	}
}
