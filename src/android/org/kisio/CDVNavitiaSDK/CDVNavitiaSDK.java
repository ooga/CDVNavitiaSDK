package org.kisio.CDVNavitiaSDK;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import org.kisio.NavitiaSDK.NavitiaSDK;
import org.kisio.NavitiaSDK.NavitiaConfiguration;
import org.kisio.NavitiaSDK.apis.PlacesApi;

import org.kisio.NavitiaSDK.invokers.ApiCallback;
import org.kisio.NavitiaSDK.invokers.ApiException;

import com.squareup.okhttp.Call;

import java.util.List;
import java.util.Map;

public class CDVNavitiaSDK extends CordovaPlugin {

    private NavitiaSDK navitiaSdk;

    private void init(String token, CallbackContext callbackContext) {
        if (token != null && token.length() > 0) {
            this.navitiaSdk = new NavitiaSDK(new NavitiaConfiguration(token));
            callbackContext.success("SDK created with token " + token);
        } else {
            callbackContext.error("No token provided");
        }
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("init")) {
            String token = args.getString(0);
            this.init(token, callbackContext);
            return true;
        } else if (action.equals("PlacesRequestBuilder.get")) {
            JSONObject params = args.getJSONObject(0);
            this.getPlacesAsyncRaw(params, callbackContext);
            return true;
        }
        return false;
    }

    private void getPlacesAsyncRaw(final JSONObject params, final CallbackContext callbackContext) {
        if (this.navitiaSdk == null) {
            callbackContext.error("NavitiaSDK is not instanciated");
            return;
        }

        final PlacesApi.PlacesRequestBuilder placesRequestBuilder = this.navitiaSdk.placesApi.newPlacesRequestBuilder();
        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (params.has("q") && (params.getString("q") != null) && (!params.getString("q").isEmpty()) ) {
                        placesRequestBuilder.withQ(params.getString("q"));
                    }
                    if (params.has("count") && (params.getString("count") != null)) {
                        placesRequestBuilder.withCount(params.getInt("count"));
                    }
                    placesRequestBuilder.rawGet(new ApiCallback<String>() {
                        @Override
                        public void onFailure(ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {
                            callbackContext.error("Problem during request call | " + e.getMessage());
                        }

                        @Override
                        public void onSuccess(String result, int statusCode, Map<String, List<String>> responseHeaders) {
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(result);
                                callbackContext.success(jsonObject);
                            } catch (Exception e) {
                                String errorMessage = "Problem during response parsing | " + String.valueOf(e.hashCode()) + ": " + e.getMessage();
                                callbackContext.error(errorMessage);
                            }
                        }

                        @Override
                        public void onUploadProgress(long bytesWritten, long contentLength, boolean done) {
                        }

                        @Override
                        public void onDownloadProgress(long bytesRead, long contentLength, boolean done) {
                        }
                    });
                } catch (Exception e) {
                    String errorMessage = "Problem during request building | " + String.valueOf(e.hashCode()) + ": " + e.getMessage();
                    callbackContext.error(errorMessage);
                }
            }
        });

    }
}