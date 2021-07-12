package com.capinfo.statistics;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.capinfo.appdir.AppDataDirConstant;
import com.capinfo.appdir.AppDataDirManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;

public class FileHelper {


    private static final String TAG = "FileHelper";

    private FileHelper() {
        //Utility classes should not have a public or default constructor.
    }

    public static FileOutputStream createFileOutputStream(String strPath) throws Exception {
        final File file = new File(strPath);
        try {
            return new FileOutputStream(file);
        } catch (Exception e) {
            final File fileParent = file.getParentFile();
            if (!fileParent.exists()) {
                if (fileParent.mkdirs()) {
                    return new FileOutputStream(file);
                }
            }
        }

        return null;
    }

    public static boolean isFileExists(String path) {
        return new File(path).exists();
    }

    public static void checkOrCreateDirectory(String path) {
        File file = new File(path);
        if (!file.exists()) {
            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
        }
    }

    public static void deleteFile(String strPath) {
        File file = new File(strPath);
        file.delete();
    }

    public static void deleteFolder(String strPath) {
        File file = new File(strPath);
        if (file.isDirectory()) {
            File[] fileChilds = file.listFiles();
            if (fileChilds == null) {
                file.delete();
            } else {
                final int nLength = fileChilds.length;
                if (nLength > 0) {
                    for (File fileChild : fileChilds) {
                        if (fileChild.isDirectory()) {
                            deleteFolder(fileChild.getAbsolutePath());
                        } else {
                            fileChild.delete();
                        }
                    }
                    file.delete();
                } else {
                    file.delete();
                }
            }
        } else {
            file.delete();
        }
    }

    public static void saveBitmapToFile(String pathDst, Bitmap bmp) {
        saveBitmapToFile(pathDst, bmp, 80);
    }

    public static void saveBitmapToFile(String pathDst, Bitmap bmp, int quality) {
        try {
            FileOutputStream fos = createFileOutputStream(pathDst);
            if (fos != null) {
                bmp.compress(Bitmap.CompressFormat.JPEG, quality, fos);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 文件重命名
     *
     * @param oldname 原来的文件名, 包括文件目录
     * @param newname 新文件名
     */
    public static File renameFile(String oldname, String newname) {
        if (!oldname.equals(newname)) {//新的文件名和以前文件名不同时,才有必要进行重命名
            File oldfile = new File(oldname);
            File newfile = new File(newname);
            if (!oldfile.exists()) {
                return null;//重命名文件不存在
            }
            if (newfile.exists())//若在该目录下已经有一个文件和新文件名相同，则不允许重命名
                Log.e(TAG,newname + "已经存在！");
            else {
                oldfile.renameTo(newfile);
            }
            return oldfile;
        } else {
            Log.e(TAG,"新文件名和旧文件名相同...");
            return null;
        }
    }

    public static void copyFile(String strPathDst, String strPathSrc) {
        if (strPathDst != null && !strPathDst.equals(strPathSrc)) {
            FileOutputStream fos = null;
            FileInputStream fis = null;
            try {
                fos = createFileOutputStream(strPathDst);
                fis = new FileInputStream(strPathSrc);
                byte[] buf = new byte[1024];
                int nReadBytes = 0;
                while ((nReadBytes = fis.read(buf, 0, buf.length)) != -1) {
                    fos.write(buf, 0, nReadBytes);
                }
                fos.flush();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static String readFileToString(String strFilePath) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(strFilePath), "GBK"));
            final StringBuffer sb = new StringBuffer();
            String strLine = null;
            while ((strLine = br.readLine()) != null) {
                sb.append(strLine);
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static String getFileExt(String fileName, String def) {
        if (fileName == null) {
            return def;
        }
        int pos = fileName.lastIndexOf(".");
        if (pos >= 0) {
            return fileName.substring(pos + 1);
        }
        return def;
    }

    public static String loadAssetFile(Context context, String fileName) {

        InputStream is = null;
        BufferedReader br = null;
        try {
            is = context.getResources().getAssets().open(fileName);
            if (is != null) {
                br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                StringBuffer buf = new StringBuffer();
                String line;
                while (null != (line = br.readLine())) {
                    buf.append(line).append('\n');
                }

                br.close();
                is.close();
                return buf.toString();
            }
        } catch (IOException e) {
            Log.e("FileHelper", "loadFirstPageItem: " + e.getMessage());
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                Log.e("FileHelper", "loadFirstPageItem: " + e.getMessage());
            }
        }
        return null;
    }


    public static final String LOG_FILE_FOLDER = AppDataDirConstant.LOG_FILE_FOLDER;
    public static final String LOG_FILE_NAME = AppDataDirConstant.LOG_FILE_NAME;

    /**
     * 此方法为android程序写入sd文件文件，用到了android-annotation的支持库@
     *
     * @param buffer 写入文件的内容
     */
    public synchronized static void writeLogToSDCard(Context context, final byte[] buffer) {

        writeFileToSDCard(context,buffer, LOG_FILE_FOLDER, LOG_FILE_NAME, true, true);
    }

    /**
     * 此方法为写入sd文件文件，用到了android-annotation的支持库@
     *
     * @param buffer   写入文件的内容
     * @param folder   保存文件的文件夹名称,如log；可为null，默认保存在sd卡根目录
     * @param fileName 文件名称，默认app_log.txt
     * @param append   是否追加写入，true为追加写入，false为重写文件
     * @param autoLine 针对追加模式，true为增加时换行，false为增加时不换行
     */
    public synchronized static void writeFileToSDCard(final Context context, final byte[] buffer, final String folder,
                                                      final String fileName, final boolean append, final boolean autoLine) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String folderPath = "";

                folderPath = AppDataDirManager.getFileDir(context) + File.separator + folder + File.separator;
                if(TextUtils.isEmpty(folderPath)) {
                    boolean sdCardExist = Environment.getExternalStorageState().equals(
                            Environment.MEDIA_MOUNTED);
                    if (sdCardExist) {
                        //TextUtils为android自带的帮助类
                        if (TextUtils.isEmpty(folder)) {
                            //如果folder为空，则直接保存在sd卡的根目录
                            folderPath = Environment.getExternalStorageDirectory()
                                    + File.separator;
                        } else {
                            folderPath = Environment.getExternalStorageDirectory()
                                    + File.separator + folder + File.separator;
                        }
                    } else {
                        return;
                    }
                }

                File fileDir = new File(folderPath);
                if (!fileDir.exists()) {
                    if (!fileDir.mkdirs()) {
                        return;
                    }
                }
                File file;
                //判断文件名是否为空
                if (TextUtils.isEmpty(fileName)) {
                    file = new File(folderPath + LOG_FILE_NAME);
                } else {
                    file = new File(folderPath + fileName);
                }
                RandomAccessFile raf = null;
                FileOutputStream out = null;
                try {
                    if (append) {
                        //如果为追加则在原来的基础上继续写文件
                        raf = new RandomAccessFile(file, "rw");
                        raf.seek(file.length());
                        raf.write(buffer);
                        if (autoLine) {
                            raf.write("\n".getBytes());
                        }
                    } else {
                        //重写文件，覆盖掉原来的数据
                        out = new FileOutputStream(file);
                        out.write(buffer);
                        out.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (raf != null) {
                            raf.close();
                        }
                        if (out != null) {
                            out.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
