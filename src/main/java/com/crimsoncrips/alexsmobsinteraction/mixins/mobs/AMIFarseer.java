package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.crimsoncrips.alexsmobsinteraction.effect.AMIEffects;
import com.crimsoncrips.alexsmobsinteraction.enchantment.AMIEnchantmentRegistry;
import com.crimsoncrips.alexsmobsinteraction.networking.AMIPacketHandler;
import com.crimsoncrips.alexsmobsinteraction.networking.FarseerPacket;
import com.github.alexthe666.alexsmobs.entity.EntityBison;
import com.github.alexthe666.alexsmobs.entity.EntityFarseer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.github.alexthe666.alexsmobs.client.event.ClientEvents.renderStaticScreenFor;


@Mixin(EntityFarseer.class)
public abstract class AMIFarseer extends Monster {



    private int alexsMobsInteraction$loop;

    protected AMIFarseer(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "tick", at = @At("TAIL"),remap = false)
    private void tickFarseer(CallbackInfo ci) {
        if (!AMInteractionConfig.FARSEER_ALTERING_ENABLED)
            return;
        if (this.level().isClientSide())
            return;
        if (!(alexsMobsInteraction$loop >= 0))
            return;

        if (this.getTarget() instanceof Player player) {
            if (player.getItemBySlot(EquipmentSlot.HEAD).getEnchantmentLevel(AMIEnchantmentRegistry.STABILIZER.get()) > 0)
                return;
            alexsMobsInteraction$loop--;
            Inventory inv = player.getInventory();

            for (int i = 0; i < 9 - 1; i++) {
                ItemStack current = inv.getItem(i);
                int j = this.getRandom().nextInt(i + 1, 9);
                ItemStack to = inv.getItem(j);
                inv.setItem(j, current);
                inv.setItem(i, to);
            }

            if (AMInteractionConfig.FARSEER_EFFECTS_ENABLED) {

                if (alexsMobsInteraction$loop == 49) {
                        AMIPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new FarseerPacket());

                    player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0));
                }
            }

        }
        if (this.getTarget() == null && alexsMobsInteraction$loop <= 0) {
            alexsMobsInteraction$loop = 50;
        }
    }

}