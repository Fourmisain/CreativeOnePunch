package io.github.fourmisain.creativeonepunch.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE;

// for 26.1
@Mixin(Player.class)
public abstract class PlayerMixin {
	@ModifyArg(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurtOrSimulate(Lnet/minecraft/world/damagesource/DamageSource;F)Z"), index = 1)
	public float onePunch(float originalDamage, @Local(argsOnly = true) Entity entity) {
		Player player = (Player) (Object) this;

		if (player.isCreative()) {
			// get main hand item attack damage (assuming multiplier is never 0)
			MutableDouble attackDamage = new MutableDouble();

			player.getMainHandItem().forEachModifier(EquipmentSlot.MAINHAND, (attribute, modifier) -> {
				if (modifier.operation() == ADD_VALUE) {
					attackDamage.addAndGet(modifier.amount());
				}
			});

			if (attackDamage.doubleValue() == 0.0) {
				if (entity.typeHolder().is(Identifier.withDefaultNamespace("sulfur_cube")))
					return 99f;

				// One Punch!
				return 9999f;
			}
		}

		return originalDamage;
	}
}
