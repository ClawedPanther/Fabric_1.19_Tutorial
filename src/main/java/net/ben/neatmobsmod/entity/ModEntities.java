package net.ben.neatmobsmod.entity;

import net.ben.neatmobsmod.NEATMobsMod;
import net.ben.neatmobsmod.entity.custom.ZomboEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;


// adds Zombos to the list of Entities that can spawn
public class ModEntities {
    public static final EntityType<ZomboEntity> ZOMBO = Registry.register(
            Registry.ENTITY_TYPE, new Identifier(NEATMobsMod.MOD_ID, "zombo"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, ZomboEntity::new).dimensions(EntityDimensions.fixed(0.6f, 1.95f)).build());
}
