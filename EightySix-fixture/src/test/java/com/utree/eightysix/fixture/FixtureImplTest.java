package com.utree.eightysix.fixture;

import com.utree.eightysix.Fixture;
import com.utree.eightysix.data.Circle;
import static org.testng.Assert.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class FixtureImplTest {

  private Fixture mFixture;

  @BeforeMethod
  public void setUp() throws Exception {
    mFixture = new FixtureImpl();
  }

  @AfterMethod
  public void tearDown() throws Exception {
  }

  @Test
  public void testGet() throws Exception {

    Circle circle = mFixture.get(Circle.class, "valid");
    assertNotNull(circle);

  }
}