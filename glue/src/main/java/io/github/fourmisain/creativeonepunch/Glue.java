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

	public static boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		boolean shouldApply;

		if (mixinClassName.endsWith("LegacyMixin")) {
			shouldApply = test("minecraft", "<1.16");
		} else if (mixinClassName.endsWith("Legacy2Mixin")) {
			shouldApply = test("minecraft", ">=1.16 <1.20.5");
		} else if (mixinClassName.endsWith("Legacy3Mixin")) {
			shouldApply = test("minecraft", ">=1.20.5 <1.21.2");
		} else if (mixinClassName.endsWith("Legacy4Mixin")) {
			shouldApply = test("minecraft", ">=1.21.2 <1.21.5");
		} else {
			shouldApply = test("minecraft", ">=1.21.5");
		}

		LOGGER.debug("{}applying {}", shouldApply ? "" : "NOT ", mixinClassName);

		return shouldApply;
	}
}
