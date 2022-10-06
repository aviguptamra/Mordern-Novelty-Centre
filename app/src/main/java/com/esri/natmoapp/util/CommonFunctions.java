package com.esri.natmoapp.util;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;

import com.esri.natmoapp.R;
import com.esri.natmoapp.auth.login.LoginActivity;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.enums.SnackbarType;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


public class CommonFunctions {

    public static final String dataFolderName = "ScanBar_log";
    public static final String dbFolderName = "database";
    public static final String mediaFolderName = "multimedia"; // add "." as
    public static final String parentFolderName = "ScanBar";
    private static CommonFunctions mInstance;
    private static Context mContext;
    private SharedPreferences mMyPreferences;
    Boolean messageShowing = false;

    String appLogFileName = Environment.getExternalStorageState()
            + "/ScanBar/ScanBar.txt";


    String syncDataFormatFile = Environment.getExternalStorageState()
            + "/ScanBar/ScanBar_SyncDataFormat_LOG.txt";
    String syncLogFileName = Environment.getExternalStorageState()
            + "/ScanBar/ScanBar_Sync_LOG.txt";
    String nmeaFileName = Environment.getExternalStorageState()
            + "/ScanBar/ScanBar_NAME_LOG.txt";

    public void createLogfolder(Context cntext) {
        try {
            String extPath = cntext.getExternalFilesDir(null).getAbsolutePath();
            File parentdir = new File(extPath + "/" + parentFolderName);
            File datadir = new File(extPath + "/" + parentFolderName + "/"
                    + dataFolderName);
            File dbdir = new File(extPath + "/" + parentFolderName + "/"
                    + dbFolderName);
            File mediadir = new File(extPath + "/" + parentFolderName + "/"
                    + mediaFolderName);
            if (true) {
                if (!parentdir.exists() || !parentdir.isDirectory()) {
                    parentdir.mkdirs();
                }
                if (!datadir.exists() || !datadir.isDirectory()) {
                    datadir.mkdirs();
                }
                if (!dbdir.exists() || !dbdir.isDirectory()) {
                    dbdir.mkdirs();
                }
                if (!mediadir.exists() || !mediadir.isDirectory()) {
                    mediadir.mkdirs();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/*    public static CommonFunctions getInstance() {
        if (mInstance == null)
            mInstance = new CommonFunctions();
        return mInstance;
    }


    public void Initialize(Context ctxt) {
        mContext = ctxt;
        mMyPreferences = mContext.getSharedPreferences("MASTMobilePref",
                Context.MODE_PRIVATE);
    }*/

   /* public static  CommonFunctions(Context ctxt) {
        mContext = ctxt;
    }*/

    public void loadLocale(Context context) {
        String language = getmMyPreferences().getString("language", "en");
        Locale myLocale = new Locale(language);
        Locale.setDefault(myLocale);
        Configuration config = new Configuration();
        config.locale = myLocale;
        context.getResources().updateConfiguration(config,
                context.getResources().getDisplayMetrics());
    }


    public SharedPreferences getmMyPreferences() {
        if (mMyPreferences == null) {
           /* Initialize(mContext.getApplicationContext());*/
        }
        return mMyPreferences;
    }

    public void appLog(String Tag, Exception e) {

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(new File(
                    appLogFileName), true);
            OutputStreamWriter writer = new OutputStreamWriter(fileOutputStream);
            writer.append("##----------" + Tag + "---------------##" + "\n");
            writer.append(new Date().toString() + "\n");
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String stacktraceString = sw.toString();
            writer.append(stacktraceString);
            writer.append("##----------------------------------------------##");
            writer.close();
            fileOutputStream.close();
        } catch (Exception e1) {
        }
    }

    public void setDownloadServiceStatus(boolean status) {
        String syncPref = "Started";
        SharedPreferences.Editor editor = getmMyPreferences().edit();
        editor.putBoolean(syncPref, status);
        editor.commit();
    }

    public boolean getDownloadServiceStatus() {
        boolean Sync = getmMyPreferences().getBoolean("Started", false);
        return Sync;
    }

    public static String getStringFromInputStream(InputStream is) {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    public  void showMessage(Context cntxt, String header, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(cntxt,R.style.alertdialog);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setTitle(header);
        /*String lang = getLocale();*/
        /*String ok = "Ok";
        if (lang.equalsIgnoreCase("fr")) {
            ok = "D'accord";
        }*/
        alertDialogBuilder.setNegativeButton("Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        alertDialogBuilder.show();
    }

    public String getLocale() {
        String language = getmMyPreferences().getString("language", "en");
        return language;
    }

    public void addErrorMessage(String Tag, String message) {
        try {

            FileWriter fw = new FileWriter(new File(syncLogFileName));
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(Tag + " " + new Date().toString() + " :>>>> " + message);
            bw.append("##----------------------------------------------##");
            bw.close();
        } catch (Exception e1) {
            System.out.println(e1.getMessage());
        }
    }

    public void syncLog(String Tag, Exception e) {
        try {

            PrintWriter pw = new PrintWriter(new File(syncLogFileName));
            e.printStackTrace(pw);
            pw.append("##----------------------------------------------##");
            pw.append(new Date().toString());
            pw.close();
        } catch (Exception e1) {
            System.out.println(e1.getMessage());
        }
    }

    public void ShowCalendar(Activity activity, final EditText editText) {
        Calendar mcurrentDate = Calendar.getInstance();
        int mYear = mcurrentDate.get(Calendar.YEAR);
        int mMonth = mcurrentDate.get(Calendar.MONTH);
        int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog mDatePicker = new DatePickerDialog(activity, R.style.datepicker, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                editText.setText(selectedyear+"-"+(selectedmonth+1) + "-" +selectedday);
            }
        }, mYear, mMonth, mDay);
        mDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
        //mDatePicker.getDatePicker().setSpinnersShown(true);
        mDatePicker.show();
    }

    public void hideSoftKeyBoard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
        try {
            if (imm.isAcceptingText()) {
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

  /*  public void hideSoftKeyBoard(Context activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
        try {
            if (imm.isAcceptingText()) {
                imm.hideSoftInputFromWindow(activity.getContentResolver().getCurrentFocus().getWindowToken(), 0);
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }*/
  /*  }*/

    public String getMonthName(int month) {
        String monthname = "";
        switch (month) {
            case 0:
                monthname = "Jan";
                break;
            case 1:
                monthname = "Feb";
                break;
            case 2:
                monthname = "Mar";
                break;
            case 3:
                monthname = "Apr";
                break;
            case 4:
                monthname = "May";
                break;
            case 5:
                monthname = "Jun";
                break;
            case 6:
                monthname = "Jul";
                break;
            case 7:
                monthname = "Aug";
                break;
            case 8:
                monthname = "Sep";
                break;
            case 9:
                monthname = "Oct";
                break;
            case 10:
                monthname = "Nov";
                break;
            case 11:
                monthname = "Dec";
                break;
        }
        return monthname;
    }


        public static String getDate() {
            String DATE_FORMAT_19 = "dd-MMM-yyyy hh:mm:ss";
            SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_19, Locale.US);
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date today = Calendar.getInstance().getTime();

            String hourOfDay = String.valueOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)); // 24 hour clock
            if(hourOfDay.length()==1)
            {
                hourOfDay="0"+hourOfDay;
            }
            String minute = String.valueOf(Calendar.getInstance().get(Calendar.MINUTE));
            if(minute.length()==1)
            {
                minute="0"+minute;
            }
            String second = String.valueOf(Calendar.getInstance().get(Calendar.SECOND));
            if(second.length()==1)
            {
                second="0"+second;
            }
            String timedate = hourOfDay + ":" + minute + ":" + second;

            Calendar mcurrentDate = Calendar.getInstance();
            int currentyear = mcurrentDate.get(Calendar.YEAR);
            int currntmonth=mcurrentDate.get(Calendar.MONTH);
            int day= mcurrentDate.get(Calendar.DAY_OF_MONTH);
            String datenumber = "";
            if (day < 10) {
                datenumber = datenumber + "0" + day;
            } else {
                datenumber = String.valueOf(day);
            }
            String todaydate=datenumber+"-" + getMonthNameStatic(currntmonth)+"-"+String.valueOf(currentyear);
            String finaldate_today = todaydate + " " + timedate;
            return finaldate_today.toString();
        }

        public static String getMonthNameStatic(int month) {
        String monthname = "";
        switch (month) {
            case 0:
                monthname = "Jan";
                break;
            case 1:
                monthname = "Feb";
                break;
            case 2:
                monthname = "Mar";
                break;
            case 3:
                monthname = "Apr";
                break;
            case 4:
                monthname = "May";
                break;
            case 5:
                monthname = "Jun";
                break;
            case 6:
                monthname = "Jul";
                break;
            case 7:
                monthname = "Aug";
                break;
            case 8:
                monthname = "Sep";
                break;
            case 9:
                monthname = "Oct";
                break;
            case 10:
                monthname = "Nov";
                break;
            case 11:
                monthname = "Dec";
                break;
        }
        return monthname;
    }


    public String getImageNames(HashMap mp) {
        Iterator it = mp.entrySet().iterator();
        String image_names = "";
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry) it.next();
            image_names = image_names+pair.getKey().toString() + ",";
            // avoids a ConcurrentModificationException
        }
        if(image_names!="")
        {
            return image_names.substring(0,image_names.length()-1);
        }
        return null;
    }
    public String getImagepaths(HashMap mp) {
        Iterator it = mp.entrySet().iterator();
        String Imagepaths = "";
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry) it.next();
            Imagepaths = Imagepaths+pair.getValue().toString() + ",";
            // avoids a ConcurrentModificationException
        }
        if(Imagepaths!="")
        {
            return Imagepaths.substring(0,Imagepaths.length()-1);
        }
        return null;
    }

    public static String[] GetStringArray(List<String> arr)
    {
        String str[] = new String[arr.size()];
        for (int j = 0; j < arr.size(); j++) {
            str[j] = arr.get(j);
        }
        return str;
    }

    public void showSnackBar(String msg, boolean indefinite,Activity activity) {

        hideSoftKeyBoard(activity);
        Snackbar snackbar = Snackbar.with(activity)
                .type(SnackbarType.MULTI_LINE);

        if (snackbar.isDismissed()) {
            messageShowing = false;
        }
        if (messageShowing == false) {
            messageShowing = true;
            snackbar.setBackgroundColor(activity.getResources().getColor(
                    R.color.light_grey));
            snackbar.text(msg);
            if (indefinite == true) {
                snackbar.duration(Snackbar.SnackbarDuration.LENGTH_LONG);
            } else {
                snackbar.duration(Snackbar.SnackbarDuration.LENGTH_SHORT);
            }
            snackbar.actionLabel("CLOSE");
            /*Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(activity, R.anim.hyo);
            snackbar.setAnimation(hyperspaceJumpAnimation);*/

            snackbar.setBackgroundColor(activity.getResources().getColor(R.color.light_grey));
            snackbar.actionColor(Color.RED);
            snackbar.show(activity);
        }
    }

    public void showSnackBarCNRM(String msg, boolean indefinite,Context activity) {

      /*  hideSoftKeyBoard(activity);*/
        Snackbar snackbar = Snackbar.with(activity)
                .type(SnackbarType.MULTI_LINE);

        if (snackbar.isDismissed()) {
            messageShowing = false;
        }
        if (messageShowing == false) {
            messageShowing = true;
            snackbar.setBackgroundColor(activity.getResources().getColor(
                    R.color.light_grey));
            snackbar.text(msg);
            if (indefinite == true) {
                snackbar.duration(Snackbar.SnackbarDuration.LENGTH_LONG);
            } else {
                snackbar.duration(Snackbar.SnackbarDuration.LENGTH_SHORT);
            }
            snackbar.actionLabel("CLOSE");
            /*Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(activity, R.anim.hyperspace_jump);
            snackbar.setAnimation(hyperspaceJumpAnimation);*/

            snackbar.setBackgroundColor(activity.getResources().getColor(R.color.light_grey));
            snackbar.actionColor(Color.RED);
            snackbar.show((Activity) activity);
        }
    }

    public String getFullName(String FirstName,String MiddleName,String LastName)
    {
        String FullName = "";
        if(!FirstName.equals("") && FirstName!=null)
        {
            FullName=FullName+FirstName;
        }
        if(!MiddleName.equals("") && MiddleName!=null)
        {
            FullName=FullName+" "+ MiddleName;
        }
        if(!LastName.equals("") && LastName!=null)
        {
            FullName=FullName+" "+LastName;
        }
        return FullName;
    }

    public void buildAlertMessageNoGps(Activity activity,Class<? extends Activity> ResultantActivity) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity,
                R.style.alertdialog);
        alertDialogBuilder.setMessage("Your Location is not set to high accuracy." +
                "Please set it to high accuracy");
        alertDialogBuilder.setTitle("Alert");
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                activity.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
        alertDialogBuilder.show();
    }

    public boolean CheckLocationStatus(Activity activity,Class<? extends Activity> ResultantActivity) {
        boolean status=true;
        final LocationManager manager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        if (!(manager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
                manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) &&
                manager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER))) {
            status=false;
            buildAlertMessageNoGps(activity,ResultantActivity);
        }
        return status;
    }

    public  int ComputeSubModuleSequenceNo(int submoduleId)
    {
        int sequenceno=0;
        switch (submoduleId)
        {
            case 5: sequenceno=1; break;
            case 6: sequenceno=2; break;
            case 32: sequenceno=3; break;
            case 8: sequenceno=4; break;
            case 9: sequenceno=5; break;

            case 33: sequenceno=6; break;
            case 7: sequenceno=7; break;
            case 46: sequenceno=8; break;
            case 10: sequenceno=9; break;
            case 12: sequenceno=10; break;

            case 51: sequenceno=11; break;
            case 31: sequenceno=12; break;
            case 60: sequenceno=13; break;
            case 61: sequenceno=14; break;
            case 62: sequenceno=15; break;
            case 80: sequenceno=16; break;
            case 86: sequenceno=17; break;
        }
        return  sequenceno;
    }

    public String  getMonthNameDigit(String month) {
        String monthdigit = "0";
        switch (month) {
            case "Jan": monthdigit="01";
                break;
            case "Feb": monthdigit="02";
                break;
            case "Mar": monthdigit="03";
                break;
            case "Apr": monthdigit="04";
                break;
            case "May": monthdigit="05";
                break;
            case "Jun": monthdigit="06";
                break;
            case "Jul": monthdigit="07";
                break;
            case "Aug": monthdigit="08";
                break;
            case "Sep": monthdigit="09";
                break;
            case "Oct": monthdigit="10";
                break;
            case "Nov": monthdigit="11";
                break;
            case "Dec": monthdigit="12";
                break;
        }
        return monthdigit;
    }

    public Date getDateInFormat(String date)
    {
        Date date1=null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-mm-yyyy");
            String datearray[] = date.trim().split("-");
            String datestring = datearray[0] + "-" + getMonthNameDigit(datearray[1]) + "-" + datearray[2];
             date1 = simpleDateFormat.parse(datestring);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return date1;
    }

    public static String getCurrentDate() {
        String DATE_FORMAT_19 = "dd-MMM-yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_19, Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date today = Calendar.getInstance().getTime();

        String datetoday = dateFormat.format(today);
        String finaldate1[] = datetoday.split(" ");
        String finaldate_today = finaldate1[0];
        return finaldate_today.toString();
    }

    public boolean validateFloatNumber(String input, Activity activity) {
        boolean valid = true;
        try {
            if (input != null) {
                if (!input.equals("")) {
                    Float.parseFloat(input);
                } else {
                    valid = true;
                }
            } else {
                valid = true;
            }
        } catch (NumberFormatException e) {
            valid = false;
            showSnackBar("Please enter a valid decimal number", true, activity);
        } catch (Exception e) {
            valid = false;
            showSnackBar("Please enter a valid decimal number", true, activity);
        }
        return valid;
    }

    public boolean validateIntegerNumber(String input, Activity activity) {
        boolean valid = true;
        try {
            if (input != null) {
                if (!input.equals("")) {
                    Integer.parseInt(input);
                } else {
                    valid = true;
                }
            } else {
                valid = true;
            }
        } catch (NumberFormatException e) {
            valid = false;
            showSnackBar("Please enter a valid integer number", true, activity);
        } catch (Exception e) {
            valid = false;
            showSnackBar("Please enter a valid integer number", true, activity);
        }
        return valid;
    }

    public boolean validateDoubleNumber(String input, Activity activity) {
        boolean valid = true;
        try {
            if (input != null) {
                if (!input.equals("")) {
                    Double.parseDouble(input);
                } else {
                    valid = true;
                }
            } else {
                valid = true;
            }
        } catch (NumberFormatException e) {
            valid = false;
            showSnackBar("Please enter a valid decimal number", true, activity);
        } catch (Exception e) {
            valid = false;
            showSnackBar("Please enter a valid decimal number", true, activity);
        }
        return valid;
    }

    public  boolean checkInMigrationDateIsLessthanOutMigrationDate(String imMigrationDate,String OutMigrationDate)
    {
        boolean flag=false;
        try {

            String dateaaray[] = imMigrationDate.split("-");
            String dateaaray1[] = OutMigrationDate.split("-");

            String datenumber = "";
            if (Integer.parseInt(dateaaray1[0]) < 10) {
                datenumber = datenumber + "0" + dateaaray1[0];
            } else {
                datenumber = String.valueOf(dateaaray1[0]);
            }
            String date_outmigration = dateaaray1[2] + "-" + getMonthNameDigit(dateaaray1[1]) + "-" + datenumber;
            String datenumber1 = "";
            if (Integer.parseInt(dateaaray[0]) < 10) {
                datenumber1 = datenumber1 + "0" + dateaaray[0];
            } else {
                datenumber1 = dateaaray[0];
            }
            String dateentered_inmigration = dateaaray[2] + "-" + getMonthNameDigit(dateaaray[1]) + "-" + datenumber1;

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date_entered_in = sdf.parse(dateentered_inmigration);
            Date date_entered_out = sdf.parse(date_outmigration);
            if(date_entered_in.compareTo(date_entered_out)<0)
            {
                flag=true;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return flag;
    }

    public boolean isNetworkConnected(Activity activity) {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public void showInternetAlert(Activity activity,int flagfinish)
    {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(activity,R.style.alertdialog);
        alertDialogBuilder.setMessage("Please check your internet connection !!");
        alertDialogBuilder.setTitle("No Internet Connection");
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                if(flagfinish==1) {
                    activity.finish();
                }
            }
        });
        alertDialogBuilder.show();
        alertDialogBuilder.setCancelable(false);
    }

    public String captalizeFirstLetter(String name) {
        String content = "";
        try {
            content = name.substring(0,1).toUpperCase() + name.substring(1).toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }

    public void showInternalError_Msg(Activity activity)
    {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(activity,R.style.alertdialog);
        alertDialogBuilder.setMessage("Some error occurred. Please try to login again !!");
        alertDialogBuilder.setTitle("Alert");
        alertDialogBuilder.setPositiveButton("OK", (dialog, id) ->
                NavigateToLogin(dialog,activity)
        );
        alertDialogBuilder.show();
        alertDialogBuilder.setCancelable(false);
    }

    public void showSessionExpired_Msg(Activity activity)
    {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(activity,R.style.alertdialog);
        alertDialogBuilder.setMessage("Session Expired. Please try to login again !!");
        alertDialogBuilder.setTitle("Alert");
        alertDialogBuilder.setPositiveButton("OK", (dialog, id) ->
               NavigateToLogin(dialog,activity)
        );
        alertDialogBuilder.show();
        alertDialogBuilder.setCancelable(false);
    }

    public void NavigateToLogin(DialogInterface dialog,Activity activity)
    {
        dialog.cancel();
        activity.finish();
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.putExtra("usertype", "Public");
        activity.startActivity(intent);
    }

}