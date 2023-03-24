package net.ben.tutorialmod;

import net.ben.tutorialmod.entity.ModEntities;
import net.ben.tutorialmod.entity.ai.MobNEAT.ZomboNEAT;
import net.ben.tutorialmod.entity.custom.ZomboEntity;
import net.ben.tutorialmod.event.UponMobSpawn;
import net.ben.tutorialmod.event.UponMobDeath;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// initialises non-client mod elements
public class TutorialMod implements ModInitializer {

	// initialises mod
	public static final String MOD_ID = "tutorialmod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	// zomboNeat instance is stored in a location accessible to other classes
	public static ZomboNEAT zomboNEAT;



	@Override
	public void onInitialize() {
		// initialises mod events
		ServerEntityEvents.ENTITY_LOAD.register(new UponMobSpawn());
		ServerEntityEvents.ENTITY_UNLOAD.register(new UponMobDeath());

		// initialises ZomboNEAT instance
		zomboNEAT = new ZomboNEAT();

		// initialises Zombos, allowing them to spawn in the game
		FabricDefaultAttributeRegistry.register(ModEntities.ZOMBO, ZomboEntity.setAttributes());

	}
}
