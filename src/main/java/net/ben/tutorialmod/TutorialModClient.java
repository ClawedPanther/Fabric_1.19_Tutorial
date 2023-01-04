package net.ben.tutorialmod;

import net.ben.tutorialmod.entity.ModEntities;
import net.ben.tutorialmod.entity.model.ZomboEntityModel;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.ZombieEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;

public class TutorialModClient implements ClientModInitializer {
    public static final EntityModelLayer MODEL_ZOMBO_LAYER = new EntityModelLayer(new Identifier(TutorialMod.MOD_ID, "zombo"), "main");

    public static final GameRules.Key<GameRules.BooleanRule> GENERATE_ZOMBOS =
            GameRuleRegistry.register("generateZombos", GameRules.Category.UPDATES, GameRuleFactory.createBooleanRule(true));

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.ZOMBO, (context) -> {
            return new ZombieEntityRenderer(context);
        });

        EntityModelLayerRegistry.registerModelLayer(MODEL_ZOMBO_LAYER, ZomboEntityModel::getTexturedModelData);
    }
}