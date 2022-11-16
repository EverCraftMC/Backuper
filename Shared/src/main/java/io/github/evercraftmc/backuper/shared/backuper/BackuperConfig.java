package io.github.evercraftmc.backuper.shared.backuper;

import java.util.ArrayList;
import java.util.List;

public class BackuperConfig {
    public enum LimitType {
        AMOUNT, SIZE
    }

    public enum SortMode {
        NONE, SIZE, SIZE_REVERSE
    }

    public String destination = "/backups";
    public SortMode sortMode = SortMode.SIZE;
    public List<String> filter = new ArrayList<String>();

    public Integer compressionLevel = 7;

    public Integer limit = 20;
    public LimitType limitType = LimitType.AMOUNT;
}