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
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import io.github.evercraftmc.backuper.shared.backuper.BackuperConfig.LimitType;
import io.github.evercraftmc.backuper.shared.backuper.BackuperConfig.SortMode;
import io.github.evercraftmc.backuper.shared.config.FileConfig;

public class Backuper {
    private FileConfig<BackuperConfig> config;

    private BackupRun currentRun = null;

    public Backuper(FileConfig<BackuperConfig> config) {
        this.config = config;
    }

    public static class BackupRun {
        private File source;
        private File destination;

        private SortMode sortMode;
        private List<String> filter;

        private Integer compressionLevel;

        private LimitType limitType;
        private Integer limit;

        private Boolean trackStats;

        private File zipFile;
        private ZipOutputStream zip;

        private List<File> files = new ArrayList<File>();

        private Boolean stopped = false;

        private Integer totalFiles = 0;
        private Long totalBytes = 0l;

        private Integer finishedFiles = 0;
        private Long finishedBytes = 0l;

        public BackupRun(File source, File destination, SortMode sortMode, List<String> filter, Integer compressionLevel, LimitType limitType, Integer limit, Boolean trackStats) {
            this.source = source;
            this.destination = destination;

            this.sortMode = sortMode;
            this.filter = filter;

            this.compressionLevel = compressionLevel;

            this.limitType = limitType;
            this.limit = limit;

            this.trackStats = trackStats;
        }

        public void start() {
            try {
                this.files = this.getFiles(this.source, true);

                this.files = this.filterFiles(this.files);

                if (this.sortMode == SortMode.SIZE) {
                    this.files.sort((a, b) -> {
                        return (int) (b.length() - a.length());
                    });
                } else if (this.sortMode == SortMode.SIZE_REVERSE) {
                    this.files.sort((a, b) -> {
                        return (int) (a.length() - b.length());
                    });
                }

                if (this.trackStats) {
                    for (File file : this.files) {
                        this.totalFiles++;
                        this.totalBytes += file.length();
                    }
                }

                this.zipFile = new File(this.destination.getAbsolutePath() + File.separator + "Backup-" + DateTimeFormatter.ofPattern("MM-d-yy-HH-mm-ss").withLocale(Locale.US).withZone(ZoneId.of("-05:00")).format(Instant.now()) + ".zip");
                if (!zipFile.exists()) {
                    zipFile.createNewFile();
                }

                this.zip = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(this.zipFile)));
                this.zip.setLevel(this.compressionLevel);

                for (File file : this.files) {
                    if (!this.stopped) {
                        this.backupFile(file);
                    } else {
                        return;
                    }
                }

                this.zip.close();

                this.cullOld();

                this.stopped = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void stop() {
            if (this.zipFile != null) {
                try {
                    Files.delete(this.zipFile.toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            this.stopped = true;
        }

        public Integer getTotal() {
            return this.totalFiles;
        }

        public Integer getFinished() {
            return this.finishedFiles;
        }

        public Float getFinishedPercent() {
            return ((float) this.totalFiles) / ((float) this.finishedFiles);
        }

        public Long getTotalBytes() {
            return this.totalBytes;
        }

        public Long getFinishedBytes() {
            return this.finishedBytes;
        }

        public Float getFinishedBytesPercent() {
            return (float) (this.totalBytes / this.finishedBytes);
        }

        private List<File> getFiles(File directory, Boolean deep) {
            List<File> files = new ArrayList<File>();

            for (File file : directory.listFiles()) {
                if (file.isDirectory() && deep) {
                    files.addAll(this.getFiles(file, deep));
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
                    if ((condition.startsWith("!") && (new WildcardFileFilter(condition.toLowerCase().substring(1), IOCase.SENSITIVE).accept(file) || path.toLowerCase().startsWith(condition.toLowerCase().substring(1).replace(File.separator, "/")))) || file == this.destination) {
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

        private void backupFile(File file) {
            try {
                if (!file.exists()) {
                    return;
                }

                ZipEntry entry = new ZipEntry(file.getAbsolutePath().replace(this.source.getAbsolutePath() + File.separator, ""));
                entry.setTime(file.lastModified());
                this.zip.putNextEntry(entry);

                BufferedInputStream fileInputStream = new BufferedInputStream(new FileInputStream(file));

                byte[] buffer = new byte[2048];
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

                this.finishedFiles++;

                this.zip.closeEntry();
                fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void cullOld() {
            try {
                List<File> backups = this.getFiles(this.destination, false);
                backups.sort(Comparator.comparingLong(File::lastModified).reversed());

                Long x = 0l;
                for (File backup : backups) {
                    if (backup.getName().startsWith("Backup-")) {
                        if (this.limitType == LimitType.AMOUNT) {
                            x++;
                        } else if (this.limitType == LimitType.SIZE) {
                            x += backup.length() / 1000000;
                        }

                        if (x > this.limit) {
                            Files.delete(backup.toPath());
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void startBackup() {
        if (this.currentRun == null) {
            this.currentRun = new BackupRun(new File(System.getProperty("user.dir")), new File(System.getProperty("user.dir") + this.config.getParsed().destination), this.config.getParsed().sortMode, this.config.getParsed().filter, this.config.getParsed().compressionLevel, this.config.getParsed().limitType, this.config.getParsed().limit, true);
            this.currentRun.start();

            this.currentRun = null;
        } else {
            throw new RuntimeException("A backup is already running");
        }
    }

    public void stopBackup() {
        if (this.currentRun != null) {
            this.currentRun.stop();
            this.currentRun = null;
        } else {
            throw new RuntimeException("No backup is currently running");
        }
    }

    public BackupRun getCurrentRun() {
        return this.currentRun;
    }
}