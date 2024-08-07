package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.crimsoncrips.alexsmobsinteraction.item.AMIItemRegistry;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityCrimsonMosquito.class)
public abstract class AMICrimsonMosquito extends Mob {

    @Shadow private int sickTicks;

    @Shadow public abstract void setFlying(boolean flying);

    @Shadow public abstract int getBloodLevel();


    static {
        PACIFIED = SynchedEntityData.defineId(EntityCrimsonMosquito.class, EntityDataSerializers.BOOLEAN);
        MUNGUSFED = SynchedEntityData.defineId(EntityCrimsonMosquito.class, EntityDataSerializers.INT);
        WARPEDFED = SynchedEntityData.defineId(EntityCrimsonMosquito.class, EntityDataSerializers.INT);
    }

    private static final EntityDataAccessor<Boolean> PACIFIED;
    private static final EntityDataAccessor<Integer> MUNGUSFED;
    private static final EntityDataAccessor<Integer> WARPEDFED;

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void defineSynched(CallbackInfo ci){
        this.entityData.define(PACIFIED, false);
        this.entityData.define(MUNGUSFED, 0);
        this.entityData.define(WARPEDFED, 0);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void addAdditional(CompoundTag compound, CallbackInfo ci){
        compound.putBoolean("Pacified", this.isPacified());
        compound.putInt("MungusFed", this.getMungusFed());
        compound.putInt("WarpedFed", this.getWarpedFed());
    }
    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readAdditional(CompoundTag compound, CallbackInfo ci){
        this.setPacified(compound.getBoolean("Pacified"));
        this.setMungusFed(compound.getInt("MungusFed"));
        this.setWarpedFed(compound.getInt("WarpedFed"));
    }

    public boolean isPacified() {
        return this.entityData.get(PACIFIED);
    }

    public void setPacified(boolean pacified) {
        this.entityData.set(PACIFIED, pacified);
    }

    public int getWarpedFed() {
        return (Integer)this.entityData.get(WARPEDFED);
    }

    public void setWarpedFed(int warped) {
        this.entityData.set(WARPEDFED, warped);
    }

    public int getMungusFed() {
        return (Integer)this.entityData.get(MUNGUSFED);
    }

    public void setMungusFed(int mungus) {
        this.entityData.set(MUNGUSFED, mungus);
    }



    protected AMICrimsonMosquito(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void AlexInteraction$tick(CallbackInfo ci) {
        EntityCrimsonMosquito crimsonMosquito = (EntityCrimsonMosquito)(Object)this;
        if (this.isPacified()){
            this.setFlying(false);
            this.jumping = false;
            if (onGround()) this.setNoAi(true);
        }
        if (this.getMungusFed() >= 3 && this.getWarpedFed() >= 10) {
            crimsonMosquito.setSick(true);
            if (sickTicks > 159){
                for (int i = 0; i < 100; ++i) {
                    double d0 = this.random.nextGaussian() * 0.02D;
                    double d1 = this.random.nextGaussian() * 0.02D;
                    double d2 = this.random.nextGaussian() * 0.02D;
                    this.level().addParticle(ParticleTypes.CRIMSON_SPORE, this.getRandomX(1.6D), this.getY() + random.nextFloat() * 3.4F, this.getRandomZ(1.6D), d0, d1, d2);
                }
            }
        }

        Entity attach = this.getVehicle();

        if ((AMInteractionConfig.GOOFY_MODE_ENABLED && AMInteractionConfig.GOOFY_CRIMSON_MULTIPLY_ENABLED &&  attach != null && this.getBloodLevel() > 1)) {
            if (!(attach instanceof Player)){
                EntityCrimsonMosquito crimsonMosquito1 = AMEntityRegistry.CRIMSON_MOSQUITO.get().create(level());
                crimsonMosquito1.copyPosition(this);
                if (!this.level().isClientSide) {
                    crimsonMosquito1.finalizeSpawn((ServerLevelAccessor) level(), level().getCurrentDifficultyAt(this.blockPosition()), MobSpawnType.CONVERSION, null, null);
                }

                if (!this.level().isClientSide) {
                    this.level().broadcastEntityEvent(this, (byte) 79);
                    level().addFreshEntity(crimsonMosquito1);
                }
                attach.remove(RemovalReason.DISCARDED);
            }

        }

    }


    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        EntityCrimsonMosquito crimsonMosquito = (EntityCrimsonMosquito)(Object)this;
        ItemStack itemstack = player.getItemInHand(hand);
        InteractionResult type = super.mobInteract(player, hand);
        if (itemstack.getItem() == AMItemRegistry.WARPED_MIXTURE.get() && !crimsonMosquito.isSick()) {
            this.spawnAtLocation(itemstack.getCraftingRemainingItem());
            if (!player.isCreative()) {
                itemstack.shrink(1);
            }

            crimsonMosquito.setSick(true);
            return InteractionResult.SUCCESS;
        }
        if (itemstack.getItem() == AMItemRegistry.MUNGAL_SPORES.get() && AMInteractionConfig.CRIMSON_TRANSFORM_ENABLED && !(this.getMungusFed() >= 3) && isPacified()) {
            feed(itemstack);
            this.setMungusFed(this.getMungusFed() + 1);
            return InteractionResult.SUCCESS;
        }
        if (itemstack.getItem() == AMIItemRegistry.SWATTER.get() && AMInteractionConfig.CRIMSON_TRANSFORM_ENABLED && !isPacified()) {
            gameEvent(GameEvent.ENTITY_INTERACT);
            itemstack.hurtAndBreak(1, this, (p_233654_0_) -> {
            });
            this.setPacified(true);
            this.playSound(AMSoundRegistry.MOSQUITO_DIE.get(), 2F, 1F);
            this.setHealth(this.getHealth() / 2);
            return InteractionResult.SUCCESS;

        }
        if (itemstack.getItem() == Items.WARPED_FUNGUS && AMInteractionConfig.CRIMSON_TRANSFORM_ENABLED && !(this.getWarpedFed() >= 10) && isPacified()) {
            feed(itemstack);
           this.setWarpedFed(this.getWarpedFed() + 1);
            return InteractionResult.SUCCESS;
        }
        return type;

    }
    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        EntityCrimsonMosquito crimsonMosquito = (EntityCrimsonMosquito)(Object)this;
        if (id == 79) {
            for(int i = 0; i < 100; ++i) {
                double d0 = this.random.nextGaussian() * 0.02;
                double d1 = this.random.nextGaussian() * 0.02;
                double d2 = this.random.nextGaussian() * 0.02;
                this.level().addParticle(ParticleTypes.CRIMSON_SPORE, this.getRandomX(1.6), this.getY() + (double)(this.random.nextFloat() * 3.4F), this.getRandomZ(1.6), d0, d1, d2);
            }
            crimsonMosquito.playSound(SoundEvents.ZOMBIE_VILLAGER_CONVERTED);
        } else {
            super.handleEntityEvent(id);
        }

    }

    public void feed(ItemStack itemStack){
        gameEvent(GameEvent.ENTITY_INTERACT);
        itemStack.shrink(1);
        this.playSound(SoundEvents.GENERIC_EAT);
    }

}
