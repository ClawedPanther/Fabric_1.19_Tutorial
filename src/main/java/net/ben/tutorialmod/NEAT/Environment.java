package net.ben.tutorialmod.NEAT;

import java.util.ArrayList;

public interface Environment {

    void evaluateFitness(ArrayList<Genome> population);

}