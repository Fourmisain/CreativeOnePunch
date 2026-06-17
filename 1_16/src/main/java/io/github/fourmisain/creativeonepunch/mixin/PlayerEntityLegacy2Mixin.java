package io.github.fourmisain.creativeonepunch.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.github.fourmisain.creativeonepunch.CreativeOnePunch.CONFIG;
import static net.minecraft.entity.attribute.EntityAttributeModifier.Operation.ADDITION;

// 1.16 - 1.20.4
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityLegacy2Mixin {
	@Inject(method = "attack", at = @At("HEAD"), cancellable = true)
	public void onePunchKill(CallbackInfo ci, @Local(argsOnly = true) Entity entity) {
		PlayerEntity player = (PlayerEntity) (Object) this;

		if (CONFIG.forceKill && player.isCreative() && creativeonepunch$isNotHoldingWeapon(player)) {
			entity.kill();
			ci.cancel();
		}
	}

	@ModifyArg(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"), index = 1)
	public float onePunch(float originalDamage) {
		PlayerEntity player = (PlayerEntity) (Object) this;

		if (player.isCreative() && creativeonepunch$isNotHoldingWeapon(player) && (!CONFIG.requireSneak || player.isSneaking())) {
			// One Punch!
			return CONFIG.damage;
		}

		return originalDamage;
	}

	@Unique
	private static boolean creativeonepunch$isNotHoldingWeapon(PlayerEntity player) {
		// get main hand item attack damage (assuming multiplier is never 0)
		double attackDamage = 0.0;
		for (EntityAttributeModifier modifier : player.getMainHandStack().getAttributeModifiers(EquipmentSlot.MAINHAND).get(EntityAttributes.GENERIC_ATTACK_DAMAGE)) {
			if (modifier.getOperation() == ADDITION) {
				attackDamage += modifier.getValue();
			}
		}

		return attackDamage == 0.0;
	}
}
