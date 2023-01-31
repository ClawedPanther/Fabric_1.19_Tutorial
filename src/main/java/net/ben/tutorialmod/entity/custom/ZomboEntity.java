package net.ben.tutorialmod.entity.custom;

import com.google.common.collect.ImmutableList;
import net.ben.tutorialmod.TutorialMod;
import net.ben.tutorialmod.entity.ModEntities;
import net.ben.tutorialmod.entity.ai.goal.DetectNearbyBlock;
import net.ben.tutorialmod.entity.ai.goal.EvaluateOutputs;
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
    public boolean walkForwards = false;
    public boolean jump = false;
    public boolean attack = false;
    public int genomeNum;
    public double totalDistanceToPlayer = 0.0;
    public int ticksSurvived = 0;
    public int successfulHits = 0;
    public float damageTaken = 0.0f;


    @Override
    public boolean damage(DamageSource source, float amount) {
        this.damageTaken += amount;
        return super.damage(source, amount);
    }

    public ZomboEntity(EntityType<? extends ZombieEntity> entityType, World world) {
        super(entityType, world);
    }

    public double distanceToPlayer(){
        double distance = this.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE);
        PlayerEntity closestPlayer = this.world.getClosestPlayer(this.getX(), this.getY(), this.getZ(), this.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE), true);
        if (closestPlayer != null){
            distance = closestPlayer.squaredDistanceTo(this.getX(), this.getY(), this.getZ());
        }
        return distance;

    }

    public boolean getTryJump(){
        return jump;
    }

    public boolean getTryWalk(){
        return walkForwards;
    }

    public boolean getTryAttack(){
        return attack;
    }

    public void setTryJump(boolean jumpOutput){
        jump = jumpOutput;
    }

    public void setTryWalk(boolean walkOutput){
        walkForwards = walkOutput;
    }

    public void setTryAttack(boolean attackOutput){
        attack = attackOutput;
    }

    @Override
    public void tick() {
        ticksSurvived ++;
        totalDistanceToPlayer += this.distanceToPlayer();
        TutorialMod.zomboNEAT.getGenomeOutput(this);
        super.tick();
    }

    public float[] getZomboInputs(){
        float targetXDiff = 0;
        float targetZDiff = 0;
        float hasTarget = 1;
        if (this.getTarget() != null){
            targetXDiff = (float) (this.getTarget().getX() - this.getX())/35;
            targetZDiff = (float) (this.getTarget().getZ() - this.getZ())/35;
        } else {
            hasTarget = 0;
        }
        float averageYaw = getAverageZomboYaw();
        averageYaw /= 360;

        return new float[]{targetXDiff, targetZDiff, hasTarget, averageYaw};
    }

    private float getAverageZomboYaw (){
        float averageYaw = 0;
        int totalZombos = 0;
        List<Entity> nearby = this.world.getOtherEntities(this, new Box(this.getX()-5, this.getY()-5, this.getZ()-5, this.getX()+5, this.getY()+5, this.getZ()+5));
        for (Entity entity:nearby){
            if (entity.getType() == ModEntities.ZOMBO){
                totalZombos ++;
                averageYaw += entity.getYaw();
            }
        }
        averageYaw /= totalZombos;
        return averageYaw;
    }

    @Override
    protected void initGoals() {
        this.initCustomGoals();
    }
    @Override
    protected void initCustomGoals() {
        this.goalSelector.add(2, new EvaluateOutputs(this, 0.37d));
        this.targetSelector.add(1, new ActiveTargetGoal<SkeletonEntity>(this, SkeletonEntity.class, true));
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return ZombieEntity.createZombieAttributes();
    }

}
