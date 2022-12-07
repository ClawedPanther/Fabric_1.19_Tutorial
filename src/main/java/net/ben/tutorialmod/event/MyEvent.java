package net.ben.tutorialmod.event;

import net.ben.tutorialmod.TutorialMod;
import net.ben.tutorialmod.entity.ModEntities;
import net.ben.tutorialmod.entity.custom.ZomboEntity;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

public class MyEvent implements ServerEntityEvents.Load {
    @Override
    public void onLoad(Entity entity, ServerWorld world) {
        if(entity instanceof ZombieEntity && !world.isClient()) {
            ((ZombieEntity) entity).convertTo(ModEntities.ZOMBO, true);
        }
        if(entity instanceof ZomboEntity && !world.isClient()) {
            MinecraftClient mc = MinecraftClient.getInstance();
            mc.player.sendChatMessage("Ah, a Zombo!", Text.literal("Ah, a Zombo!"));
        }
    }
}
