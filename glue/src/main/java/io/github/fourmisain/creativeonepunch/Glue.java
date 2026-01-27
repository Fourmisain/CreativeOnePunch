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

	public static boolean testMinecraft(String versionRange) {
		return test("minecraft", versionRange);
	}
	
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
			shouldApply = testMinecraft("<1.16");
		} else if (mixinClassName.endsWith("Legacy2Mixin")) {
			shouldApply = testMinecraft(">=1.16 <1.20.5");
		} else if (mixinClassName.endsWith("Legacy3Mixin")) {
			shouldApply = testMinecraft(">=1.20.5 <1.21.2");
		} else if (mixinClassName.endsWith("Legacy4Mixin")) {
			shouldApply = testMinecraft(">=1.21.2 <1.21.5");
		} else if (mixinClassName.endsWith("PlayerEntityMixin")) {
			shouldApply = testMinecraft(">=1.21.5 <=1.21.11");
		} else {
			shouldApply = testMinecraft(">=26.1-alpha.4");
		}

		LOGGER.debug("{}applying {}", shouldApply ? "" : "NOT ", mixinClassName);

		return shouldApply;
	}
}
