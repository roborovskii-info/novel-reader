package info.bunny178.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * @author ISHIMARU Sohei on 2015/09/18.
 */

public class ZipUtils {
    private static final int EOF = -1;
    private ZipFile zip;
    private byte[] buf;
    private String path = null;

    private ZipUtils() {
        buf = new byte[8092];
    }

    public static List<String> getList(File f) {
        ZipUtils z = new ZipUtils();
        return z.listZip(f);
    }

    public static boolean decompressZip(File dir, File zip) {
        ZipUtils z = new ZipUtils();
        z.path = dir.getAbsolutePath();
        return z.meltZip(zip);
    }

    public static void gzipDeCompress(String from, String to)
            throws IOException {
        byte[] buf = new byte[1024];
        GZIPInputStream in = new GZIPInputStream(new FileInputStream(from));
        BufferedOutputStream out = new BufferedOutputStream(
                new FileOutputStream(to));
        int size;
        while ((size = in.read(buf, 0, buf.length)) != -1) {
            out.write(buf, 0, size);
        }
        out.flush();
        out.close();
        in.close();
    }

    public static boolean compressZip(File zip, File basepath, File[] files) {
        ZipUtils z = new ZipUtils();
        z.path = basepath.getAbsolutePath() + File.separator;
        return z.freezeZip(zip, files);
    }

    private boolean freezeZip(File zip, File[] entry) {
        try {
            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zip));
            for (File file : entry) {
                freezeFile(zos, file);
            }
            zos.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void freezeFile(ZipOutputStream zos, File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                freezeFile(zos, f);
            }
        } else {
            System.out.println("Compressing file : " + file.getAbsolutePath());
            addEntryFile(zos, file);
        }
    }

    private long addEntryFile(ZipOutputStream zos, File file) {
        CRC32 crc = new CRC32();
        try {
            BufferedInputStream bis = new BufferedInputStream(
                    new FileInputStream(file));
            String tmp = file.getAbsolutePath();
            tmp = tmp.replace(path, "");
            ZipEntry entry = new ZipEntry(tmp);
            int size;
            byte[] bb = getFileBytes(file);
            crc.update(bb, 0, (int) file.length());
            entry.setSize(file.length());
            entry.setCrc(crc.getValue());
            zos.putNextEntry(entry);
            while ((size = bis.read(buf, 0, 1024)) != EOF) {
                zos.write(buf, 0, size);
            }
            bis.close();
            zos.closeEntry();
            return entry.getCompressedSize();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private static byte[] getFileBytes(File file) throws IOException {
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        try {
            int len = (int) file.length();
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis, len);
            byte buf[] = new byte[len];
            bis.read(buf, 0, len);
            return buf;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                bis.close();
                bis = null;
            }
        }
        return null;
    }

    private boolean meltZip(File f) {
        try {
            zip = new ZipFile(f.getAbsolutePath());
            Enumeration<? extends ZipEntry> enu = zip.entries();
            while (enu.hasMoreElements()) {
                extractFile(enu.nextElement());
            }
            zip.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private List<String> listZip(File f) {
        try {
            zip = new ZipFile(f.getAbsolutePath());
            Enumeration<? extends ZipEntry> enu = zip.entries();
            List<String> ret = new ArrayList<String>();
            while (enu.hasMoreElements()) {
                ret.addAll(listFile((ZipEntry) enu.nextElement()));
            }
            return ret;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void extractFile(ZipEntry entry) throws IOException {
        String name = entry.getName();
        if (name.endsWith("/")) {
            name = path + File.separator + name;
            File dir = new File(name);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        } else {
            if (name.contains("/")) {
                String[] dir = name.split("/");
                String tmp = path + File.separator;
                for (int i = 0; i < dir.length - 1; i++) {
                    File d = new File(tmp + dir[i]);
                    if (!d.exists())
                        d.mkdir();
                    tmp = tmp + dir[i] + File.separator;
                }
                name = path + File.separator + name;
                write(name, entry);
            } else if (name.contains("\\")) {
                String[] dir = name.split("\\\\");
                String tmp = path + File.separator;
                for (int i = 0; i < dir.length - 1; i++) {
                    File d = new File(tmp + dir[i]);
                    if (!d.exists())
                        d.mkdir();
                    tmp = tmp + dir[i] + File.separator;
                }
                name = path + File.separator + name;
                write(name, entry);
            } else {
                name = path + File.separator + name;
                write(name, entry);
            }
        }
    }

    private void write(String name, ZipEntry entry) throws IOException {
        FileOutputStream fs = new FileOutputStream(name);
        InputStream is = zip.getInputStream(entry);
        File ff = new File(name);
        System.out.println(ff.getAbsolutePath());
        int n;
        while ((n = is.read(buf)) > 0) {
            fs.write(buf, 0, n);
        }
        is.close();
        fs.close();
    }

    private List<String> listFile(ZipEntry entry) throws IOException {
        List<String> ret = new ArrayList<>();
        String name = entry.getName();
        if (entry.isDirectory()) {
            ret.add("Dir :" + name);
        } else {
            ret.add("File:" + name);
        }
        return ret;
    }
}
