package net.ben.tutorialmod.event;

import net.ben.tutorialmod.TutorialMod;
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
        if(entity instanceof ZombieEntity && !world.isClient() && !(entity instanceof ZomboEntity)) {
//            ZomboEntity replacementZombo = ModEntities.ZOMBO.create(entity.world);
//            replacementZombo.copyPositionAndRotation(entity);
//            replacementZombo.setBaby(((ZombieEntity) entity).isBaby());
//            replacementZombo.setAiDisabled(((ZombieEntity) entity).isAiDisabled());
//            entity.world.spawnEntity(replacementZombo);
//            replacementZombo.setCanPickUpLoot(((ZombieEntity) entity).canPickUpLoot());
//            for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
//                ItemStack itemStack = ((ZombieEntity) entity).getEquippedStack(equipmentSlot);
//                if (itemStack.isEmpty()) continue;
//                replacementZombo.equipStack(equipmentSlot, itemStack.copy());
//                replacementZombo.setEquipmentDropChance(equipmentSlot, 0.085f);
//                itemStack.setCount(0);
//            }
//            ((ZombieEntity) entity).world.spawnEntity(replacementZombo);
//            if (((ZombieEntity) entity).hasVehicle()) {
//                Entity vehicleEntity = ((ZombieEntity) entity).getVehicle();
//                ((ZombieEntity) entity).stopRiding();
//                replacementZombo.startRiding(vehicleEntity, true);
//            }
//            ((ZombieEntity) entity).discard();
            ((ZombieEntity) entity).convertTo(ModEntities.ZOMBO, true);
        }
        if(entity instanceof ZomboEntity && !world.isClient()) {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player != null){
                mc.player.sendMessage(Text.literal("Ah, a Zombo!"));

            }
        }
    }
}
