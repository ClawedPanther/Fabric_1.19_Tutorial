package net.ben.neatmobsmod.entity.ai.goal;

import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.mob.MobEntity;

public class CustomTargetGoal extends ActiveTargetGoal {
    public CustomTargetGoal(MobEntity mob, Class targetClass, boolean checkVisibility) {
        super(mob, targetClass, checkVisibility);
    }

    @Override
    public void tick() {
        super.tick();
        findClosestTarget();
    }

    @Override
    public boolean shouldRunEveryTick() {return true;}
}
