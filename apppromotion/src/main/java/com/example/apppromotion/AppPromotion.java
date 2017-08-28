package com.example.apppromotion;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Himal on 8/24/2017.
 */

public class AppPromotion {

    private static final String BASE_URL = "http://192.168.1.11/promoters/apis/get_promotion";
    private static final String TAG = "AppPromotion";
    private static AsyncHttpClient client = new AsyncHttpClient();
    private Activity mActivity;

    private String appName, appDesc, appLogo, appPackageName;
    private int popupInterval;
    private Handler h = new Handler();
    private Runnable runnable;

    public AppPromotion(Activity mActivity, String packageName, int popupInterval) {
        this.mActivity = mActivity;
        this.popupInterval = popupInterval;

        startAppPromotion(packageName);
    }

    private void startAppPromotion(final String packageName) {
        h.postDelayed(new Runnable() {
            public void run() {
                //do something
                Log.d(TAG, "---------------- called at every 30 sec ----------- ");
                callApiForPromotion(packageName);

                runnable = this;
                h.postDelayed(runnable, popupInterval * 30 * 1000);
            }
        }, popupInterval * 30 * 1000);

    }

    private void callApiForPromotion(String packageName) {
        RequestParams rp = new RequestParams();
        rp.add("build_id", packageName);

        postMethod(BASE_URL, rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                Log.d(TAG, "---------------- this is response : " + response);
                try {
                    JSONObject mainObj = new JSONObject(response.toString());

                    if (mainObj.getInt("error") == 0) {
                        Log.d(TAG, "---------------- error 0 --------");
                        JSONObject promotionObj = mainObj.getJSONObject("promote_app");
                        if (promotionObj.length() != 0) {
                            appName = promotionObj.getString("title");
                            appDesc = promotionObj.getString("desc");
                            appLogo = promotionObj.getString("image_path");
                            appPackageName = promotionObj.getString("build_id");
                            showUserContactPopup();
                        }
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                // Pull out the first event on the public timeline
            }
        });
    }

    public static void postMethod(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(url, params, responseHandler);
    }

    private void showUserContactPopup() {
        final Dialog dialog = new Dialog(mActivity);
        dialog.setContentView(R.layout.popup_app_promotion);

        ImageView ivAppLogo = (ImageView) dialog.findViewById(R.id.ivAppLogo);
        TextView tvAppName = (TextView) dialog.findViewById(R.id.tvAppName);
        TextView tvAppDesc = (TextView) dialog.findViewById(R.id.tvAppDesc);
        Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
        Button btnPromotion = (Button) dialog.findViewById(R.id.btnPromotion);

        tvAppName.setText(appName);
        tvAppDesc.setText(appDesc);
        Glide.with(mActivity).load(appLogo).into(ivAppLogo);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnPromotion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
//                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity  object
                try {
                    mActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    mActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });
        dialog.show();
    }
}
