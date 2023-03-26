package net.ben.neatmobsmod.entity.ai.goal;

import net.ben.neatmobsmod.entity.custom.ZomboEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class EvaluateOutputs extends Goal {
    private final ZomboEntity mob;
    private double speed;
    private int jumpCooldown;
    private float slipperiness;
    private int jumpAssist;
    private int attackCooldown;


    @Override
    public boolean canStart() {
        return true;
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    /*
    initialises default values, Zombos are allowed to jump
    imminently after spawning but have to wait one second
    before they can attack
    */
    @Override
    public void start() {
        jumpCooldown = 0; jumpAssist = 0; attackCooldown = 20;
    }

    //initialises the goal
    public EvaluateOutputs (ZomboEntity mob, double speed){
        this.mob = mob;
        this.speed = speed;
    }

    /*
    moves the Zombo forwards if it is grounded or if it has jumped recently (for 0.2 seconds
    after jumping). The 'jump assist' helps let Zombos jump onto blocks, something that normal
    mobs are also capable of.
    */
    private void walkForwards(){
        if (this.mob.isOnGround() || jumpAssist != 0) {
            //get data of the block the mob is currently standing on
            BlockPos blockUnderPos = new BlockPos(new Vec3d( this.mob.getX(), this.mob.getY()-1, this.mob.getZ()));
            //slipperiness simulates the friction applied by a block
            slipperiness = this.mob.world.getBlockState(blockUnderPos).getBlock().getSlipperiness();
            double speedMod = this.speed * this.mob.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) * slipperiness;
            //babys move 50% faster than adults
            if (this.mob.isBaby()) {
                speedMod *= 1.5;
            }
            //speed is reduced to ~33% when partially or fully submerged in water
            if (this.mob.isTouchingWater()){
                speedMod *= 0.33;
            }
            /*
            velocity after jumping is decreased as default
            Minecraft zombies can not maneuver while in the air
             */
            if (this.mob.isOnGround()==false){
                speedMod /= 100;
            }
            //splits velocity in faced direction into X and Z components using trigonometry
            float yaw = this.mob.getAllYaw();
            double addX = MathHelper.sin(yaw * (float) Math.PI / 180) * speedMod;
            double addZ = MathHelper.cos(yaw * (float) Math.PI / 180) * speedMod;
            this.mob.setVelocity(this.mob.getVelocity().add(new Vec3d(-addX, 0.0d, addZ)));

            //keeps track of the forwards boost shortly after jumping
            if (jumpAssist != 0) {
                jumpAssist--;
            }
        }
    }

    //attacks targeted mob if close enough, 1 second cooldown between attacks
    private void attack(){
        //checks if mob has a target
        if (this.mob.getTarget() != null){
            LivingEntity target = this.mob.getTarget();

            //calculates distance between Zombo and target
            double square_distance = this.mob.squaredDistanceTo(target.getX(), target.getY(), target.getZ());
            double max_square_distance = this.mob.getWidth() * 2.0f * (this.mob.getWidth() * 2.0f) + target.getWidth();
            if (attackCooldown == 0) {
                attackCooldown = 20;
                this.mob.swingHand(Hand.MAIN_HAND);
                if (square_distance <= max_square_distance) {
                    this.mob.tryAttack(this.mob.getTarget());
                    this.mob.incrementSuccessfulHits();
                }
            }
        }
    }

    //makes Zombo jump if  grounded, adds 0.1 second cooldown between landing and jumping again
    private void jump(){
        if (jumpCooldown == 0 && this.mob.isOnGround()){
            this.mob.setVelocity(this.mob.getVelocity().add(new Vec3d(0.0d, 0.5d, 0.0d)));
            jumpCooldown = 2;
        }
    }

    //runs every game tick (roughly 20 times per second)
    @Override
    public void tick() {
        // resets jump assist and decrements jump cooldown if mob is grounded
        if (this.mob.isOnGround()){
            if (jumpCooldown > 0) {
                jumpCooldown--;
            }
            if (jumpAssist == 0){
                jumpAssist = 4;
            }
        }

        if (attackCooldown > 0){
            attackCooldown--;
        }

        //sets yaw value to equal the value of 'allYaw'

        float yaw = this.mob.getAllYaw();
        this.mob.setYaw(yaw);
        this.mob.setHeadYaw(yaw);
        this.mob.setBodyYaw(yaw);

        //check boolean action attributes
        if (this.mob.getTryJump()){
            jump();
        }
        if (this.mob.getTryWalk()){
            walkForwards();
        }
        if (this.mob.getTryAttack()){
            attack();
        }
    }
}
