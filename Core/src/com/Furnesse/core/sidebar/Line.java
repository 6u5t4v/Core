package com.Furnesse.core.sidebar;

import java.util.List;

import com.Furnesse.core.Core;

public abstract class Line {

	List<String> lines = Core.instance.getSettings().sb_lines;
	
	public String getCurrentLine() {
		return null;
	}
	
	
}
