package com.Furnesse.core.config;

import com.Furnesse.core.Core;
import com.Furnesse.core.utils.Lang;

public enum Message
{
	PREFIX("prefix"), NO_PERMISSION("no_permission"), RELOADED("reloaded"), INVALID_PLAYER("invalid_player"),
	FULL_INVENTORY("full_inventory"),

	RANK_CREATED("rank.rank_created"), RANK_DELETED("rank.rank_deleted"), RANK_SETPREFIX("rank.rank_setprefix"),
	RANK_SETSUFFIX("rank.rank_setsuffix"), RANK_PERM_ADDED("rank.rank_perm_added"),
	RANK_PERM_REMOVED("rank.rank_perm_removed"), USER_SET_RANK("rank.user_set_rank"),
	USER_ADDED_PERM("rank.user_added_perm"), USER_REMOVED_PERM("rank.user_removed_perm"),
	USER_RANK_RECEIVED("rank.user_rank_received"), USER_PERM_RECEIVED("rank.user_perm_received"),
	USER_PERM_REMOVED("rank.user_perm_removed"),

	DEATHCOORDS("deathchest.deathcoords"), DEATHCHEST_EXPIRE_TIME("deathchest.deathchest_expire_time"),
	DEATHCHEST_DISSAPEARED("deathchest.deathchest_disappeared"),
	DEATHCHEST_CANNOT_BREAK("deathchest.deathchest_cannot_break"),
	DEATHCHEST_CANNOT_OPEN("deathchest.deathchest_cannot_open"),
	DEATHCHEST_FASTLOOT_COMPLETE("deathchest.deathchest_fastloot_complete"),

	ITEMS_INVALID_ITEM("citems.items_invalid_item"), ITEMS_PLAYER_RECEIVED("citems.items_player_received"),
	ITEMS_SUCCESFULL_RECEIVED("citems.items_succesfull_received");

	private String path;
	private String message;

	Message(String path) {
		this.path = path;
		this.message = Lang.chat(Core.instance.getFileManager().getConfig("lang.yml").get().getString(path));
	}

	public String getChatMessage() {
		return PREFIX.getMessage() + this.message;
	}

	public String getPath() {
		return path;
	}

	public String getMessage() {
		return message;
	}

	private void setMessage(String message) {
		this.message = message;
	}

	public static void reload() {
		for (Message m : values()) {
			m.setMessage(Lang.chat(Core.instance.getFileManager().getConfig("lang.yml").get().getString(m.getPath())));
		}
	}
}
