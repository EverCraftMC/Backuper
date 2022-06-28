package io.github.evercraftmc.backuper.shared.backuper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import io.github.evercraftmc.backuper.shared.config.Config;

public class Backuper {
    public enum LimitType {
        AMOUNT, SIZE
    }

    private Config config;

    private File source;
    private File dest;

    public Backuper(Config config, String source, String dest) {
        this.config = config;

        this.source = new File(source);
        this.dest = new File(dest);

        if (!this.source.exists()) {
            throw new RuntimeException("File source \"" + this.source.getAbsolutePath() + "\" does not exist");
        }

        if (!this.dest.exists()) {
            throw new RuntimeException("File dest \"" + this.dest.getAbsolutePath() + "\" does not exist");
        }
    }

    public void backup() {
        try {
            FileOutputStream fos = new FileOutputStream(this.dest.getAbsolutePath() + File.separator + "Backup-" + DateTimeFormatter.ofPattern("MM-d-yy-HH-mm-ss").withLocale(Locale.US).withZone(ZoneId.of("-05:00")).format(Instant.now()) + ".zip");
            ZipOutputStream zipOut = new ZipOutputStream(fos);

            backupDir(zipOut, this.source);

            zipOut.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File[] backups = this.dest.listFiles();
        Arrays.sort(backups, Comparator.comparingLong(File::lastModified).reversed());

        Long x = 0l;
        for (Integer i = 0; i < backups.length; i++) {
            if (LimitType.valueOf(config.getString("limitType")) == LimitType.AMOUNT) {
                x++;
            } else if (LimitType.valueOf(config.getString("limitType")) == LimitType.SIZE) {
                x += backups[i].length();
            }

            if (x > config.getInteger("limit")) {
                try {
                    Files.delete(backups[i].toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void backupDir(ZipOutputStream zipOut, File file) {
        for (File cfile : file.listFiles()) {
            String path = cfile.getAbsolutePath().replace(this.source.getAbsolutePath(), "").replace("\\", "/");

            Boolean excluded = false;
            for (String condition : this.config.getStringList("filter")) {
                if (condition.startsWith("!") && (new WildcardFileFilter(condition.substring(1)).accept(cfile) || path.toLowerCase().startsWith(condition.substring(1).replace("\\", "/").toLowerCase()))) {
                    excluded = true;
                }
            }

            if (!excluded) {
                Boolean matched = false;
                for (String condition : this.config.getStringList("filter")) {
                    if (!condition.startsWith("!") && (new WildcardFileFilter(condition).accept(cfile) || path.toLowerCase().startsWith(condition.replace("\\", "/").toLowerCase()))) {
                        matched = true;

                        backupFile(zipOut, cfile);

                        if (cfile.isDirectory()) {
                            backupDir(zipOut, cfile);
                        }

                        break;
                    }
                }

                if (!matched && cfile.isDirectory()) {
                    backupDir(zipOut, cfile);
                }
            }
        }
    }

    private void backupFile(ZipOutputStream zipOut, File file) {
        try {
            if (file.isDirectory()) {
                zipOut.putNextEntry(new ZipEntry(file.getAbsolutePath().replace(this.source.getAbsolutePath() + File.separator, "") + File.separator));
                zipOut.closeEntry();
            } else {
                FileInputStream fis = new FileInputStream(file);
                ZipEntry zipEntry = new ZipEntry(file.getAbsolutePath().replace(this.source.getAbsolutePath() + File.separator, ""));
                zipOut.putNextEntry(zipEntry);

                byte[] bytes = new byte[1024];
                int length;
                while ((length = fis.read(bytes)) >= 0) {
                    zipOut.write(bytes, 0, length);
                }

                fis.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}