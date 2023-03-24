package net.ben.tutorialmod.entity.custom;

import net.ben.tutorialmod.TutorialMod;
import net.ben.tutorialmod.entity.ModEntities;
import net.ben.tutorialmod.entity.ai.goal.EvaluateOutputs;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import java.util.List;

public class ZomboEntity extends ZombieEntity{
    private boolean walkForwards = false;
    private boolean jump = false;
    private boolean attack = false;
    private int genomeNum = -1;
    private double totalDistanceToTarget = 0.0;
    private int ticksSurvived = 0;
    private int successfulHits = 0;
    private float allYaw = 0.0f;
    private double lastDistance;

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

    // accessors and mutators

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

    /*
    totalDistanceToTarget and ticksSurvived are
    never needed as individual values, so the
    accessor need only return the average distance
    to the target
     */
    public double getAverageDistanceToTarget(){
        return totalDistanceToTarget/ticksSurvived;
    }

    public int getSuccessfulHits() {
        return successfulHits;
    }

    /*
    since successfulHits is only ever modified by
    increasing it by one, its setter can just
    increment it rather than set it
     */
    public void incrementSuccessfulHits(){
        successfulHits++;
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

    public int getGenomeNum() {
        return genomeNum;
    }

    public void setGenomeNum(int newGenomeNum){
        genomeNum = newGenomeNum;
    }

    // gets run every in game tick (20 times per second)
    @Override
    public void tick() {
        /*
         values need only be updated on client ticks
         (otherwise they will be updated twice per tick)
         and only when the Zombo is alive (since mobs still
         exist for a short time after dying)
         */
        if (this.world.isClient == false&&this.isAlive()){
            ticksSurvived ++;
            /*
             limits Zombos' lifetimes as they would
             otherwise live forever (and thus never
             train)
             */
            if (ticksSurvived > 6000){
                this.discard();
            }

            /*
            to allow Zombos to track the nearest target,
            their current target is reset every quarter of
            a second.
            Since the Zombo will have no target during this time
            the most recently recorded distance from the target
            is used instead.
            when the target is not being reset,
             */
            if (ticksSurvived%5!=1 || ticksSurvived==1){
                lastDistance = this.distanceToTarget();
            }
            if (ticksSurvived%5==0){
                this.setTarget(null);
            }

            // update total distance from target
            totalDistanceToTarget += lastDistance;
            if ((ticksSurvived%2)==0 && genomeNum >= 0) {
                TutorialMod.zomboNEAT.getGenomeOutput(this);
            }
        }
        super.tick();
    }

    /*
    sends input data to genome for evaluation
    all input data is a value between 0 and 1
     */
    public float[] getZomboInputs(){
        float targetXDiff = 0;
        float targetZDiff = 0;
        float angleToTarget = 0;
        float hasTarget = 1;
        float toTarget;

        //normalises input value to be between 0 and 1
        toTarget = ((float) this.distanceToTarget())/50;

        /*
        finds the distance between the Zombo and
        its target (in the XZ/horizontal plane)
         */
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

    /*
    calculates the average angle that Zombos
    within a 5 block cube are facing in the
     */
    private float getAverageZomboYaw (){
        float totalYaw = 0;
        float averageYaw = 0;
        int totalZombos = 0;
        /*
        uses some of Minecraft's methods to get
        a list of all the entitys in a 10x10x10
        cube centred on the current Zombo
         */
        Box boxToCheck = new Box(this.getX()-5, this.getY()-5, this.getZ()-5, this.getX()+5, this.getY()+5, this.getZ()+5);
        List<Entity> nearby = this.world.getOtherEntities(this, boxToCheck);
        /*
        finds total number of Zombos in the surrounding area
        and sums all of their yaw values
         */
        for (Entity entity:nearby){
            if (entity.getType() == ModEntities.ZOMBO){
                totalZombos ++;
                totalYaw += ((ZomboEntity) entity).getAllYaw();
            }
        }
        //avoids 0 division error when calculating average
        if (totalZombos >0){
            averageYaw = totalYaw/totalZombos;
        }
        return averageYaw;
    }


    /*
     sets the Zombo's goal to the
     EvaluateOutputs goal and the
     target selector to Minecraft's
     ActiveTargetGoal, which finds the
     closest entity of the chosen type
     (in this case villagers) within the
     mobs vision range and sets them as
     the mob's target
     */
    @Override
    protected void initGoals() {
        this.goalSelector.add(2, new EvaluateOutputs(this, 0.37d));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, VillagerEntity.class,0, true, false, null));
    }

    /*
    set the Zombo's attributes (attack
    speed, damage, health, etc.) to be
    the same as the default Minecraft
    zombie
     */
    public static DefaultAttributeContainer.Builder setAttributes() {
        return ZombieEntity.createZombieAttributes();
    }

}
