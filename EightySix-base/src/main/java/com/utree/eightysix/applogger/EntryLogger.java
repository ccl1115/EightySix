package com.utree.eightysix.applogger;


/**
 */
public interface EntryLogger {

  <T extends EntryAdapter> void log(T entryAdapter);
}
