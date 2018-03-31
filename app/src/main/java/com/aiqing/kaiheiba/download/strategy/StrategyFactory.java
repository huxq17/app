package com.aiqing.kaiheiba.download.strategy;


public class StrategyFactory {
    public enum Strategies {
        SINGLE_THREAD(1, new DefaultDownloadStrategy()), MULTI_THREAD(2, new DefaultDownloadStrategy());
        private IDownloadStrategy downloadStrategy;
        private int threadNum;

        Strategies(int threadNum, IDownloadStrategy downloadStrategy) {
            this.downloadStrategy = downloadStrategy;
            this.threadNum = threadNum;
        }
    }

    public static IDownloadStrategy getDownloadStrategy(int threadNum) {
        if (threadNum > 1) threadNum = 2;
        for (Strategies strategies : Strategies.values()) {
            if (threadNum == strategies.threadNum) {
                return strategies.downloadStrategy;
            }
        }
        return null;
    }
}
