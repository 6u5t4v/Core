package com.Furnesse.core.customcommands;

import java.util.List;

public class CustomCommand {

	private String cmd;
	private String perm;
	private List<String> msg;
	private List<String> aliases;

	public CustomCommand(String cmd, String perm, List<String> msg, List<String> aliases) {
		this.cmd = cmd;
		this.perm = perm;
		this.aliases = aliases;
		this.msg = msg;
	}

	public List<String> getMsg() {
		return msg;
	}

	public void setMsg(List<String> msg) {
		this.msg = msg;
	}

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public String getPerm() {
		return perm;
	}

	public void setPerm(String perm) {
		this.perm = perm;
	}

	public List<String> getAliases() {
		return aliases;
	}

	public void setAliases(List<String> aliases) {
		this.aliases = aliases;
	}

}
