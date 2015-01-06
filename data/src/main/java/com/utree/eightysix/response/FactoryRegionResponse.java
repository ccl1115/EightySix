package com.utree.eightysix.response;

import com.google.gson.annotations.SerializedName;
import com.utree.eightysix.data.Circle;
import com.utree.eightysix.data.Paginate;
import com.utree.eightysix.rest.Response;

/**
 */
public class FactoryRegionResponse extends Response {

  @SerializedName ("object")
  public Paginate<Circle> object;


  @SerializedName ("subinfo")
  public String subinfo;
}
