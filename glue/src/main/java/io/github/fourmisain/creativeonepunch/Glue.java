package io.github.fourmisain.creativeonepunch;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.api.metadata.version.VersionPredicate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class Glue {
	public static final Logger LOGGER = LogManager.getLogger("glue");

	public static boolean test(String modId, String versionRange) {
		try {
			Optional<ModContainer> container = FabricLoader.getInstance().getModContainer(modId);
			if (!container.isPresent())
				return false;

			VersionPredicate pred = VersionPredicate.parse(versionRange);
			Version version = container.get().getMetadata().getVersion();

			return pred.test(version);
		} catch (VersionParsingException e) {
			LOGGER.error("version matching failed!", e);
			return false;
		}
	}

	public static boolean isLegacyMinecraft() {
		return test("minecraft", "<1.16");
	}

	public static boolean isLegacyMixin(String mixinClassName) {
		return mixinClassName.endsWith("LegacyMixin");
	}

	public static boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		boolean shouldApply = isLegacyMixin(mixinClassName) == isLegacyMinecraft();

		LOGGER.debug("{}applying {}", shouldApply ? "" : "NOT ", mixinClassName);

		return shouldApply;
	}
}
