package me.coppershark.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import me.coppershark.main.Main;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class Settings {
	private File settingsFile;
	private Main main;
	private File logFolder;
	private HashSet<String> logFileNames = new HashSet<String>();
	private HashMap<String, String> settings_cache = new HashMap<String, String>();

	public Settings(Main main) {
		this.main = main;
	}

	public void setFolder(FMLPreInitializationEvent event) {
		logFolder = new File(event.getModConfigurationDirectory(), Main.MODID);
		logFolder.mkdirs();
	}

	public void createSettingsFolderAndFile() {
		File settingsFolder = new File(logFolder, "settings");
		settingsFolder.mkdirs();
		settingsFile = new File(settingsFolder, "settings.txt");
		if (!settingsFile.exists())
			try {
				settingsFile.createNewFile();
			} catch (IOException ignored) {
			}
	}

	@SuppressWarnings("resource")
	public String getSetting(String setting) {
		String cached = settings_cache.get(setting);
		if (cached != null)
			return cached;
		try {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(new FileInputStream(settingsFile), Charset.forName("UTF-8")));
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.isEmpty() || !line.contains(":") || line.split(":")[0].length() <= 0)
					continue;
				if (line.split(":")[0].equalsIgnoreCase(setting))
					return line.split(":").length > 1 ? line.split(":")[1] : "";
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "default";
	}

	public void putSetting(String setting, String value) {
		settings_cache.put(setting, value);
		HashMap<String, String> settings = new HashMap<String, String>();
		BufferedReader reader;
		try {
			reader = new BufferedReader(
					new InputStreamReader(new FileInputStream(settingsFile), Charset.forName("UTF-8")));
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.isEmpty() || !line.contains(":") || line.split(":")[0].length() <= 0)
					continue;
				settings.put(line.split(":")[0], line.split(":").length > 1 ? line.split(":")[1] : "");
			}
			reader.close();
			settings.put(setting, value);
			BufferedWriter writer;
			writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(settingsFile, false), StandardCharsets.UTF_8));
			for (Entry<String, String> e : settings.entrySet())
				writer.append(e.getKey() + ":" + e.getValue() + "\n");
			writer.close();
		} catch (IOException ignored) {
		}
	}
}
