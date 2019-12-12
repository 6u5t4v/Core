package com.Furnesse.core.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.Furnesse.core.Core;

public class FileManager {
	public static HashMap<String, Config> configs = new HashMap<>();
	public static Core plugin;

	public FileManager(Core plugin) {
		this.plugin = plugin;
	}

	public Config getConfig(String name) {
		if (!configs.containsKey(name)) {
			configs.put(name, new Config(name));
		}
		return configs.get(name);
	}

	public Config saveConfig(String name) {
		return getConfig(name).save();
	}

	public Config reloadConfig(String name) {
		return getConfig(name).reload();
	}

	public class Config {
		private String name;

		private File file;
		private YamlConfiguration config;

		public Config(String name) {
			this.name = name;
		}

		public Config save() {
			if (this.config == null || this.file == null)
				return this;
			try {
				if (this.config.getConfigurationSection("").getKeys(true).size() != 0)
					this.config.save(this.file);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			return this;
		}

		public YamlConfiguration get() {
			if (this.config == null)
				reload();
			return this.config;
		}

		public Config saveDefaultConfig() {
			this.file = new File(FileManager.plugin.getDataFolder(), this.name);

			FileManager.plugin.saveResource(this.name, false);

			return this;
		}

		public Config reload() {
			if (this.file == null) {
				this.file = new File(FileManager.plugin.getDataFolder(), this.name);
			}
			this.config = YamlConfiguration.loadConfiguration(this.file);

			try {
				Reader defConfigStream = new InputStreamReader(FileManager.plugin.getResource(this.name), "UTF8");

				if (defConfigStream != null) {
					YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
					this.config.setDefaults((Configuration) defConfig);
				}
			} catch (UnsupportedEncodingException | NullPointerException unsupportedEncodingException) {
			}

			return this;
		}

		public Config copyDefaults(boolean force) {
			get().options().copyDefaults(force);
			return this;
		}

		public Config set(String key, Object value) {
			get().set(key, value);
			return this;
		}

		public Object get(String key) {
			return get().get(key);
		}
	}
}
