package net.ben.tutorialmod.entity.custom;

import com.google.common.collect.ImmutableList;
import net.ben.tutorialmod.entity.ai.goal.WalkForwardsGoal;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.control.JumpControl;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameRules;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.List;

public class ZomboEntity extends ZombieEntity{


    public ZomboEntity(EntityType<? extends ZombieEntity> entityType, World world) {
        super(entityType, world);
    }


    @Override
    protected void initGoals() {
        this.initCustomGoals();
    }
    @Override
    protected void initCustomGoals() {
        this.goalSelector.add(2, new WalkForwardsGoal((PathAwareEntity) this, 0.37d));
        this.targetSelector.add(1, new ActiveTargetGoal<SkeletonEntity>((MobEntity)this, SkeletonEntity.class, true));
    }


    public static DefaultAttributeContainer.Builder setAttributes() {
        return ZombieEntity.createZombieAttributes();
    }

}
