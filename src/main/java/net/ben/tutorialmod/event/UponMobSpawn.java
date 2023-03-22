package net.ben.tutorialmod.event;

import net.ben.tutorialmod.TutorialMod;
import net.ben.tutorialmod.TutorialModClient;
import net.ben.tutorialmod.entity.ModEntities;
import net.ben.tutorialmod.entity.custom.ZomboEntity;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

public class UponMobSpawn implements ServerEntityEvents.Load {

    @Override
    public void onLoad(Entity entity, ServerWorld world) {
        if (entity instanceof VillagerEntity){
            TutorialMod.zomboNEAT.villagerNum ++;
            if (TutorialMod.zomboNEAT.villagerNum > 15){
                entity.discard();
            }
        } else if (entity instanceof  ZomboEntity && TutorialMod.zomboNEAT.getCurrentGenome() == 0){
            String topScore = String.valueOf(TutorialMod.zomboNEAT.lastBestScore);
            String generationNumber = String.valueOf(TutorialMod.zomboNEAT.getGeneration());

            Text message = Text.literal("Top Score: "+topScore+" Generation: "+generationNumber);
            entity.world.getPlayers().forEach(player -> player.sendMessage(message));
        }
        if(entity instanceof ZomboEntity && ((ZomboEntity) entity).genomeNum == -1) {
            TutorialMod.zomboNEAT.assignNetwork((ZomboEntity) entity);
        }
        if(entity.getType() == EntityType.ZOMBIE && world.getGameRules().getBoolean(TutorialModClient.GENERATE_ZOMBOS) && !TutorialMod.zomboNEAT.populationComplete()) {
            ((ZombieEntity) entity).convertTo(ModEntities.ZOMBO, true);
        } else if (entity.getType() == EntityType.SKELETON)    {
            ((SkeletonEntity) entity).convertTo(EntityType.VILLAGER, false);
        } else if (entity instanceof MobEntity && entity.getType() != EntityType.VILLAGER && entity.getType() != ModEntities.ZOMBO){
            entity.discard();
        }
    }
}
