package io.github.evercraftmc.backuper.shared.backuper;

import java.util.ArrayList;
import java.util.List;

public class BackuperConfig {
    public enum LimitType {
        AMOUNT, SIZE
    }

    public String destination = "/backups";
    public List<String> filter = new ArrayList<String>();

    public Integer limit = 20;
    public LimitType limitType = LimitType.AMOUNT;
}