package com.Furnesse.core.chat;

import java.util.List;

public class ChatFormat {

	private String name;
	private String format;
	private String clickCmd;
	private List<String> tooltip;

	public ChatFormat(String name, String format, String clickCmd, List<String> tooltip) {
		this.name = name;
		this.format = format;
		this.clickCmd = clickCmd;
		this.tooltip = tooltip;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getClickCmd() {
		return clickCmd;
	}

	public void setClickCmd(String clickCmd) {
		this.clickCmd = clickCmd;
	}

	public List<String> getTooltip() {
		return tooltip;
	}

	public void setTooltip(List<String> tooltip) {
		this.tooltip = tooltip;
	}

}
