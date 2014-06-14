package com.utree.eightysix.rest;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.base.Sequence;
import com.utree.eightysix.C;
import com.utree.eightysix.response.CirclesResponse;
import com.utree.eightysix.response.Paginate;
import com.utree.eightysix.response.data.Circle;
import com.utree.eightysix.response.data.Circles;

/**
 * Provide fixture data
 *
 * @author simon
 */
public class FixtureUtil {

  private static FixtureUtil sFixtureUtil;
  private static String[] FIXTURE_CITIES = {"凯里", "重庆", "昆山", "北京", "深圳", "东莞", "上海", "L.A.", "成都"};
  private static String[] FIXTURE_VIEW_GROUP_TYPES = {"最佳建议", "在职工厂", "智能推荐"};
  private static String[] FIXTURE_CIRCLES = {"圣美电脑", "洗脚城", "富士康", "仁宝电脑" };

  static {
    Fixture.of(Circle.class).addTemplate("valid", new Rule() {
      {
        add("name", random(FIXTURE_CIRCLES));
        add("friendCount", random(Integer.class, range(1, 100)));
        add("workmateCount", random(Integer.class, range(100, 10000)));
        add("cityName", random(FIXTURE_CITIES));
        add("distance", random(Integer.class, range(1000, 30000)));
        add("circleType", random(1, 2));
        add("lock", random(1, 2));
        add("viewGroupType", sequence(new Sequence<String>() {
          private int count = 0;

          @Override
          public String nextValue() {
            count++;
            if (count < 3) {
              return FIXTURE_VIEW_GROUP_TYPES[0];
            } else if (count < 6) {
              return FIXTURE_VIEW_GROUP_TYPES[1];
            } else {
              return FIXTURE_VIEW_GROUP_TYPES[2];
            }
          }
        }));
        add("viewType", random("1", "2", "3", "4"));
      }
    });

    Fixture.of(Paginate.Page.class).addTemplate("valid", new Rule() {
      {
        add("countPage", 100);
        add("countRec", 1);
        add("currPage", sequence(1, 1));
        add("endRecord", 1);
        add("pageSize", 20);
        add("startRecord", 1);
      }
    });

  }

  public static Response get(String api) {
    return getFixtureUtil().route(api);
  }

  private static FixtureUtil getFixtureUtil() {
    if (sFixtureUtil == null) {
      sFixtureUtil = new FixtureUtil();
    }
    return sFixtureUtil;
  }

  private Response route(String api) {
    if (C.API_FACTORY_MY.equals(api)) {
      return getCirclesResponse();
    } else {
      return null;
    }
  }

  private CirclesResponse getCirclesResponse() {
    CirclesResponse response = new CirclesResponse();

    response.code = 0;
    response.message = "";
    Circles circles = new Circles();
    circles.factoryCircle = new Paginate<Circle>();
    circles.factoryCircle.lists = Fixture.from(Circle.class).gimme(20, "valid");
    circles.factoryCircle.page = Fixture.from(Paginate.Page.class).gimme("valid");

    response.object = circles;

    return response;
  }
}
