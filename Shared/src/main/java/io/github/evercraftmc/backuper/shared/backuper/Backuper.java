package io.github.evercraftmc.backuper.shared.backuper;

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

        private List<String> filter;

        private LimitType limitType;
        private Integer limit;

        private Boolean trackStats;

        private List<File> files = new ArrayList<File>();

        private Boolean stopped = false;

        private Integer total = 0;
        private Long totalBytes = 0l;

        private Integer finished = 0;
        private Long finishedBytes = 0l;

        public BackupRun(File source, File destination, List<String> filter, LimitType limitType, Integer limit, Boolean trackStats) {
            this.source = source;
            this.destination = destination;

            this.filter = filter;

            this.limitType = limitType;
            this.limit = limit;

            this.trackStats = trackStats;
        }

        public void run() {
            try {
                this.files = this.filterFiles(this.getFiles(this.source, true));

                if (this.trackStats) {
                    for (File file : this.files) {
                        total++;
                        totalBytes += file.length();
                    }
                }

                ZipOutputStream zip = this.createZip();

                for (File file : this.files) {
                    if (!this.stopped) {
                        this.backupFile(zip, file);

                        if (this.trackStats) {
                            this.finished++;
                            this.finishedBytes += file.length();
                        }
                    } else {
                        zip.close();

                        return;
                    }
                }

                zip.close();

                if (this.limit != -1) {
                    this.cullOld(this.limitType, this.limit);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void stop() {
            this.stopped = true;
        }

        public Integer getFinished() {
            return this.finished;
        }

        public Float getFinishedPercent() {
            return ((float) this.total) / ((float) this.finished);
        }

        public Long getFinishedBytes() {
            return this.finishedBytes;
        }

        public Float getFinishedBytesPercent() {
            return (float) (this.totalBytes / this.finishedBytes);
        }

        private ZipOutputStream createZip() {
            try {
                File file = new File(this.destination.getAbsolutePath() + File.separator + "Backup-" + DateTimeFormatter.ofPattern("MM-d-yy-HH-mm-ss").withLocale(Locale.US).withZone(ZoneId.of("-05:00")).format(Instant.now()) + ".zip");
                if (!file.exists()) {
                    file.createNewFile();
                }
                FileOutputStream fileStream = new FileOutputStream(file);
                ZipOutputStream zipSteam = new ZipOutputStream(fileStream);

                return zipSteam;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        private List<File> getFiles(File directory, Boolean deep) {
            List<File> files = new ArrayList<File>();

            for (File file : directory.listFiles()) {
                if (file.isDirectory()) {
                    if (deep) {
                        files.addAll(this.getFiles(file, true));
                    } else {
                        files.add(file);
                    }
                } else {
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

        private void backupFile(ZipOutputStream zip, File file) {
            try {
                FileInputStream fileStream = new FileInputStream(file);
                zip.putNextEntry(new ZipEntry(file.getAbsolutePath().replace(this.source.getAbsolutePath() + File.separator, "")));

                byte[] bytes = new byte[2048];
                int length;
                while ((length = fileStream.read(bytes)) >= 0) {
                    zip.write(bytes, 0, length);
                }

                fileStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void cullOld(LimitType limitType, Integer limit) {
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
            this.currentRun = new BackupRun(new File(System.getProperty("user.dir")), new File(System.getProperty("user.dir") + this.config.getParsed().destination), this.config.getParsed().filter, this.config.getParsed().limitType, this.config.getParsed().limit, true);
            this.currentRun.run();

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