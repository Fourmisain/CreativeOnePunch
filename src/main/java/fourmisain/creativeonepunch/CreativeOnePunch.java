package fourmisain.creativeonepunch;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.ActionResult;

public class CreativeOnePunch implements ModInitializer {
	@Override
	public void onInitialize() {
		AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
			if (player.isCreative()) {
				// get main hand item attack damage
				double attackDamage = 0;
				for (EntityAttributeModifier modifier : player.getMainHandStack().getAttributeModifiers(EquipmentSlot.MAINHAND).get(EntityAttributes.GENERIC_ATTACK_DAMAGE))
					attackDamage += modifier.getValue();

				if (attackDamage == 0) {
					entity.damage(DamageSource.player(player), 9999);
					return ActionResult.SUCCESS;
				}
			}

			return ActionResult.PASS;
		});
	}
}
