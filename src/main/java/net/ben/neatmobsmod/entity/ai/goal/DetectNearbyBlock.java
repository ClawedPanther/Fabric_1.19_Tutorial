package net.ben.neatmobsmod.entity.ai.goal;

import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class DetectNearbyBlock extends Goal {

    protected final PathAwareEntity mob;

    public DetectNearbyBlock (PathAwareEntity mob) {
        this.mob = mob;
    }
    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public boolean canStart() {
        return true;
    }
    @Override
    public boolean shouldContinue() {
        return true;
    }

    @Override
    public void tick() {
        Vec3d blockHeadPos = new Vec3d( this.mob.getX(), this.mob.getY()+1, this.mob.getZ());
        double mobX = (int)this.mob.getX();
        double mobY = (int)this.mob.getY()+1;
        double mobZ = (int)this.mob.getZ();
        double radius = 35.0;

        for (double x = mobX-radius; x<mobX+radius+1.0; x++){
            for (double y = mobY-radius; y<mobY+radius+1.0; y++){
                for (double z = mobZ-radius; z<mobZ+radius+1.0; z++){
                    BlockPos current = new BlockPos(new Vec3d(x,y,z));
                    if (this.mob.world.getBlockState(current).isOf(Blocks.JUKEBOX)){
                        System.out.printf("Jukebox Detected");
                    }
                }
            }
        }

    }
}
