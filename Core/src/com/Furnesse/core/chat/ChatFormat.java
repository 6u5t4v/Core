package com.Furnesse.core.chat;

public class ChatFormat {

	private String formatName;
	private String prefix;
	private String suffix;
	private String displayname;
	private String message;
	private String clickCmd;

	public ChatFormat(String formatName, String prefix, String suffix, String displayname, String message,
			String clickCmd) {
		this.formatName = formatName;
		this.prefix = prefix;
		this.suffix = suffix;
		this.displayname = displayname;
		this.message = message;
		this.clickCmd = clickCmd;
	}

	public String getFormatName() {
		return formatName;
	}

	public void setFormatName(String formatName) {
		this.formatName = formatName;
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

	public String getDisplayname() {
		return displayname;
	}

	public void setDisplayname(String displayname) {
		this.displayname = displayname;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getClickCmd() {
		return clickCmd;
	}

	public void setClickCmd(String clickCmd) {
		this.clickCmd = clickCmd;
	}

}
