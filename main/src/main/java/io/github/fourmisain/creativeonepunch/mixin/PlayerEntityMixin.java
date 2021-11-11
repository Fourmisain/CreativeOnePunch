package io.github.fourmisain.creativeonepunch.mixin;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import static net.minecraft.entity.attribute.EntityAttributeModifier.Operation.ADDITION;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
	@ModifyArgs(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"))
	public void onePunch(Args args) {
		PlayerEntity player = (PlayerEntity) (Object) this;

		if (player.isCreative()) {
			// get main hand item attack damage (assuming multiplier is never 0)
			double attackDamage = 0.0;
			for (EntityAttributeModifier modifier : player.getMainHandStack().getAttributeModifiers(EquipmentSlot.MAINHAND).get(EntityAttributes.GENERIC_ATTACK_DAMAGE)) {
				if (modifier.getOperation() == ADDITION) {
					attackDamage += modifier.getValue();
				}
			}

			if (attackDamage == 0.0) {
				// One Punch!
				args.set(1, 9999f);
			}
		}
	}
}
