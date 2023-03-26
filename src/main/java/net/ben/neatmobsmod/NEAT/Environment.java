package net.ben.neatmobsmod.NEAT;

import java.util.ArrayList;

public interface Environment {

    void evaluateFitness(ArrayList<Genome> population);

}