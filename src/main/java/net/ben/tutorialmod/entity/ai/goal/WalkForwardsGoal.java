package net.ben.tutorialmod.entity.ai.goal;

import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.io.PrintStream;
import java.util.EnumSet;

public class WalkForwardsGoal extends Goal {


    protected final PathAwareEntity mob;
    protected double speed;
    protected int jumpCooldown;
    protected int jumpWait;
    protected float slipperiness;


    @Override
    public boolean shouldContinue() {
        return true;
    }


    @Override
    public void start() {
        jumpCooldown = 0; jumpWait = 0;
    }

    @Override
    public boolean canStart() {
        return true;
    }

    public WalkForwardsGoal (PathAwareEntity mob, double speed) {
        this.mob = mob;
        this.speed = speed;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public void tick() {
        BlockPos blockUnderPos = new BlockPos(new Vec3d( this.mob.getX(), this.mob.getY()-1, this.mob.getZ()));
        if (this.mob.getTarget() != null){
            this.mob.getLookControl().lookAt(this.mob.getTarget(), 30.0f, 30.0f);
        }
        if (this.mob.isOnGround()) {
            slipperiness = this.mob.world.getBlockState(blockUnderPos).getBlock().getSlipperiness();
            if (jumpCooldown < 2) {
                jumpCooldown++;
            }
            float yaw = this.mob.getHeadYaw();
            double speedMod = this.speed * this.mob.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) * slipperiness;
            double addX = MathHelper.sin(yaw * (float) Math.PI / 180) * speedMod;
            double addZ = MathHelper.cos(yaw * (float) Math.PI / 180) * speedMod;
            this.mob.setVelocity(this.mob.getVelocity().add(new Vec3d(-addX, 0.0d, addZ)));
        }
        if (this.mob.world.getBlockState(blockUnderPos).isOf(Blocks.AIR) && jumpCooldown == 2 && this.mob.isOnGround()){
            if (jumpWait == 3){
                this.mob.setVelocity(this.mob.getVelocity().add(new Vec3d(0.0d, 0.5d, 0.0d)));
                jumpCooldown = 0;
            } else {
                jumpWait ++;
            }
        } else {
            jumpWait = 0;
        }
        this.mob.setBodyYaw(this.mob.getHeadYaw());
        this.mob.setYaw(this.mob.getHeadYaw());
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }
}
