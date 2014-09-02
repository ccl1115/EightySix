package com.utree.eightysix.utils;

import org.testng.Assert;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

public class IdQueueTest {

  @Test
  public void testGet() throws Exception {
    IdQueue idQueue = new IdQueue(1000, 5);

    Assert.assertEquals(idQueue.get(), 1000);
    Assert.assertEquals(idQueue.get(), 1001);
    Assert.assertEquals(idQueue.get(), 1002);
    Assert.assertEquals(idQueue.get(), 1003);
    Assert.assertEquals(idQueue.get(), 1004);
    Assert.assertEquals(idQueue.get(), 1000);
    Assert.assertEquals(idQueue.get(), 1001);
    Assert.assertEquals(idQueue.get(), 1002);
    Assert.assertEquals(idQueue.get(), 1003);
  }
}
