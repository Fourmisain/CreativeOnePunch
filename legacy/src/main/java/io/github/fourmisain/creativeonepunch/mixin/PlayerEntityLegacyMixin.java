package io.github.fourmisain.creativeonepunch.mixin;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import static net.minecraft.entity.attribute.EntityAttributeModifier.Operation.ADDITION;

// for pre-1.16
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityLegacyMixin {
	@ModifyArg(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"), index = 1)
	public float onePunch(float originalDamage) {
		PlayerEntity player = (PlayerEntity) (Object) this;

		if (player.isCreative()) {
			// get main hand item attack damage (assuming multiplier is never 0)
			double attackDamage = 0.0;
			for (EntityAttributeModifier modifier : player.getMainHandStack().getAttributeModifiers(EquipmentSlot.MAINHAND).get("generic.attackDamage")) {
				if (modifier.getOperation() == ADDITION) {
					attackDamage += modifier.getAmount();
				}
			}

			if (attackDamage == 0.0) {
				// One Punch!
				return 9999f;
			}
		}

		return originalDamage;
	}
}
