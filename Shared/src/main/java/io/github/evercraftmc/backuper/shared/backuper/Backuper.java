package io.github.evercraftmc.backuper.shared.backuper;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import io.github.evercraftmc.backuper.shared.backuper.BackuperConfig.LimitType;
import io.github.evercraftmc.backuper.shared.backuper.BackuperConfig.SortMode;
import io.github.kale_ko.ejcl.Config;

public class Backuper {
    private Config<BackuperConfig> config;

    private BackuperRun currentRun = null;

    public Backuper(Config<BackuperConfig> config) {
        this.config = config;
    }

    public static class BackuperRun {
        private File source;
        private File destination;

        private List<String> filter;
        private SortMode sortMode;

        private int compressionLevel;

        private String timezone;

        private int limit;
        private LimitType limitType;

        private boolean trackStats;

        private File zipFile;
        private ZipOutputStream zip;

        private List<File> files = new ArrayList<File>();

        private int totalFiles = 0;
        private int finishedFiles = 0;

        private long totalBytes = 0l;
        private long finishedBytes = 0l;

        private boolean stopped = false;

        public BackuperRun(File source, File destination, List<String> filter, SortMode sortMode, int compressionLevel, String timezone, int limit, LimitType limitType, boolean trackStats) {
            this.source = source;
            this.destination = destination;

            this.filter = filter;
            this.sortMode = sortMode;

            this.compressionLevel = compressionLevel;

            this.timezone = timezone;

            this.limit = limit;
            this.limitType = limitType;

            this.trackStats = trackStats;
        }

        public void start() throws IOException {
            if (!this.stopped) {
                this.files = this.getFiles(this.source, true);

                this.files = this.filterFiles(this.files);

                if (this.sortMode == SortMode.SIZE) {
                    this.files.sort((a, b) -> {
                        if (a.length() > b.length()) {
                            return 1;
                        } else if (b.length() > a.length()) {
                            return -1;
                        } else {
                            return 0;
                        }
                    });
                } else if (this.sortMode == SortMode.SIZE_REVERSE) {
                    this.files.sort((a, b) -> {
                        if (a.length() < b.length()) {
                            return 1;
                        } else if (b.length() < a.length()) {
                            return -1;
                        } else {
                            return 0;
                        }
                    });
                } else if (this.sortMode == SortMode.MODIFIED) {
                    this.files.sort((a, b) -> {
                        if (a.lastModified() > b.lastModified()) {
                            return 1;
                        } else if (b.lastModified() > a.lastModified()) {
                            return -1;
                        } else {
                            return 0;
                        }
                    });
                } else if (this.sortMode == SortMode.MODIFIED_REVERSE) {
                    this.files.sort((a, b) -> {
                        if (a.lastModified() < b.lastModified()) {
                            return 1;
                        } else if (b.lastModified() < a.lastModified()) {
                            return -1;
                        } else {
                            return 0;
                        }
                    });
                }

                if (this.trackStats) {
                    this.totalFiles = this.files.size();

                    for (File file : this.files) {
                        this.totalBytes += file.length();
                    }
                }

                this.zipFile = new File(this.destination.getAbsolutePath() + File.separator + "Backup-" + DateTimeFormatter.ofPattern("MM-d-yy-HH-mm-ss").withLocale(Locale.US).withZone(ZoneId.of(this.timezone)).format(Instant.now()) + ".zip");
                if (!zipFile.exists()) {
                    Files.createFile(zipFile.toPath());
                }

                this.zip = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(this.zipFile)));
                this.zip.setLevel(this.compressionLevel);

                for (File file : this.files) {
                    if (!this.stopped) {
                        if (file.exists()) {
                            this.backupFile(file);
                        }
                    } else {
                        return;
                    }
                }

                this.zip.close();

                this.stopped = true;

                this.cullOld();
            } else {
                throw new RuntimeException("Backuper run is already stopped");
            }
        }

        public boolean isStopped() {
            return this.stopped;
        }

        public void stop() throws IOException {
            if (!this.stopped) {
                if (this.zipFile != null) {
                    Files.delete(this.zipFile.toPath());
                }

                this.stopped = true;
            } else {
                throw new RuntimeException("Backuper run is already stopped");
            }
        }

        public int getFinished() {
            return this.finishedFiles;
        }

        public int getTotal() {
            return this.totalFiles;
        }

        public float getFinishedPercent() {
            return ((float) this.finishedFiles) / ((float) this.totalFiles);
        }

        public long getFinishedBytes() {
            return this.finishedBytes;
        }

        public long getTotalBytes() {
            return this.totalBytes;
        }

        public float getFinishedBytesPercent() {
            return ((float) this.finishedBytes) / ((float) this.totalBytes);
        }

        private List<File> getFiles(File directory, Boolean deep) {
            List<File> files = new ArrayList<File>();

            for (File file : directory.listFiles()) {
                if (file.isDirectory() && deep) {
                    List<File> childFiles = this.getFiles(file, deep);
                    files.addAll(childFiles);
                } else if (file.isFile()) {
                    files.add(file);
                }
            }

            return files;
        }

        private List<File> filterFiles(List<File> files) {
            List<File> filteredFiles = new ArrayList<File>();

            for (File file : files) {
                String path = file.getAbsolutePath().replace(this.source.getAbsolutePath(), "").replace(File.separator, "/");

                Boolean excluded = false;
                for (String condition : this.filter) {
                    if ((condition.startsWith("!") && (new WildcardFileFilter(condition.toLowerCase().substring(1), IOCase.SENSITIVE).accept(file) || path.toLowerCase().startsWith(condition.toLowerCase().substring(1).replace(File.separator, "/")))) || file.equals(this.destination)) {
                        excluded = true;

                        break;
                    }
                }

                if (!excluded) {
                    for (String condition : this.filter) {
                        if (!condition.startsWith("!") && (new WildcardFileFilter(condition.toLowerCase(), IOCase.SENSITIVE).accept(file) || path.toLowerCase().startsWith(condition.toLowerCase().replace(File.separator, "/")))) {
                            filteredFiles.add(file);

                            break;
                        }
                    }
                }
            }

            return filteredFiles;
        }

        private void backupFile(File file) throws IOException {
            ZipEntry entry = new ZipEntry(file.getAbsolutePath().replace(this.source.getAbsolutePath() + File.separator, ""));
            entry.setTime(file.lastModified());
            this.zip.putNextEntry(entry);

            BufferedInputStream fileInputStream = new BufferedInputStream(new FileInputStream(file));

            byte[] buffer = new byte[4096];
            int read = 0;
            while ((read = fileInputStream.read(buffer)) > 0) {
                this.zip.write(buffer, 0, read);

                if (this.trackStats) {
                    this.finishedBytes += read;
                }

                if (read != buffer.length) {
                    break;
                }
            }

            if (this.trackStats) {
                this.finishedFiles++;
            }

            fileInputStream.close();
            this.zip.closeEntry();
        }

        private void cullOld() throws IOException {
            List<File> files = this.getFiles(this.destination, false);

            files.sort((a, b) -> {
                if (a.lastModified() < b.lastModified()) {
                    return 1;
                } else if (b.lastModified() < a.lastModified()) {
                    return -1;
                } else {
                    return 0;
                }
            });

            long x = 0l;
            for (File file : files) {
                if (file.getName().startsWith("Backup-")) {
                    if (this.limitType == LimitType.AMOUNT) {
                        x++;
                    } else if (this.limitType == LimitType.SIZE) {
                        x += file.length() / 1000000;
                    }

                    if (x > this.limit) {
                        Files.delete(file.toPath());
                    }
                }
            }
        }
    }

    public BackuperRun getCurrentRun() {
        return this.currentRun;
    }

    public void startBackup() {
        if (this.currentRun == null) {
            try {
                this.currentRun = new BackuperRun(new File(System.getProperty("user.dir")), new File(System.getProperty("user.dir") + this.config.get().destination), this.config.get().filter, this.config.get().sortMode, this.config.get().compressionLevel, this.config.get().timezone, this.config.get().limit, this.config.get().limitType, true);
                this.currentRun.start();

                this.currentRun = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            throw new RuntimeException("A backup is already running");
        }
    }

    public void stopBackup() {
        if (this.currentRun != null) {
            try {
                this.currentRun.stop();
                this.currentRun = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            throw new RuntimeException("No backup is currently running");
        }
    }
}