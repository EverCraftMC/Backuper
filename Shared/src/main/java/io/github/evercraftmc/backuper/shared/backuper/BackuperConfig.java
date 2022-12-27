package io.github.evercraftmc.backuper.shared.backuper;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class BackuperConfig {
    public enum LimitType {
        NONE, AMOUNT, SIZE
    }

    public enum SortMode {
        NONE, SIZE, SIZE_REVERSE, MODIFIED, MODIFIED_REVERSE
    }

    public String destination = "/backups";
    public List<String> filter = new ArrayList<String>();
    public SortMode sortMode = SortMode.SIZE;

    public Integer compressionLevel = 5;

    public String timezone = ZoneId.systemDefault().getId();

    public Integer limit = 20;
    public LimitType limitType = LimitType.AMOUNT;
}