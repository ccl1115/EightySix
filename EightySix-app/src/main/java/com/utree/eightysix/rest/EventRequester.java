package com.utree.eightysix.rest;

import com.squareup.otto.Subscribe;
import com.utree.eightysix.U;
import com.utree.eightysix.event.RequestEvent;
import de.akquinet.android.androlog.Log;

/**
 * @author simon
 */
public class EventRequester {

  @Subscribe
  public <T extends Response> void onRequestPraisePost(RequestEvent<T> request) {
    RequestData data = U.getRESTRequester().convert(request.getRequest());
    Log.d("EventRequester", "api: " + data.getApi());
    Log.d("EventRequester", "params: " + data.getParams().toString());
    U.getRESTRequester().request(data, new HandlerWrapper<T>(data, request.getOnResponse(), request.getClz()));
  }
}
