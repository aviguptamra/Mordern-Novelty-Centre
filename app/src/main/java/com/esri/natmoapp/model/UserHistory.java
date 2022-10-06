package com.esri.natmoapp.model;

import java.io.Serializable;
import java.util.List;

public class UserHistory implements Serializable {

    private List<UserHistoryModel> redeemResponses;

    private List<ProductDetails> productScannedList;


    public List<ProductDetails> getProductScannedList() {
        return productScannedList;
    }

    public void setProductScannedList(List<ProductDetails> productScannedList) {
        this.productScannedList = productScannedList;
    }

    public List<UserHistoryModel> getRedeemResponses() {
        return redeemResponses;
    }

    public void setRedeemResponses(List<UserHistoryModel> redeemResponses) {
        this.redeemResponses = redeemResponses;
    }
}