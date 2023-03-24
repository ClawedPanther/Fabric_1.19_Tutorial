package net.ben.tutorialmod.event;

import net.ben.tutorialmod.TutorialMod;
import net.ben.tutorialmod.entity.ModEntities;
import net.ben.tutorialmod.entity.custom.ZomboEntity;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;

// triggers when an entity is unloaded
public class UponMobDeath implements ServerEntityEvents.Unload{

    @Override
    public void onUnload(Entity entity, ServerWorld world) {
        /*
        if entity is a Zombo, the Zombo's assigned
        genome is assigned a fitness score
         */
        if(entity.getType() == ModEntities.ZOMBO) {
            TutorialMod.zomboNEAT.assignFitness((ZomboEntity) entity);
        /*
        if entity is a villager, total number of villagers is
        decremented
        */
        } else if (entity instanceof VillagerEntity){
            TutorialMod.zomboNEAT.setVillagerNum(TutorialMod.zomboNEAT.getVillagerNum()-1);
        }
    }
}
