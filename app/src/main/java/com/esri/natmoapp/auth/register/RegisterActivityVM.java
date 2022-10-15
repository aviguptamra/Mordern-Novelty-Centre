package com.esri.natmoapp.auth.register;

import static android.content.Context.INPUT_METHOD_SERVICE;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.provider.Settings;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;
import com.esri.natmoapp.R;
import com.esri.natmoapp.auth.login.LoginActivity;
import com.esri.natmoapp.auth.otpverification.OtpVerification;
import com.esri.natmoapp.db.CommonDatabaseFunctions;
import com.esri.natmoapp.model.APIErrorResponse;
import com.esri.natmoapp.model.Registration;
import com.esri.natmoapp.server.ServiceGenerator;
import com.esri.natmoapp.util.AESCrypt;
import com.esri.natmoapp.util.CommonFunctions;
import com.esri.natmoapp.util.Constants;
import com.esri.natmoapp.util.SharedPref;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.stfalcon.androidmvvmhelper.mvvm.activities.ActivityViewModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivityVM extends ActivityViewModel<RegisterActivity> {

    RegisterInteractor interactor;
    CommonFunctions commonFunctions = new CommonFunctions();
    private static final String TAG = RegisterActivity.class.getSimpleName();
    List<Integer> genderids = new ArrayList<>();
    List<String> gendernames = new ArrayList<>();
    List<Integer> orgntypeid = new ArrayList<>();
    List<String> orgtype = new ArrayList<>();
    List<Integer> stateids = new ArrayList<>();
    List<String> statenames = new ArrayList<>();
    List<Integer> districtids = new ArrayList<>();
    List<String> districtnames = new ArrayList<>();
    int Spinnerpostiongenderid = -1, Spinnerpostiondistrictid = -1,
            Spinnerpostionstateid = -1, Spinnerpostionorgtypeid;
    public android.app.AlertDialog builder_DialogRegSuccess;
    Button submit;
    ServiceGenerator serviceGenerator;
    CommonDatabaseFunctions commonDatabaseFunctions=new CommonDatabaseFunctions(activity);

    public RegisterActivityVM(RegisterActivity activity, RegisterInteractor interactor) {
        super(activity);
        this.interactor = interactor;
        serviceGenerator = new ServiceGenerator();
        IntializeDropdown();
        IntializeFilters();
        CheckInternetConnection();
    }

    public void CheckInternetConnection() {
        if (!commonFunctions.isNetworkConnected(activity)) {
            commonFunctions.showInternetAlert(activity, 1);
        }
    }
    public void RegisterUser() {
        try {
            String otp=getRandomNumberString().trim();
            SharedPref sharedPref=new SharedPref(activity,"");
            sharedPref.set("otp",otp);
            Registration registration = new Registration();
            registration.setFname(activity.getBinding().firstnameedt.getText().toString());
            registration.setLname(activity.getBinding().lastnameedt.getText().toString());
            registration.setEmail(activity.getBinding().emailedtt.getText().toString().trim());
            registration.setMobile(activity.getBinding().mobilenoedt.getText().toString());
            registration.setPassword(AESCrypt.encrypt(activity.getBinding().pwdedt.getText().toString().trim()));
            registration.setGender(activity.getBinding().gendertxt.getText().toString());
            registration.setDob(activity.getBinding().dobtextedt.getText().toString());
            registration.setOrganizationType(activity.getBinding().orgtypeedt.getText().toString());
            registration.setOrganizationName(activity.getBinding().orgnameedt.getText().toString());
            registration.setAddress(activity.getBinding().Addressedt.getText().toString());
            registration.setState(activity.getBinding().statenamedt.getText().toString());
            registration.setDistrict(activity.getBinding().disnamedt.getText().toString());
            registration.setPin(activity.getBinding().pincodenoedt.getText().toString());
            //registration.setUserType("Public");
            registration.setOtp(otp);
            registration.setLatitude(String.valueOf(activity.latitude));
            registration.setLongitude(String.valueOf(activity.longitude));
            registration.setDeviceId(Settings.Secure.getString(activity.getContentResolver(),
                    Settings.Secure.ANDROID_ID));
            registration.setAppVersion(Constants.APP_VERSION);
            if (commonFunctions.isNetworkConnected(activity)) {
                RegisterUser(registration);
            } else {
                commonFunctions.showMessage(activity, "Alert", "Please check your internet connection !!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getRandomNumberString() {
        // It will generate 6 digit random Number.
        // from 0 to 9999
        Random rnd = new Random();
        int number = rnd.nextInt(9999);
        // this will convert any number sequence into 6 character.
        return String.format("%04d", number);
    }

    public void RegisterUser(Registration registration) {
        activity.progressDialog.setMessage("Communicating from server");
        interactor.setShowProgress();
        //String input = new Gson().toJson(registration);
        Call<Registration> call = serviceGenerator.getService().RegisterUser(registration);
        call.enqueue(new Callback<Registration>() {
            @Override
            public void onResponse(Call<Registration> call, Response<Registration> response) {
                try {
                    interactor.setHideProgress();
                    if (response.code() == 201) {
                        activity.runOnUiThread(() -> logToUser(Constants.IS_NOT_ERROR, Constants.USER_REGISTRATION_MSG));
                        show_RegSuccessDialog(registration);
                    } else if (response.code() == 409) {
                        String response_error = response.errorBody().string();
                        APIErrorResponse apiErrorResponse = new Gson().fromJson(response_error, APIErrorResponse.class);
                        Toast.makeText(activity,apiErrorResponse.getMessage(),Toast.LENGTH_LONG);
                        commonFunctions.showMessage(activity,"Alert",apiErrorResponse.getMessage());
                    } else if (response.code() == 401) {
                        commonFunctions.showSessionExpired_Msg(activity);
                    } else {
                        commonFunctions.showInternalError_Msg(activity);
                    }
                }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            }
            @Override
            public void onFailure(Call<Registration> call, Throwable t) {
                interactor.setHideProgress();
                interactor.setServerError();
                Toast.makeText(activity, "Unable to get response from server",
                        Toast.LENGTH_LONG).show();
            }
        });
    }
    private void logToUser(boolean isError, String message) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
        if (isError) {
            Log.e(TAG, message);
        } else {
            Log.d(TAG, message);
        }
    }

    public void IntializeDropdown() {
        genderids.add(1);
        genderids.add(2);
        genderids.add(3);
        gendernames.add("Male");
        gendernames.add("Female");
        gendernames.add("Other");

        orgntypeid.add(1);
        orgntypeid.add(2);
        orgntypeid.add(3);
        orgntypeid.add(4);
        orgtype.add("Government");
        orgtype.add("Private");
        orgtype.add("Institutions");
        orgtype.add("Others");
        stateids = commonDatabaseFunctions.getNamesandIdsOfMasterTable("State","isActive=1").entrySet().iterator().next().getKey();
        statenames = commonDatabaseFunctions.getNamesandIdsOfMasterTable("State","isActive=1").entrySet().iterator().next().getValue();
    }

    public void showcalendar() {
        commonFunctions.hideSoftKeyBoard(activity);
        commonFunctions.ShowCalendar(activity, activity.getBinding().dobtextedt);
    }

    public void showOrgtypeDropdown() {
        new CommonFunctions().hideSoftKeyBoard(activity);
        android.app.AlertDialog.Builder orgtypes = new AlertDialog.Builder(activity,R.style.alertdialog);
        orgtypes.setTitle("Select Organization Type");
        orgtypes.setItems(CommonFunctions.GetStringArray(orgtype), (dialog, which) -> {
            Spinnerpostionorgtypeid = orgntypeid.get(which);
            activity.getBinding().orgtypeedt.setText(orgtype.get(which));
        });
        orgtypes.create().show();
    }

    public void showGenderDropdown() {
        new CommonFunctions().hideSoftKeyBoard(activity);
        android.app.AlertDialog.Builder gender = new AlertDialog.Builder(activity,R.style.alertdialog);
        gender.setTitle("Select Gender");
        gender.setItems(CommonFunctions.GetStringArray(gendernames), (dialog, which) -> {
            Spinnerpostiongenderid = genderids.get(which);
            activity.getBinding().gendertxt.setText(gendernames.get(which));
        });
        gender.create().show();
    }

    public void showstateDropdown() {
        new CommonFunctions().hideSoftKeyBoard(activity);
        android.app.AlertDialog.Builder state = new AlertDialog.Builder(activity,R.style.alertdialog);
        state.setTitle("Select State");
        state.setItems(CommonFunctions.GetStringArray(statenames), (dialog, which) -> {
            Spinnerpostionstateid = stateids.get(which);
            activity.getBinding().statenamedt.setText(statenames.get(which));
            activity.getBinding().disnamedt.setText("");
            getDistrict(stateids.get(which));
        });
        state.create().show();
    }

    public void getDistrict(int stateid)
    {
        districtids.clear();
        districtnames.clear();
        districtids = commonDatabaseFunctions.getNamesandIdsOfMasterTable("District","IsActive=1 and StateId="+stateid).entrySet().iterator().next().getKey();
        districtnames = commonDatabaseFunctions.getNamesandIdsOfMasterTable("District","IsActive=1 and StateId="+stateid).entrySet().iterator().next().getValue();

    }

    public void showdistrictDropdown() {
        new CommonFunctions().hideSoftKeyBoard(activity);
        android.app.AlertDialog.Builder district = new AlertDialog.Builder(activity,R.style.alertdialog);
        district.setTitle("Select District");
        district.setItems(CommonFunctions.GetStringArray(districtnames), (dialog, which) -> {
            Spinnerpostiondistrictid = districtids.get(which);
            activity.getBinding().disnamedt.setText(districtnames.get(which));
        });
        district.create().show();
    }


    public void show_RegSuccessDialog(Registration registration) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View regsuccess_popup = inflater.inflate(R.layout.dialog_reg_success, null);
        IntializeRegSuccess_ViewPopup(regsuccess_popup,registration);
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        builder.setView(regsuccess_popup);
        builder_DialogRegSuccess = builder.create();
        builder_DialogRegSuccess.show();
        builder_DialogRegSuccess.setCancelable(false);
        builder_DialogRegSuccess.setCanceledOnTouchOutside(false);
    }

    private void IntializeRegSuccess_ViewPopup(View regsuccess_popup,Registration registration) {

        submit = (Button) regsuccess_popup.findViewById(R.id.reg_ack);
        submit.setOnClickListener(v -> {
            builder_DialogRegSuccess.cancel();
            Intent intent=new Intent(activity, OtpVerification.class);
            intent.putExtra("email",registration.getEmail());
            intent.putExtra("otp",registration.getOtp());
            intent.putExtra("isReset",0);
            activity.startActivity(intent);
        });
    }


    public void showRegistrationAlert() {
        if (commonFunctions.isNetworkConnected(activity)) {
            if (validateRegistrationform()) {
                showRegistrationAlertpopup();
            }
        } else {
            commonFunctions.showInternetAlert(activity, 1);
        }
    }

    private boolean validateRegistrationform() {
        boolean valid = true;
        activity.getBinding().pwdvalidMsg.setVisibility(View.GONE);
        if (activity.getBinding().firstnameedt.getText().toString().equals("")) {
            valid = false;
            activity.getBinding().scrollbarReg.scrollTo(0, activity.getBinding().firstnameedt.getBottom());
            activity.getBinding().firstnameedt.requestFocus();
            showsoftKeyBoard(activity);
            new CommonFunctions().showSnackBar("Please enter first name !!", false, activity);
        } else if (activity.getBinding().lastnameedt.getText().toString().equals("")) {
            valid = false;
            activity.getBinding().scrollbarReg.scrollTo(0, activity.getBinding().lastnameedt.getBottom());
            activity.getBinding().lastnameedt.requestFocus();
            showsoftKeyBoard(activity);
            new CommonFunctions().showSnackBar("Please enter last name !!", false, activity);
        } else if (activity.getBinding().emailedtt.getText().toString().equals("")) {
            valid = false;
            activity.getBinding().scrollbarReg.scrollTo(0, activity.getBinding().emailedtt.getBottom());
            activity.getBinding().emailedtt.requestFocus();
            showsoftKeyBoard(activity);
            new CommonFunctions().showSnackBar("Please enter email !!", false, activity);
        } else if (!isValidEmail(activity.getBinding().emailedtt.getText().toString())) {
            valid = false;
            activity.getBinding().scrollbarReg.scrollTo(0, activity.getBinding().emailedtt.getBottom());
            activity.getBinding().emailedtt.requestFocus();
            showsoftKeyBoard(activity);
            new CommonFunctions().showSnackBar("Please enter valid email !!", false, activity);
        } else if (activity.getBinding().mobilenoedt.getText().toString().equals("")) {
            valid = false;
            activity.getBinding().scrollbarReg.scrollTo(0, activity.getBinding().mobilenoedt.getBottom());
            activity.getBinding().mobilenoedt.requestFocus();
            showsoftKeyBoard(activity);
            new CommonFunctions().showSnackBar("Please enter mobile no !!", false, activity);
        } else if (!(activity.getBinding().mobilenoedt.getText().toString().length() == 10)) {
            valid = false;
            activity.getBinding().scrollbarReg.scrollTo(0, activity.getBinding().mobilenoedt.getBottom());
            activity.getBinding().mobilenoedt.requestFocus();
            showsoftKeyBoard(activity);
            new CommonFunctions().showSnackBar("Mobile number should be 10 digit !!", false, activity);
            //new CommonFunctions().showSnackBar("Mobile number should be 10 digit !!", false, activity);
        } else if (!(activity.getBinding().mobilenoedt.getText().toString().startsWith("6") ||
                activity.getBinding().mobilenoedt.getText().toString().startsWith("7") ||
                activity.getBinding().mobilenoedt.getText().toString().startsWith("8") ||
                activity.getBinding().mobilenoedt.getText().toString().startsWith("9"))) {
            valid = false;
            activity.getBinding().scrollbarReg.scrollTo(0, activity.getBinding().mobilenoedt.getBottom());
            activity.getBinding().mobilenoedt.requestFocus();
            showsoftKeyBoard(activity);
            new CommonFunctions().showSnackBar("Please enter valid mobile no !!", false, activity);
            // new CommonFunctions().showSnackBar("Please enter valid mobile no !!", false, activity);
        } else if (activity.getBinding().pwdedt.getText().toString().equals("")) {
            valid = false;
            activity.getBinding().scrollbarReg.scrollTo(0, activity.getBinding().pwdedt.getBottom());
            activity.getBinding().pwdedt.requestFocus();
            showsoftKeyBoard(activity);
            new CommonFunctions().showSnackBar("Please enter password !!", false, activity);
        }
        else if (activity.getBinding().pwdedt.getText().toString().trim().length()<8) {
            valid = false;
            activity.getBinding().scrollbarReg.scrollTo(0, activity.getBinding().pwdedt.getBottom());
            activity.getBinding().pwdedt.requestFocus();
            showsoftKeyBoard(activity);
            new CommonFunctions().showSnackBar("Password should be at least 8 characters !!", false, activity);
        }
        else if (!isValidPassword(activity.getBinding().pwdedt.getText().toString())) {
            valid = false;
            activity.getBinding().scrollbarReg.scrollTo(0, activity.getBinding().pwdedt.getBottom());
            activity.getBinding().pwdedt.requestFocus();
            showsoftKeyBoard(activity);
            activity.getBinding().pwdvalidMsg.setVisibility(View.VISIBLE);
            new CommonFunctions().showSnackBar("Please enter a  valid password !!", false, activity);
        } else if (activity.getBinding().conpwdedt.getText().toString().equals("")) {
            valid = false;
            activity.getBinding().scrollbarReg.scrollTo(0, activity.getBinding().conpwdedt.getBottom());
            activity.getBinding().conpwdedt.requestFocus();
            showsoftKeyBoard(activity);
            new CommonFunctions().showSnackBar("Please enter confirm password !!", false, activity);
        } else if (!(activity.getBinding().pwdedt.getText().toString().equals(activity.getBinding().conpwdedt.getText().toString()))) {
            valid = false;
            activity.getBinding().scrollbarReg.scrollTo(0, activity.getBinding().pwdedt.getBottom());
            activity.getBinding().pwdedt.requestFocus();
            showsoftKeyBoard(activity);
            new CommonFunctions().showSnackBar("password and confirm password does not match !!", false, activity);
        } else if (activity.getBinding().gendertxt.getText().toString().equals("")) {
            valid = false;
            activity.getBinding().gendertxt.requestFocus();
            new CommonFunctions().showSnackBar("Please select gender !!", false, activity);
        } else if (activity.getBinding().dobtextedt.getText().toString().equals("")) {
            valid = false;
            activity.getBinding().dobtextedt.requestFocus();
            new CommonFunctions().showSnackBar("Please select date of birth !!", false, activity);
        } else if (activity.getBinding().orgtypeedt.getText().toString().equals("")) {
            valid = false;
            activity.getBinding().orgtypeedt.requestFocus();
            new CommonFunctions().showSnackBar("Please select organization type !!", false, activity);
        } else if (activity.getBinding().orgnameedt.getText().toString().equals("")) {
            valid = false;
            activity.getBinding().orgnameedt.requestFocus();
            new CommonFunctions().showSnackBar("Please enter organization name !!", false, activity);
        } else if (activity.getBinding().Addressedt.getText().toString().equals("")) {
            valid = false;
            activity.getBinding().Addressedt.requestFocus();
            new CommonFunctions().showSnackBar("Please enter address !!", false, activity);
        } else if (activity.getBinding().statenamedt.getText().toString().equals("")) {
            valid = false;
            activity.getBinding().statenamedt.requestFocus();
            new CommonFunctions().showSnackBar("Please select state !!", false, activity);
        } else if (activity.getBinding().disnamedt.getText().toString().equals("")) {
            valid = false;
            activity.getBinding().disnamedt.requestFocus();
            new CommonFunctions().showSnackBar("Please select disnamedt !!", false, activity);
        } else if (activity.getBinding().pincodenoedt.getText().toString().equals("")) {
            valid = false;
            activity.getBinding().pincodenoedt.requestFocus();
            new CommonFunctions().showSnackBar("Please enter pincode !!", false, activity);
        }
        return valid;
    }

    public void IntializeFilters() {
        activity.getBinding().firstnameedt.setFilters(new InputFilter[]{ignoreFirstWhiteSpace("name", activity.getBinding().firstnameedt)});
        activity.getBinding().lastnameedt.setFilters(new InputFilter[]{ignoreFirstWhiteSpace("name", activity.getBinding().lastnameedt)});
        activity.getBinding().emailedtt.setFilters(new InputFilter[]{ignoreFirstWhiteSpace("name", activity.getBinding().emailedtt)});
        activity.getBinding().mobilenoedt.setFilters(new InputFilter[]{ignoreFirstWhiteSpace("mobno", activity.getBinding().mobilenoedt)});
        activity.getBinding().pwdedt.setFilters(new InputFilter[]{ignoreFirstWhiteSpace("pwd", activity.getBinding().pwdedt)});
        activity.getBinding().conpwdedt.setFilters(new InputFilter[]{ignoreFirstWhiteSpace("pwd", activity.getBinding().conpwdedt)});
        activity.getBinding().orgnameedt.setFilters(new InputFilter[]{ignoreFirstWhiteSpace("name", activity.getBinding().orgnameedt)});
        activity.getBinding().Addressedt.setFilters(new InputFilter[]{ignoreFirstWhiteSpace("address", activity.getBinding().Addressedt)});
        activity.getBinding().pincodenoedt.setFilters(new InputFilter[]{ignoreFirstWhiteSpace("pincode", activity.getBinding().pincodenoedt)});
    }
    public InputFilter ignoreFirstWhiteSpace(String text, TextInputEditText textInputEditText) {
        return (source, start, end, dest, dstart, dend) -> {

            for (int i = start; i < end; i++) {
                if (Character.isWhitespace(source.charAt(i))) {
                    if (dstart == 0)
                        if (textInputEditText.getText().toString().trim().equals("")) {
                            return "";
                        }
                }
            }
            if (text.equals("name")) {
                if (dend > 99) {
                    return "";
                } else if (textInputEditText.getText().toString().trim().length() == 100) {
                    return "";
                }
            }

            if (text.equals("pincode")) {
                if (dend > 5) {
                    return "";
                } else if (textInputEditText.getText().toString().trim().length() == 6) {
                    return "";
                }
            }

            if (text.equals("mobno")) {
                if (dend > 9) {
                    return "";
                } else if (textInputEditText.getText().toString().trim().length() == 10) {
                    return "";
                }
            }

            if (text.equals("pwd")) {
                if (dend > 14) {
                    return "";
                } else if (textInputEditText.getText().toString().trim().length() == 16) {
                    return "";
                }
            }

            if (text.equals("address")) {
                if (dend > 253) {
                    return "";
                } else if (textInputEditText.getText().toString().trim().length() == 254) {
                    return "";
                }
            }
            return null;
        };
    }

    public boolean isValidPassword(final String password) {
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public void showsoftKeyBoard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText()) {
            imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

  /*  ^                 # start-of-string
            (?=.*[0-9])       # a digit must occur at least once
            (?=.*[a-z])       # a lower case letter must occur at least once
            (?=.*[A-Z])       # an upper case letter must occur at least once
            (?=.*[@#$%^&+=])  # a special character must occur at least once you can replace with your special characters
            (?=\\S+$)          # no whitespace allowed in the entire string
            .{4,}             # anything, at least six places though
    $                 # end-of-string*/



    public void showRegistrationAlertpopup() {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(activity,R.style.alertdialog);
        alertDialogBuilder.setMessage("Are you sure want to register this user !!");
        alertDialogBuilder.setTitle("Alert");
        alertDialogBuilder.setPositiveButton("OK", (dialog, id) -> RegisterUser());
        alertDialogBuilder.setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());
        alertDialogBuilder.show();
    }


}