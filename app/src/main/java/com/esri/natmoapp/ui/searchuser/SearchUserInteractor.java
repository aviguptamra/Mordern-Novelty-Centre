package com.esri.natmoapp.ui.searchuser;

public interface SearchUserInteractor {

    void setShowProgress();

    void setHideProgress();

    void setProgessDialogMessgae(String message);

    void setServerError();

}
