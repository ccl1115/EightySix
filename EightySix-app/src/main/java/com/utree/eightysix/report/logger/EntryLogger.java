package com.utree.eightysix.report.logger;


/**
 */
public interface EntryLogger {

  <T extends EntryAdapter> void log(T entryAdapter);
}
