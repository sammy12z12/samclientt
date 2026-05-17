package net.qolmod.util;

import java.util.ArrayDeque;
import java.util.Deque;

public class CpsTracker {

    private final Deque<Long> leftClicks  = new ArrayDeque<>();
    private final Deque<Long> rightClicks = new ArrayDeque<>();

    public void registerLeft()  { leftClicks.addLast(System.currentTimeMillis()); }
    public void registerRight() { rightClicks.addLast(System.currentTimeMillis()); }

    public void decay() {
        long cutoff = System.currentTimeMillis() - 1000;
        while (!leftClicks.isEmpty()  && leftClicks.peekFirst()  < cutoff) leftClicks.pollFirst();
        while (!rightClicks.isEmpty() && rightClicks.peekFirst() < cutoff) rightClicks.pollFirst();
    }

    public int getLeftCps()  { return leftClicks.size(); }
    public int getRightCps() { return rightClicks.size(); }
}
