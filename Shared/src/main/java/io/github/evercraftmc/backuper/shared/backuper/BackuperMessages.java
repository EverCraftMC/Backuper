package io.github.evercraftmc.backuper.shared.backuper;

public class BackuperMessages {
    public static class Error {
        public String noPerms = "&cYou need the permission \"{permission}\" to do that";
        public String invalidArgs = "&cInvalid arguments";
    }

    public static class Reload {
        public String reloading = "&aReloading plugin..";
        public String reloaded = "&aSuccessfully reloaded";
    }

    public static class Backup {
        public String starting = "&aStarting a backup of all data..";
        public String stopping = "&aStopping the current backup..";
        public String finished = "&aSuccessfully backed up all data";

        public String alreadyRunning = "&cThere is already a backup running";
        public String notRunning = "&cThere is no backup currently running";
    }

    public Error error = new Error();

    public Reload reload = new Reload();

    public Backup backup = new Backup();
}