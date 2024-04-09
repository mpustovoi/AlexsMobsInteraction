package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.ReflectionUtil;
import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.crimsoncrips.alexsmobsinteraction.enchantment.AIEnchantmentRegistry;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIPanicBaby;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWanderRanged;
import com.github.alexthe666.alexsmobs.entity.ai.CreatureAITargetItems;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.ForgeEventFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;


@Mixin(EntityTiger.class)
public abstract class AITiger extends Mob {


    @Shadow private int animationTick;

    @Shadow public abstract Animation getAnimation();

    @Shadow @Final public static Animation ANIMATION_LEAP;

    protected AITiger(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }
    private static final EntityDataAccessor<Integer> STUNTICK = SynchedEntityData.defineId(EntityElephant.class, EntityDataSerializers.INT);

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void defineSynched(CallbackInfo ci){
        this.entityData.define(STUNTICK, 0);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void addAdditional(CompoundTag compound, CallbackInfo ci){
        compound.putInt("StunTicks", this.getStunTicks());
    }
    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readAdditional(CompoundTag compound, CallbackInfo ci){
        this.setStunTicks(compound.getInt("StunTicks"));

    }

    public int getStunTicks() {
        return (Integer)this.entityData.get(STUNTICK);
    }

    public void setStunTicks(int stuntick) {
        this.entityData.set(STUNTICK, stuntick);
    }

    boolean stun = false;

    protected boolean isImmobile() {
        return stun;
    }


    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo ci) {
        if (AInteractionConfig.stunnablecharge) {
            setStunTicks(getStunTicks() - 1);
            LivingEntity target = getTarget();
            if (getStunTicks() > 0 && target != null) {
                setTarget(null);
            }
            if (getStunTicks() < 1) {
                stun = false;

            }

            if (this.getTarget() instanceof Player player && this.getAnimation() == ANIMATION_LEAP && (player.getItemBySlot(EquipmentSlot.OFFHAND).getEnchantmentLevel(AIEnchantmentRegistry.FINAL_STAND.get()) > 0 || player.getItemBySlot(EquipmentSlot.MAINHAND).getEnchantmentLevel(AIEnchantmentRegistry.FINAL_STAND.get()) > 0)) {
                if (this.distanceTo(this.getTarget()) < 3F && this.hasLineOfSight(this.getTarget()) && this.getTarget().isBlocking() && !stun) {
                    setStunTicks(200);
                    stun = true;
                    this.playSound(SoundEvents.SHIELD_BLOCK, 2F, 1F);
                    target.removeEffect(new MobEffectInstance((MobEffect)AMEffectRegistry.FEAR.get(), Integer.MAX_VALUE, 0).getEffect());
                    target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 150, 1));
                    target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 300, 3));
                }
            }

        }
        float angle;


        if (this.getStunTicks() > 0) {
            if (this.level().isClientSide) {
                angle = 0.017453292F * this.yBodyRot;
                double headX = (double)(1.5F * this.getScale() * Mth.sin(3.1415927F + angle));
                double headZ = (double)(1.5F * this.getScale() * Mth.cos(angle));

                for(int i = 0; i < 5; ++i) {
                    float innerAngle = 0.017453292F * (this.yBodyRot + (float)(this.tickCount * 5)) * (float)(i + 1);
                    double extraX = (double)(0.5F * Mth.sin((float)(Math.PI + (double)innerAngle)));
                    double extraZ = (double)(0.5F * Mth.cos(innerAngle));
                    this.level().addParticle(ParticleTypes.CRIT, true, this.getX() + headX + extraX, this.getEyeY() + 0.5, this.getZ() + headZ + extraZ, 0.0, 0.0, 0.0);
                }
            }
        }
    }

}
