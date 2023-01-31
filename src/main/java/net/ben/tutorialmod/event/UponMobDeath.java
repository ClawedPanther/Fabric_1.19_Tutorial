package net.ben.tutorialmod.event;

import net.ben.tutorialmod.TutorialMod;
import net.ben.tutorialmod.TutorialModClient;
import net.ben.tutorialmod.entity.ModEntities;
import net.ben.tutorialmod.entity.ai.MobNEAT.ZomboNEAT;
import net.ben.tutorialmod.entity.custom.ZomboEntity;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.server.world.ServerWorld;

public class UponMobDeath implements ServerEntityEvents.Unload{

    @Override
    public void onUnload(Entity entity, ServerWorld world) {
        if(entity.getType() == ModEntities.ZOMBO) {
            TutorialMod.zomboNEAT.assignFitness((ZomboEntity) entity);
        }
    }
}
