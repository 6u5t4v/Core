package com.Furnesse.core.utils;

import com.Furnesse.core.Core;

public class TPSMeter implements Runnable {
	private Core plugin;

	private static String bestTPS;

	private static String goodTPS;

	private static String decentTPS;

	private static String badTPS;

	private static String veryBadTPS;

	private static String awfulTPS;

	private long secstart;

	private long secend;

	private static long tps;

	private int ticks;

	public TPSMeter(Core plugin) {
		this.plugin = plugin;
		bestTPS = plugin.getConfig().getString("BestTPS");
		goodTPS = plugin.getConfig().getString("GoodTPS");
		decentTPS = plugin.getConfig().getString("DecentTPS");
		badTPS = plugin.getConfig().getString("BadTPS");
		veryBadTPS = plugin.getConfig().getString("ReallyBadTPS");
		awfulTPS = plugin.getConfig().getString("AwfulTPS");
	}

	public void run() {
		this.secstart = System.currentTimeMillis() / 1000L;
		if (this.secstart == this.secend) {
			this.ticks++;
		} else {
			this.secend = this.secstart;
			tps = (tps == 0L) ? this.ticks : ((tps + this.ticks) / 2L);
			this.ticks = 1;
		}
	}

	public static String getTPS() {
		if (tps >= 20L)
			return String.valueOf(bestTPS) + '\024';
		if (tps < 20L && tps >= 18L)
			return String.valueOf(goodTPS) + tps;
		if (tps < 18L && tps >= 16L)
			return String.valueOf(decentTPS) + tps;
		if (tps < 16L && tps >= 14L)
			return String.valueOf(badTPS) + tps;
		if (tps < 14L && tps >= 12L)
			return String.valueOf(veryBadTPS) + tps;
		if (tps < 12L)
			return String.valueOf(awfulTPS) + tps;
		return String.valueOf(awfulTPS) + "1";
	}
}
