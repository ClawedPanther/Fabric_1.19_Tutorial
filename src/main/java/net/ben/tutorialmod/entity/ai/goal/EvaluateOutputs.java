package net.ben.tutorialmod.entity.ai.goal;

import net.ben.tutorialmod.entity.custom.ZomboEntity;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

public class EvaluateOutputs extends Goal {
    protected final ZomboEntity mob;
    protected double speed;
    protected int jumpCooldown;
    protected float slipperiness;
    protected int jumpAssist;
    protected int attackCooldown;


    @Override
    public boolean canStart() {
        return true;
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void start() {
        jumpCooldown = 0; jumpAssist = 0; attackCooldown = 20;
    }

    public EvaluateOutputs (ZomboEntity mob, double speed){
        this.mob = mob;
        this.speed = speed;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    private void walkForwards(){
        BlockPos blockUnderPos = new BlockPos(new Vec3d( this.mob.getX(), this.mob.getY()-1, this.mob.getZ()));
        double speedMod = this.speed * this.mob.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) * slipperiness;
        if (this.mob.isBaby()) {
            speedMod *= 1.5;
        }
        if (this.mob.isTouchingWater()){
            speedMod *= 0.33;
        }
        if (this.mob.isOnGround()) {
            slipperiness = this.mob.world.getBlockState(blockUnderPos).getBlock().getSlipperiness();
            float yaw = this.mob.getHeadYaw();
            double addX = MathHelper.sin(yaw * (float) Math.PI / 180) * speedMod;
            double addZ = MathHelper.cos(yaw * (float) Math.PI / 180) * speedMod;
            this.mob.setVelocity(this.mob.getVelocity().add(new Vec3d(-addX, 0.0d, addZ)));
        } else if (jumpAssist != 0) {
            float yaw = this.mob.getHeadYaw();
            double addX = MathHelper.sin(yaw * (float) Math.PI / 180) * speedMod;
            double addZ = MathHelper.cos(yaw * (float) Math.PI / 180) * speedMod;
            this.mob.setVelocity(this.mob.getVelocity().add(new Vec3d(-addX, 0.0d, addZ)));
            jumpAssist --;
        }

    }

    private void attackFront(){
        if (this.mob.getTarget() != null){
            LivingEntity target = this.mob.getTarget();
            double square_distance = this.mob.squaredDistanceTo(target.getX(), target.getY(), target.getZ());
            double max_square_distance = this.mob.getWidth() * 2.0f * (this.mob.getWidth() * 2.0f) + target.getWidth();
            if (attackCooldown == 0) {
                attackCooldown = 20;
                this.mob.swingHand(Hand.MAIN_HAND);
                if (square_distance <= max_square_distance) {
                    this.mob.tryAttack(this.mob.getTarget());
                }
            }
        }
    }


    private void jump(){
        if (jumpCooldown == 0 && this.mob.isOnGround()){
            this.mob.setVelocity(this.mob.getVelocity().add(new Vec3d(0.0d, 0.5d, 0.0d)));
            jumpCooldown = 2;
        }
    }

    @Override
    public void tick() {
        float yaw = this.mob.getAllYaw();
        this.mob.setYaw(yaw);
        this.mob.setHeadYaw(yaw);
        this.mob.setBodyYaw(yaw);

        if (this.mob.isOnGround()){
            if (jumpCooldown > 0) {
                jumpCooldown--;
            }
            if (jumpAssist != 0){
                jumpAssist = 0;
            }
        }
        if (attackCooldown > 0){attackCooldown--;}
        this.mob.setBodyYaw(this.mob.getHeadYaw());
        this.mob.setYaw(this.mob.getHeadYaw());
        if (this.mob.getTryJump()){
            jump();
        }
        if (this.mob.getTryWalk()){
            walkForwards();
        }
        if (this.mob.getTryAttack()){
            attackFront();
        }
    }
}
