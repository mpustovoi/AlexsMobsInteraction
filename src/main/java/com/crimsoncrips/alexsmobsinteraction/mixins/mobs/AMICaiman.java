package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AMInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityAnaconda;
import com.github.alexthe666.alexsmobs.entity.EntityBoneSerpent;
import com.github.alexthe666.alexsmobs.entity.EntityCaiman;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;


@Mixin(EntityCaiman.class)
public abstract class AMICaiman extends TamableAnimal {

    protected AMICaiman(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }


    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void registerGoals(CallbackInfo ci) {
        EntityCaiman caiman = (EntityCaiman)(Object)this;

        if (AMInteractionConfig.CAIMAN_AGGRO_ENABLED) {
            caiman.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(caiman, Player.class, 150, true, true, null) {
                @Override
                public boolean canContinueToUse() {
                    return super.canContinueToUse() && !caiman.isTame();
                }
            });
        }
        if (AMInteractionConfig.CAIMAN_EGG_ATTACK_ENABLED) {
            caiman.targetSelector.addGoal(8, new EntityAINearestTarget3D<>(caiman, LivingEntity.class, 0, true, false, (livingEntity) -> {
                return livingEntity.isHolding(Ingredient.of(AMBlockRegistry.CAIMAN_EGG.get()));
            }) {
                @Override
                public boolean canContinueToUse() {
                    return super.canContinueToUse() && !caiman.isTame() && !caiman.isBaby();
                }
            });
        }
    }
}