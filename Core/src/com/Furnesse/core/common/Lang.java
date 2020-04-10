package com.Furnesse.core.common;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bukkit.ChatColor;

public class Lang {

	public static String color(String str) {
		return ChatColor.translateAlternateColorCodes('&', str);
	}
	
	/**
     * Returns a coloured version of a list.
     *
     * @param iterable list to be coloured.
     * @return coloured list.
     */
    public static List<String> color(Iterable<? extends String> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false)
                .filter(Objects::nonNull)
                .map(Lang::color)
                .collect(Collectors.toList());
    }

    public static List<String> color(String... args) {
        return color(Arrays.asList(args));
    }
}
