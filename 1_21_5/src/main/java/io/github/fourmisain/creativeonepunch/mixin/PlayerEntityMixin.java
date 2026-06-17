package io.github.fourmisain.creativeonepunch.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.github.fourmisain.creativeonepunch.CreativeOnePunch.CONFIG;
import static net.minecraft.entity.attribute.EntityAttributeModifier.Operation.ADD_VALUE;

// for 1.21.5+
// PlayerEntity.isCreative() changed from an abstract method to
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
	@Inject(method = "attack", at = @At("HEAD"), cancellable = true)
	public void onePunchKill(CallbackInfo ci, @Local(argsOnly = true) Entity entity) {
		PlayerEntity player = (PlayerEntity) (Object) this;

		if (CONFIG.forceKill && player.isCreative() && creativeonepunch$isNotHoldingWeapon(player) && entity.getEntityWorld() instanceof ServerWorld serverWorld) {
			entity.kill(serverWorld);
			ci.cancel();
		}
	}

	@ModifyArg(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;sidedDamage(Lnet/minecraft/entity/damage/DamageSource;F)Z"), index = 1)
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
		var attackDamage = new MutableDouble();

		player.getMainHandStack().applyAttributeModifiers(EquipmentSlot.MAINHAND, (attribute, modifier) -> {
			if (modifier.operation() == ADD_VALUE) {
				attackDamage.addAndGet(modifier.value());
			}
		});

		return attackDamage.doubleValue() == 0.0;
	}
}
