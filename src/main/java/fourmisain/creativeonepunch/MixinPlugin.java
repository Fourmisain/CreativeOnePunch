package fourmisain.creativeonepunch;

import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.api.metadata.version.VersionPredicate;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.fabricmc.loader.impl.util.version.VersionPredicateParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class MixinPlugin implements IMixinConfigPlugin {
	@Override
	public void onLoad(String mixinPackage) {

	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		Logger logger = LogManager.getLogger("creativeonepunch");

		try {
			logger.debug("shouldApplyMixin({})", mixinClassName);
			logger.debug("normalized game version: {}", FabricLoaderImpl.INSTANCE.getGameProvider().getNormalizedGameVersion());

			VersionPredicate legacyPredicate = VersionPredicateParser.parse("<1.16");
			SemanticVersion version = SemanticVersion.parse(FabricLoaderImpl.INSTANCE.getGameProvider().getNormalizedGameVersion());

			boolean isLegacyVersion = legacyPredicate.test(version);
			boolean isLegacyMixin = mixinClassName.endsWith("LegacyMixin");
			boolean shouldApply = isLegacyMixin == isLegacyVersion;

			logger.debug("isLegacyVersion {}", isLegacyVersion);
			logger.debug("isLegacyMixin {}", isLegacyMixin);

			if (shouldApply)
				logger.debug("applying {}" + mixinClassName);

			return shouldApply;
		} catch (VersionParsingException e) {
			logger.error("version matching failed, disabled mixins!", e);
			return false;
		}
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

	}

	@Override
	public List<String> getMixins() {
		return null;
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

	}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

	}
}
