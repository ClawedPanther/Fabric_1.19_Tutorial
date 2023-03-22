package net.ben.tutorialmod;

import net.ben.tutorialmod.entity.ModEntities;
import net.ben.tutorialmod.entity.ai.MobNEAT.ZomboNEAT;
import net.ben.tutorialmod.entity.custom.ZomboEntity;
import net.ben.tutorialmod.event.UponMobSpawn;
import net.ben.tutorialmod.event.UponMobDeath;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRenderers;
import net.minecraft.world.GameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bernie.geckolib3.GeckoLib;

public class TutorialMod implements ModInitializer {

	public static final String MOD_ID = "tutorialmod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static ZomboNEAT zomboNEAT;



	@Override
	public void onInitialize() {
		ServerEntityEvents.ENTITY_LOAD.register(new UponMobSpawn());
		ServerEntityEvents.ENTITY_UNLOAD.register(new UponMobDeath());
		
		zomboNEAT = new ZomboNEAT();

		FabricDefaultAttributeRegistry.register(ModEntities.ZOMBO, ZomboEntity.setAttributes());

	}
}
