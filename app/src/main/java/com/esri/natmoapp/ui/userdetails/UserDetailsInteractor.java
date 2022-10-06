package com.esri.natmoapp.ui.userdetails;

public interface UserDetailsInteractor {

    void setShowProgress();

    void setHideProgress();

    void setProgessDialogMessgae(String message);

    void setServerError();
}
