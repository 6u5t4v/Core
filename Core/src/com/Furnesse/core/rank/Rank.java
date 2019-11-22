package com.Furnesse.core.rank;

import java.util.List;

public class Rank {

	private String name;
	private String prefix;
	private String suffix;
	private boolean defaultRank;
	private List<String> permissions;
	private List<String> inherits;

	public Rank(
			String name, 
			String prefix, 
			String suffix, 
			boolean defaultRank, 
			List<String> permissions,
			List<String> inherits
			){
		
		this.name = name;
		this.prefix = prefix;
		this.suffix = suffix;
		this.defaultRank = defaultRank;
		this.permissions = permissions;
		this.inherits = inherits;
	}

	
	public List<String> getInherits() {
		return inherits;
	}

	public void setInherits(List<String> inherits) {
		this.inherits = inherits;
	}


	public boolean isDefaultRank() {
		return defaultRank;
	}

	public void setDefaultRank(boolean defaultRank) {
		this.defaultRank = defaultRank;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public List<String> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<String> permissions) {
		this.permissions = permissions;
	}
}
