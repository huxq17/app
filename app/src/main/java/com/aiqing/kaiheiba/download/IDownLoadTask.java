package com.aiqing.kaiheiba.download;


public interface IDownLoadTask {

    void onBlockStart(int blockSize);

    void onBlockUpdate(int length);

    void onBlockFinish();
}
