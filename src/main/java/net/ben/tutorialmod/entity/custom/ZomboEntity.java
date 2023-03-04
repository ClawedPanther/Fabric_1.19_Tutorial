package net.ben.tutorialmod.entity.custom;

import com.google.common.collect.ImmutableList;
import net.ben.tutorialmod.TutorialMod;
import net.ben.tutorialmod.entity.ModEntities;
import net.ben.tutorialmod.entity.ai.goal.CustomTargetGoal;
import net.ben.tutorialmod.entity.ai.goal.DetectNearbyBlock;
import net.ben.tutorialmod.entity.ai.goal.EvaluateOutputs;
import net.ben.tutorialmod.entity.ai.goal.WalkForwardsGoal;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.TargetPredicate;
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
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.text.Text;
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
    public int genomeNum = -1;
    public double totalDistanceToTarget = 0.0;
    public int ticksSurvived = 0;
    public int successfulHits = 0;
    public float damageTaken = 0.0f;
    public float allYaw = 0.0f;
    protected double lastDistance;


    @Override
    public boolean damage(DamageSource source, float amount) {
        this.damageTaken += amount;
        return super.damage(source, amount);
    }

    public ZomboEntity(EntityType<? extends ZombieEntity> entityType, World world) {
        super(entityType, world);
    }

    public double distanceToTarget(){

        double distance = 50;
        LivingEntity target = this.getTarget();
        if (target != null){
            distance = Math.sqrt(Math.pow(this.getX()-target.getX(), 2) + Math.pow(this.getZ()-target.getZ(), 2));
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

    public float getAllYaw(){
        return allYaw;
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

    public void setAllYaw(float allYawOutput){
        allYaw = allYawOutput;
    }

    @Override
    public void tick() {
        if (this.world.isClient == false&&this.isAlive()){
            ticksSurvived ++;
            if (ticksSurvived > 600){
                this.discard();
            }

            if (ticksSurvived%5!=1 || ticksSurvived==1){
                lastDistance = this.distanceToTarget();
            }
            if (ticksSurvived%5==0){
                this.setTarget(null);
            }
            totalDistanceToTarget += lastDistance;
            if ((ticksSurvived%2)==0 && genomeNum >= 0) {
                TutorialMod.zomboNEAT.getGenomeOutput(this);
            }
        }
        super.tick();
    }

    public float[] getZomboInputs(){
        float targetXDiff = 0;
        float targetZDiff = 0;
        float angleToTarget = 0;
        float hasTarget = 1;
        float toTarget;

        toTarget = ((float) this.distanceToTarget())/50;

        LivingEntity target = this.getTarget();
        if (target != null){
            targetXDiff = (float) (target.getX() - this.getX())/35;
            targetZDiff = (float) (target.getZ() - this.getZ())/35;
            angleToTarget=(float)((MathHelper.atan2((double)-targetXDiff, (double)targetZDiff))/(2*Math.PI));
        } else {
            hasTarget = 0;
        }
        float averageYaw = getAverageZomboYaw();
        averageYaw /= 360;



        return new float[]{toTarget, hasTarget, averageYaw, angleToTarget};
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
        if (totalZombos >0){
            averageYaw /= totalZombos;
        }
        return averageYaw;
    }

    @Override
    protected void initGoals() {
        this.initCustomGoals();
    }
    @Override
    protected void initCustomGoals() {
        this.goalSelector.add(2, new EvaluateOutputs(this, 0.37d));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, VillagerEntity.class,0, true, false, null));
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return ZombieEntity.createZombieAttributes();
    }

}
