package com.Furnesse.core.utils;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;

public class Utils {
	static Map<Location, Material> placeholders = new HashMap<>();

	public static Map<Location, Material> getPlaceholders() {
		return placeholders;
	}
}
