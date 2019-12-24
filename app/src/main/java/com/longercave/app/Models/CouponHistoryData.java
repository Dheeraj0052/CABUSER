package com.longercave.app.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Esack N on 10/30/2017.
 */

public class CouponHistoryData {

    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("user_id")
    @Expose
    public Integer userId;
    @SerializedName("promocode_id")
    @Expose
    public Integer promocodeId;
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("created_at")
    @Expose
    public String createdAt;

    @SerializedName("promocode_id")
    @Expose
    public Integer promocode_id;

    @SerializedName("promo_code")
    @Expose
    public String promoCode;
    @SerializedName("discount")
    @Expose
    public Integer discount;
    @SerializedName("discount_type")
    @Expose
    public String discountType;
    @SerializedName("expiration")
    @Expose
    public String expiration;
    @SerializedName("promocode_status")
    @Expose
    public String promocode_status;
    @SerializedName("deleted_at")
    @Expose
    public String deletedAt;

}
