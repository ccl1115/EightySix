package com.utree.eightysix.location;

import android.os.Parcel;
import android.os.Parcelable;

/**
 */
public interface Location {


    /**
     * Call this in Activity's onResume to register the callback
     *
     * @param onResult the callback to register
     */
    void onResume(OnResult onResult);

    /**
     * Call this method in Activity's onPause to unregister the callback
     *
     * @param onResult the callback to unregister
     */
    void onPause(OnResult onResult);

    /**
     * Start a location request
     * <p/>
     * Usually this method is called in an activity context.
     */
    void requestLocation();

    /**
     * Like {@link #requestLocation()}, but only callback once when receive a location
     * and unregister itself automatically
     *
     * @param onResult the callback to register
     */
    void requestLocation(OnResult onResult);

    interface OnResult {
        void onResult(Result result);
    }

    class Result implements Parcelable {
        public String address;
        public String city;
        public String poi;
        public double longitude;
        public double latitude;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(address);
            dest.writeString(city);
            dest.writeString(poi);
            dest.writeDouble(longitude);
            dest.writeDouble(latitude);
        }

        public final static Creator<Result> CREATOR = new Creator<Result>() {
            @Override
            public Result createFromParcel(Parcel source) {
                Result result = new Result();
                result.address = source.readString();
                result.city = source.readString();
                result.poi = source.readString();
                result.longitude = source.readDouble();
                result.latitude = source.readInt();
                return result;
            }

            @Override
            public Result[] newArray(int size) {
                return new Result[size];
            }
        };
    }
}
