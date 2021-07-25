package rcomic.utils;

import java.util.concurrent.RecursiveTask;

/**
 * 分散搜尋任務
 *
 * @author Ray Lee Created on 2020/09/22
 */
public abstract class DistributedTask<T> extends RecursiveTask<T> {
  private static final long serialVersionUID = 1L;
  private int start;
  private int end;
  private int batch;

  /**
   * 設定分佈搜尋的起始點.
   *
   * @param start 起始點
   * @param end 結束點
   * @param batch 最少搜尋每批筆數
   */
  public DistributedTask(int start, int end, int batch) {
    this.start = start;
    this.end = end;
    this.batch = batch;
  }

  public boolean canCompute() {
    return (end - start) <= batch;
  }

  public int getMiddle() {
    return (start + end) / 2;
  }

  public int getStart() {
    return start;
  }

  public int getEnd() {
    return end;
  }

  public int getBatch() {
    return batch;
  }
}
