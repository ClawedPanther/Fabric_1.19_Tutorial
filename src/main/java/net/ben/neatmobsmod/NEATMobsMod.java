package net.ben.neatmobsmod;

import net.ben.neatmobsmod.entity.ModEntities;
import net.ben.neatmobsmod.entity.ai.MobNEAT.ZomboNEAT;
import net.ben.neatmobsmod.entity.custom.ZomboEntity;
import net.ben.neatmobsmod.event.UponMobSpawn;
import net.ben.neatmobsmod.event.UponMobDeath;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// initialises non-client mod elements
public class NEATMobsMod implements ModInitializer {

	// initialises mod
	public static final String MOD_ID = "neatmobsmod";
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
