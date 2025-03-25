package io.github.fourmisain.creativeonepunch.mixin;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import static net.minecraft.entity.attribute.EntityAttributeModifier.Operation.ADD_VALUE;

// for 1.21.2+
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
	@ModifyArg(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;sidedDamage(Lnet/minecraft/entity/damage/DamageSource;F)Z"), index = 1)
	public float onePunch(float originalDamage) {
		PlayerEntity player = (PlayerEntity) (Object) this;

		if (player.isCreative()) {
			// get main hand item attack damage (assuming multiplier is never 0)
			MutableDouble attackDamage = new MutableDouble();

			player.getMainHandStack().applyAttributeModifiers(EquipmentSlot.MAINHAND, (attribute, modifier) -> {
				if (modifier.operation() == ADD_VALUE) {
					attackDamage.addAndGet(modifier.value());
				}
			});

			if (attackDamage.doubleValue() == 0.0) {
				// One Punch!
				return 9999f;
			}
		}

		return originalDamage;
	}
}
