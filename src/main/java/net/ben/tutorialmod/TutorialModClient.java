package net.ben.tutorialmod;

import net.ben.tutorialmod.entity.ModEntities;
import net.ben.tutorialmod.entity.model.ZomboEntityModel;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.client.render.entity.ZombieEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;

//initialises client side mod elements
public class TutorialModClient implements ClientModInitializer {
    public static final EntityModelLayer MODEL_ZOMBO_LAYER = new EntityModelLayer(new Identifier(TutorialMod.MOD_ID, "zombo"), "main");

    /*
    initalises gamerule, boolean variables that can be changed
    while the program is running.
    the gamerule allows for best genome generation to be toggled
     */
    //
    public static final GameRules.Key<GameRules.BooleanRule> GENERATE_BEST_ZOMBOS =
            GameRuleRegistry.register("generateBestZombos", GameRules.Category.UPDATES, GameRuleFactory.createBooleanRule(false));

    @Override
    public void onInitializeClient() {
        //initialises the Zombo model renderer
        EntityRendererRegistry.register(ModEntities.ZOMBO, ZombieEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(MODEL_ZOMBO_LAYER, ZomboEntityModel::getTexturedModelData);
    }
}