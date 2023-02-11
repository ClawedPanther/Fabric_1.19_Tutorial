package net.ben.tutorialmod.event;

import net.ben.tutorialmod.TutorialMod;
import net.ben.tutorialmod.TutorialModClient;
import net.ben.tutorialmod.entity.ModEntities;
import net.ben.tutorialmod.entity.custom.ZomboEntity;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

public class MyEvent implements ServerEntityEvents.Load {
    @Override
    public void onLoad(Entity entity, ServerWorld world) {

        if(entity instanceof ZomboEntity && ((ZomboEntity) entity).genomeNum == -1) {

            TutorialMod.zomboNEAT.assignNetwork((ZomboEntity) entity);
        }
        if(entity.getType() == EntityType.ZOMBIE && world.getGameRules().getBoolean(TutorialModClient.GENERATE_ZOMBOS) && !TutorialMod.zomboNEAT.populationComplete()) {
            ((ZombieEntity) entity).convertTo(ModEntities.ZOMBO, true);
        } else if (entity instanceof MobEntity && entity.getType() != EntityType.SKELETON && entity.getType() != ModEntities.ZOMBO){
            entity.discard();
        }
    }
}
