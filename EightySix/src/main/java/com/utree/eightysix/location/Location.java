package com.utree.eightysix.location;

/**
 */
public interface Location {


    void onResume(OnResult onResult);

    void onPause(OnResult onResult);

    void requestLocation();

    class Result {
        public String address;
        public String city;
        public String poi;
        public double longitude;
        public double latitude;
    }

    interface OnResult {
        void onResult(Result result);
    }
}
