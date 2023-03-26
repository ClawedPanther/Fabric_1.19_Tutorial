package net.ben.neatmobsmod.event;

import net.ben.neatmobsmod.NEATMobsMod;
import net.ben.neatmobsmod.entity.ModEntities;
import net.ben.neatmobsmod.entity.custom.ZomboEntity;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameRules;

// triggers when an entity is unloaded
public class UponMobDeath implements ServerEntityEvents.Unload{

    @Override
    public void onUnload(Entity entity, ServerWorld world) {
        /*
        if entity is a Zombo, the Zombo's assigned
        genome is assigned a fitness score
         */
        boolean mobSpawningOn = entity.world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING);
        if(entity.getType() == ModEntities.ZOMBO) {
            NEATMobsMod.zomboNEAT.assignFitness((ZomboEntity) entity);
        /*
        only runs if mobs are spawning naturally
        if entity is a villager, total number of villagers is
        decremented
        */
        } else if (entity instanceof VillagerEntity && mobSpawningOn){
            NEATMobsMod.zomboNEAT.setVillagerNum(NEATMobsMod.zomboNEAT.getVillagerNum()-1);
        }
    }
}
