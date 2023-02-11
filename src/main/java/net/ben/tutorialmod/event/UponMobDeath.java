package net.ben.tutorialmod.event;

import net.ben.tutorialmod.TutorialMod;
import net.ben.tutorialmod.TutorialModClient;
import net.ben.tutorialmod.entity.ModEntities;
import net.ben.tutorialmod.entity.ai.MobNEAT.ZomboNEAT;
import net.ben.tutorialmod.entity.custom.ZomboEntity;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;

import java.util.List;
import java.util.Random;

public class UponMobDeath implements ServerEntityEvents.Unload{
    protected int deaths = 0;

    @Override
    public void onUnload(Entity entity, ServerWorld world) {
        if (TutorialMod.zomboNEAT.getCurrentGenome()==0){
            Text message = Text.literal("Top Score: "+String.valueOf(TutorialMod.zomboNEAT.getLastBest())+" Generation: "+String.valueOf(TutorialMod.zomboNEAT.getGeneration()));
            entity.world.getPlayers().forEach(player -> player.sendMessage(message));
        }
        if(entity.getType() == ModEntities.ZOMBO) {
            TutorialMod.zomboNEAT.assignFitness((ZomboEntity) entity);
        }
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        Box box = new Box(x-100, y-5, z-100, x+100, y+5, z+100);
        List<Entity> entitys = entity.world.getOtherEntities(entity, box, EntityPredicates.VALID_LIVING_ENTITY);
        for (Entity e : entitys){
            if (e.getType() == EntityType.SKELETON && e.age > 2000){
                e.discard();
            }
        }
    }
}
