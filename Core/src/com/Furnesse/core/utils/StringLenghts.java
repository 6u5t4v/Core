package com.Furnesse.core.utils;

import org.bukkit.ChatColor;

public class StringLenghts {
	
	private static boolean ver13;
	
	public static int getMaxSize() {
	    if (ver13)
	      return 64; 
	    return 16;
	  }
	
	private static String getResult(boolean BOLD, boolean ITALIC, boolean MAGIC, boolean STRIKETHROUGH, boolean UNDERLINE,
			ChatColor color) {
		return ((color != null && !color.equals(ChatColor.WHITE)) ? color : "") + "" + (BOLD ? ChatColor.BOLD : "")
				+ (ITALIC ? ChatColor.ITALIC : "") + (MAGIC ? ChatColor.MAGIC : "")
				+ (STRIKETHROUGH ? ChatColor.STRIKETHROUGH : "") + (UNDERLINE ? ChatColor.UNDERLINE : "");
	}

	public static String[] splitString(String string) {
		StringBuilder prefix = new StringBuilder(
				string.substring(0, (string.length() >= getMaxSize()) ? getMaxSize() : string.length()));
		StringBuilder suffix = new StringBuilder(
				(string.length() > getMaxSize()) ? string.substring(getMaxSize()) : "");
		if (prefix.toString().length() > 1 && prefix.charAt(prefix.length() - 1) == '�') {
			prefix.deleteCharAt(prefix.length() - 1);
			suffix.insert(0, '�');
		}
		int length = prefix.length();

		boolean PASSED = false, UNDERLINE = PASSED, STRIKETHROUGH = UNDERLINE, MAGIC = STRIKETHROUGH, ITALIC = MAGIC,
				BOLD = ITALIC;
		ChatColor textColor = null;
		for (int index = length - 1; index > -1; index--) {
			char section = prefix.charAt(index);
			if (section == '�' && index < prefix.length() - 1) {
				char c = prefix.charAt(index + 1);
				ChatColor color = ChatColor.getByChar(c);
				if (color != null) {
					if (color.equals(ChatColor.RESET)) {
						break;
					}
					if (textColor == null && color.isFormat()) {
						if (color.equals(ChatColor.BOLD) && !BOLD) {
							BOLD = true;
						} else if (color.equals(ChatColor.ITALIC) && !ITALIC) {
							ITALIC = true;
						} else if (color.equals(ChatColor.MAGIC) && !MAGIC) {
							MAGIC = true;
						} else if (color.equals(ChatColor.STRIKETHROUGH) && !STRIKETHROUGH) {
							STRIKETHROUGH = true;
						} else if (color.equals(ChatColor.UNDERLINE) && !UNDERLINE) {
							UNDERLINE = true;
						}
					} else if (textColor == null && color.isColor()) {
						textColor = color;
					}
				}
			} else if (index > 0 && !PASSED) {
				char c = prefix.charAt(index);
				char c1 = prefix.charAt(index - 1);
				if (c != '�' && c1 != '�' && c != ' ') {
					PASSED = true;
				}
			}
			if (!PASSED && prefix.charAt(index) != ' ') {
				prefix.deleteCharAt(index);
			}
			if (textColor != null) {
				break;
			}
		}
		String result = suffix.toString().isEmpty() ? ""
				: getResult(BOLD, ITALIC, MAGIC, STRIKETHROUGH, UNDERLINE, textColor);
		if (!suffix.toString().isEmpty() && !suffix.toString().startsWith("�")) {
			suffix.insert(0, result);
		}
		return new String[] {
				(prefix.toString().length() > getMaxSize()) ? prefix.toString().substring(0, getMaxSize())
						: prefix.toString(),
				(suffix.toString().length() > getMaxSize()) ? suffix.toString().substring(0, getMaxSize())
						: suffix.toString() };
	}
}
