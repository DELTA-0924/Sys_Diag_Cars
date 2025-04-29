package sys.diag.car.common;

import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public  class DataImageUtil {
    public static String copyImageToInternalStorage(Uri uri, Context context) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "image_" + timeStamp + ".jpg";

        try (InputStream inputStream = context.getContentResolver().openInputStream(uri)) {
            if (inputStream == null) return null;

            File internalFile = new File(context.getFilesDir(), imageFileName);
            try (FileOutputStream outputStream = new FileOutputStream(internalFile)) {
                byte[] buffer = new byte[1024];
                int read;
                while ((read = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, read);
                }
                return internalFile.getAbsolutePath();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static void deleteAllSavedImages(Context context) {
        File dir = context.getFilesDir();
        File[] files = dir.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.getName().startsWith("image_") && file.getName().endsWith(".jpg")) {
                    file.delete();
                }
            }
        }
    }
}
