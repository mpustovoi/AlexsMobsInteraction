package com.crimsoncrips.alexsmobsinteraction.server.goal;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.github.alexthe666.alexsmobs.entity.EntityRattlesnake;
import com.github.alexthe666.alexsmobs.entity.EntityRoadrunner;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public class AMIWarnPredator extends Goal {
    int executionChance = 20;
    LivingEntity target = null;

    EntityRattlesnake rattlesnake;

    private static final Predicate<LivingEntity> WARNABLE_PREDICATE = (mob) -> {
        return mob instanceof Player && !((Player) mob).isCreative() && !mob.isSpectator() || mob instanceof EntityRoadrunner || (mob instanceof EntityRattlesnake && AlexsMobsInteraction.COMMON_CONFIG.RATTLESNAKE_TERRITORIAL_ENABLED.get()) ;
    };

    public AMIWarnPredator(EntityRattlesnake rattlesnake) {
        this.rattlesnake = rattlesnake;
    }

    public boolean canUse() {
        if (rattlesnake.getRandom().nextInt(this.executionChance) == 0) {
            List<LivingEntity> list = rattlesnake.level().getEntitiesOfClass(LivingEntity.class, rattlesnake.getBoundingBox().inflate(5.0, 5.0, 5.0), WARNABLE_PREDICATE);
            double d0 = Double.MAX_VALUE;
            LivingEntity possibleTarget = null;
            Iterator var7 = list.iterator();

            while(var7.hasNext()) {
                LivingEntity entity = (LivingEntity) var7.next();
                double d1 = rattlesnake.distanceToSqr(entity);
                if (!(d1 > d0) && entity != rattlesnake) {
                    d0 = d1;
                    possibleTarget = entity;
                }
            }

            this.target = possibleTarget;
            return !list.isEmpty();
        } else {
            return false;
        }
    }

    public boolean canContinueToUse() {
        return this.target != null && (double)rattlesnake.distanceTo(this.target) < 5.0 && rattlesnake.getTarget() == null;
    }

    public void stop() {
        this.target = null;
        rattlesnake.setRattling(false);
    }

    public void tick() {
        rattlesnake.setRattling(true);
        rattlesnake.setCurled(true);
        if (target != null) {
            rattlesnake.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
        }
    }
}

