package com.utree.eightysix.app.event;

/**
 * @author simon
 */
public class QRCodeScanEvent {

  private String mText;

  public QRCodeScanEvent(String text) {
    mText = text;
  }

  public String getText() {
    return mText;
  }
}
