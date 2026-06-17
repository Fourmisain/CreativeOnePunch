package io.github.fourmisain.creativeonepunch.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.github.fourmisain.creativeonepunch.CreativeOnePunch.CONFIG;
import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE;

// for 26.1
@Mixin(Player.class)
public abstract class PlayerMixin {
	@Inject(method = "attack", at = @At("HEAD"), cancellable = true)
	public void onePunchKill(CallbackInfo ci, @Local(argsOnly = true) Entity entity) {
		Player player = (Player) (Object) this;

		if (CONFIG.forceKill && player.isCreative() && creativeonepunch$isNotHoldingWeapon(player) && entity.level() instanceof ServerLevel serverLevel) {
			entity.kill(serverLevel);
			ci.cancel();
		}
	}

	@ModifyArg(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurtOrSimulate(Lnet/minecraft/world/damagesource/DamageSource;F)Z"), index = 1)
	public float onePunch(float originalDamage, @Local(argsOnly = true) Entity entity) {
		Player player = (Player) (Object) this;

		if (player.isCreative() && creativeonepunch$isNotHoldingWeapon(player) && (!CONFIG.requireSneak || player.isShiftKeyDown())) {
			float damage = CONFIG.damage;

			if (entity.typeHolder().is(Identifier.withDefaultNamespace("sulfur_cube")))
				damage = Math.min(damage, 99f);

			// One Punch!
			return damage;
		}

		return originalDamage;
	}

	@Unique
	private static boolean creativeonepunch$isNotHoldingWeapon(Player player) {
		// get main hand item attack damage (assuming multiplier is never 0)
		var attackDamage = new MutableDouble();

		player.getMainHandItem().forEachModifier(EquipmentSlot.MAINHAND, (attribute, modifier) -> {
			if (modifier.operation() == ADD_VALUE) {
				attackDamage.addAndGet(modifier.amount());
			}
		});

		return attackDamage.doubleValue() == 0.0;
	}
}
