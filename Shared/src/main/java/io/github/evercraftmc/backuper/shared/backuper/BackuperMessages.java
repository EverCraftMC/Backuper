package io.github.evercraftmc.backuper.shared.backuper;

public class BackuperMessages {
    public static class Error {
        public String noPerms = "&cYou need the permission \"{permission}\" to do that";
        public String noConsole = "&cYou can't do that from the console";
        public String playerNotFound = "&cCouldn't find player \"{player}\"";
        public String invalidArgs = "&cInvalid arguments";
    }

    public static class Reload {
        public String reloading = "&aReloading plugin..";
        public String reloaded = "&aSuccessfully reloaded";
    }

    public static class Backup {
        public String backingUp = "&aBacking up data..";
        public String backedUp = "&aSuccessfully backed up all data";
    }

    public Error error = new Error();

    public Reload reload = new Reload();

    public Backup backup = new Backup();
}