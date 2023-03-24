package net.ben.tutorialmod.event;

import net.ben.tutorialmod.TutorialMod;
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


// triggers when an entity is initialised
public class UponMobSpawn implements ServerEntityEvents.Load {

    @Override
    public void onLoad(Entity entity, ServerWorld world) {
        /*
        if entity is a villager, the total number of
        villagers present variable is increased by one.
        if this causes the value to exceed the max number of
        villagers permitted, the villager is deleted
         */
        if (entity instanceof VillagerEntity){
            TutorialMod.zomboNEAT.setVillagerNum(TutorialMod.zomboNEAT.getVillagerNum() + 1);
            if (TutorialMod.zomboNEAT.getVillagerNum() > 15){
                entity.discard();
            }
            /*
            if the entity is the first Zombo
            of a new generation, the fitness score
            and generation number are displayed in
            Minecraft's in game chat
             */
        } else if (entity instanceof  ZomboEntity && TutorialMod.zomboNEAT.getCurrentGenome() == 0){
            String topScore = String.valueOf(TutorialMod.zomboNEAT.getLastBestScore());
            String generationNumber = String.valueOf(TutorialMod.zomboNEAT.getGeneration());

            Text message = Text.literal("Top Score: "+topScore+" Generation: "+generationNumber);
            entity.world.getPlayers().forEach(player -> player.sendMessage(message));
        }
        // assigns a genome to Zombos that don't have one
        if(entity instanceof ZomboEntity && ((ZomboEntity) entity).getGenomeNum() == -1) {
            TutorialMod.zomboNEAT.assignNetwork((ZomboEntity) entity);
        }

        if(entity.getType() == EntityType.ZOMBIE && !TutorialMod.zomboNEAT.populationComplete()) {
            ((ZombieEntity) entity).convertTo(ModEntities.ZOMBO, true);
        } else if (entity.getType() == EntityType.SKELETON)    {
            ((SkeletonEntity) entity).convertTo(EntityType.VILLAGER, false);
        } else if (entity instanceof MobEntity && entity.getType() != EntityType.VILLAGER && entity.getType() != ModEntities.ZOMBO){
            entity.discard();
        }
    }
}
