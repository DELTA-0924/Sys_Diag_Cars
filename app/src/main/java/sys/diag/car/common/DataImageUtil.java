package sys.diag.car.common;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import java.net.URL;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public  class DataImageUtil {
    public final static String NO_IMAGE = "placeholder";
    public static String copyImageToInternalStorage(Uri uri, Context context,String imageFileName) {



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
    public static String DownloadImageAndSaveLocal(File filesDir,String imageUrl){
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection =(HttpURLConnection)url.openConnection();
            connection.connect();
            InputStream input = connection.getInputStream();
            String[] args = imageUrl.split("/");

            String fileName = args[args.length - 1];
            Log.w("Image_Name",fileName);
            File file =  new File(filesDir,fileName);
            FileOutputStream writer = new FileOutputStream(file);
            byte[] buffer =new byte[4096];
            int bytesRead;
            while((bytesRead = input.read(buffer))!=-1){
                writer.write(buffer,0,bytesRead);
            }
            writer.close();
            input.close();
            return file.getAbsolutePath();
        }catch(IOException e){
            e.printStackTrace();
            return null;
        }

    }

    public static List<MultipartBody.Part> getAllJpgImagesFromInternalStorage(File internalDir) {
        List<MultipartBody.Part> images = new ArrayList<>();

        File[] files = internalDir.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().toLowerCase().endsWith(".jpg")) {
                    RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file);
                    MultipartBody.Part part = MultipartBody.Part.createFormData("images", file.getName(), requestFile);
                    images.add(part);
                }
            }
        }

        return images;
    }


    public static void deleteAllSavedImages(Context context) {
        File dir = context.getFilesDir();
        File[] files = dir.listFiles();

        if (files != null) {
            for (File file : files) {

                    file.delete();

            }
        }
    }
    public static void deleteImagesByName(Context context,String filename) {
        File dir = context.getFilesDir();
        File[] files = dir.listFiles();

        if (files != null) {
            for (File file : files) {
                if(file.getName().toLowerCase().equals(filename))
                    file.delete();
            }
        }
    }
}
