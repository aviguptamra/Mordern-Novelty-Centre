package com.esri.natmoapp.util;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Base64;

import androidx.annotation.RequiresApi;



import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class FileCommonFunctions {

    public static String getMultimediaDirectory(Context context) {
        String extPath = context.getExternalFilesDir(null).getAbsolutePath() +
                File.separator + CommonFunctions.parentFolderName + File.separator + CommonFunctions.mediaFolderName;
        return extPath;
    }

    public static void copyFile(File source, File destination) throws IOException {
        try {
            if (source.exists()) {
                FileChannel src = new FileInputStream(source).getChannel();
                FileChannel dst = new FileOutputStream(destination).getChannel();
                dst.transferFrom(src, 0, src.size());       // copy the first file to second.....
                src.close();
                dst.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getImageNameFromUri(Uri mImageUri) {
        String lastPathSegment = mImageUri.getLastPathSegment();
        String splitstring[] = lastPathSegment.split("/");
        String image_Name = splitstring[splitstring.length-1];
        return image_Name;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String getPath(Uri uri, Activity activity) throws NumberFormatException {
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        //        // deal with different Uris.
        if (DocumentsContract.isDocumentUri(activity.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);

                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{
                        split[1]
                };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {
                    MediaStore.Images.Media.DATA
            };
            Cursor cursor = null;
            try {
                cursor = activity.getContentResolver()
                        .query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public byte[] resizeImage(byte[] input) {
        Bitmap original = BitmapFactory.decodeByteArray(input, 0, input.length);

        int img_height = (original.getHeight()) / 4;
        int img_width = (original.getWidth()) / 4;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.ENGLISH);

        Bitmap resized = Bitmap.createScaledBitmap(original, img_width, img_height, true);
        Bitmap bitmap = rotate(resized, 90);
        Canvas canvas = new Canvas(bitmap); //bmp is the bitmap to dwaw into
        String date_string = "Date: " + sdf.format(new Date());
        String latitude_val = " Lat: " + 2344556.66;
        String longitude_val = " Long: " + 575454.78;
        Paint paint = new Paint();
        paint.setColor(Color.YELLOW);
        paint.setTextSize(22);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(date_string, 70, 400, paint);
        canvas.drawText(latitude_val, 80, 425, paint);
        canvas.drawText(longitude_val, 80, 450, paint);
        ByteArrayOutputStream blob = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, blob);
        return blob.toByteArray();
    }

    public static Bitmap rotate(Bitmap bitmap, int degree) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix mtx = new Matrix();
        mtx.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public String getQueryInsert(String tableName,Context context) {
        String query="";
        try {
            AssetManager am = context.getAssets();
            InputStream in = am.open(tableName);
            if (in != null) {
                // prepare the file for reading
                InputStreamReader input = new InputStreamReader(in);
                BufferedReader br = new BufferedReader(input);
                String line = br.readLine();
                query=line+" ";
                while ((line=br.readLine()) != null) {
                    query=query+line;
                }
                in.close();
            }else{
                System.out.println("It's the assests");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return query;
    }

    public byte[] encodeImageToBytes(String path) {
        File imagefile = new File(path);
        String encImage = "";
        byte[] b = null;
        FileInputStream fis = null;
        try {
            if (path != "") {
                fis = new FileInputStream(imagefile);
                Bitmap bm = BitmapFactory.decodeStream(fis);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                b = baos.toByteArray();
                //encImage = Base64.encodeToString(b, Base64.DEFAULT);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }


    private String encodeImage(String path)
    {
        File imagefile = new File(path);
        String encImage="";
        FileInputStream fis = null;
        try{
            if(path!="") {
                fis = new FileInputStream(imagefile);
                Bitmap bm = BitmapFactory.decodeStream(fis);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] b = baos.toByteArray();
                encImage = Base64.encodeToString(b, Base64.DEFAULT);
            }
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return encImage;
    }

/*    public String getDocumets(HashMap<String,String>imgpathlist)
    {
        String Documentlists=null;
        List<Document> documetList=new ArrayList<Document>();
        for (HashMap.Entry<String, String> entry : imgpathlist.entrySet())
        {
            Document documentdetail= new Document();
            String image_Name="Image_"+String.valueOf(System.currentTimeMillis()) + ".jpg";
            //documentdetail.setDocumentName(entry.getKey());
            documentdetail.setDocumentName(image_Name);
            documentdetail.setDocumentFile(encodeImage(entry.getValue()));
            documetList.add(documentdetail);
        }
        Documentlists=serializelistData(documetList);
        return Documentlists;
    }

    public String  serializeActualAWPBtoJSON(List<Document> DocumentList)
    {
        String Documentlists=null;
        try
        {
            Gson gson = new Gson();
            Documentlists = gson.toJson(DocumentList);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return Documentlists;
    }

    public String  serializelistData(List  ListData)
    {
        String serializedListData=null;
        try
        {
            Gson gson = new Gson();
            serializedListData = gson.toJson(ListData);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return serializedListData;
    }*/

   /* public  String getDocumentsString(String imageNames, String paths)
    {
        HashMap<String,String> imagepathList=new HashMap<>();
        String imgnames[]=imageNames.split(",");
        String imgPaths[]=paths.split(",");
        for(int i=0;i<=imgnames.length-1;i++)
        {
            imagepathList.put(imgnames[i],imgPaths[i]);
        }
        String documents=new FileCommonFunctions().getDocumets(imagepathList);
        return documents;
    }*/




}