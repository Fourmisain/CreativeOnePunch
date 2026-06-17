package io.github.fourmisain.creativeonepunch;

import com.google.gson.JsonParseException;
import io.github.fourmisain.creativeonepunch.config.Config;
import io.github.fourmisain.creativeonepunch.config.GsonConfigHelper;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class CreativeOnePunch implements ModInitializer {
	public static final String MOD_ID = "creativeonepunch";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	public static final Config CONFIG = readOrCreateConfig();

	private static Config readOrCreateConfig() {
		GsonConfigHelper configHelper = new GsonConfigHelper(MOD_ID);

		if (configHelper.exists()) {
			try {
				return configHelper.load(Config.class);
			} catch (IOException | JsonParseException e) {
				LOGGER.error("Could not load config file, using defaults", e);
			}
		}

		Config config = new Config();

		try {
			configHelper.save(config);
		} catch (IOException e) {
			LOGGER.error("Could not save config file", e);
		}

		return config;
	}

	@Override
	public void onInitialize() {

	}
}
