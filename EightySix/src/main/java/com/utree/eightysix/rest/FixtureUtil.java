package com.utree.eightysix.rest;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.ObjectFactory;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.base.Sequence;
import com.utree.eightysix.C;
import com.utree.eightysix.response.CirclesResponse;
import com.utree.eightysix.response.Paginate;
import com.utree.eightysix.response.data.Circle;
import com.utree.eightysix.response.data.Circles;
import com.utree.eightysix.response.data.Post;
import java.util.Random;

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

  private static String[] FIXTURE_POST_CONTENT = { "人性：如果你每天给他一元钱，只要一天不给，他就会恨你！——如果每天给他一巴掌，只要一天不打他就会跪谢你！",
      "昨天和男朋友爱爱，他着急，准备用一只手把我胸罩弄开，结果半天弄不开，这二货就问你这怎么这么难打开，我说都是这样的啊，然后他说，别人的都好弄啊！我当你是口误？",
      "父亲节那天，为了让老公感受一下节日地快乐，特意拉着他陪我和宝宝逛超市，买的算是瓶瓶罐罐的汤汤水水类，又去菜市场，排骨，龙骨，萝卜，面…，最后提着三大包回家，还得做饭，洗衣服，拖地…。我睡觉他带哇，这父亲节过得，一年能两次麽？",
      "苦逼工程男一枚，四个月没回家了，昨天回家见到老婆那个渴望你懂的。知道父母不在家所以抱起二货老婆就往卧室走去。可是你个二货为什么不告诉我丈母娘在卧室呢！那什么丈母娘你好歹也吃完饭在走啊！还有老婆你别笑了行不？",
      "那些几十岁的大叔们，你们和我们年轻人抢女朋友，就不要怪我们对你们未成年的女儿下手。",
      "尼玛，昨晚女友脱光了和我睡一起，太兴奋，就一直想怎么搞能让她最舒服，想太久后来睡着了，尼玛，睡着了，，着了，，了",
      "昨天体检，检查外科的时候，医生让我把裤子脱了，然后撅屁股，本以为她只是看看那里有没有长东西，哪知道还没等我站稳，她就一个指头戳进去，啊，多么痛的领悟！！！那感觉，至今难忘……",
      "之前和老公离婚了，偶尔去看孩子的时候闻到他身上一股香味，还暗想这家伙现在还会喷香水了，后来复婚了才知道那是力白洗衣液的味道…",
      "今天糗死了，穿了一裤裙和老妈去逛街，老妈看我的裙子太短了，就把裙子使劲往下拽结果拽的太使劲了连裤子也一起扯下来了，我的小pp就这么赤裸裸的暴露在大街上了。呜呜呜",
      "本人女，现在每天都在等一个男生给我发信息，他一天不发或者不回我信息就感觉好失落，好难受，他发信息了就感觉好开心，我想说我是不是喜欢上他了，求解",
      "对付蛮横要价故意拆散恩爱情侣或者趁机敲竹杠的丈母娘，本人一向主张先领证再有孩子后跟丈母娘谈判。",
      "记得有一次生病，家人和亲戚都要隔离，我一三岁的外甥女，说，姨姨，妈妈不让我碰你，我偷偷抱抱你，你别让我妈妈知道，当时心里就特别的酸，眼泪瞬间就就出来了",
      "你们再不要抱怨丈母娘迟迟不发货，我妈一直想发货，但不知道地址写哪儿~~85后的妹纸伤不起"
  };

  private static String[] FIXTURE_BG_URL = {
      "http://utree-images.oss-cn-beijing.aliyuncs.com/c/1b/cfe6eb49253e58ab5bffce60a0b",
      "http://utree-images.oss-cn-beijing.aliyuncs.com/f/13/d9f9b88e0c13b611513dbab0e5a",
      "",
      ""
  };

  private static Integer[] FIXTURE_BG_COLOR = {
      0xff403923,
      0xff4a49f3,
      0xff3296ad,
      0xff38993f,
      0xffaa439c,
      0xff89ce94
  };


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

    Fixture.of(Post.class).addTemplate("valid", new Rule() {
      {
        add("id", random(Integer.class));
        add("bgUrl", random(FIXTURE_BG_URL));
        add("bgColor", random(FIXTURE_BG_COLOR));
        add("content", random(FIXTURE_POST_CONTENT));
        add("comments", random(Integer.class, range(0, 10000)));
        add("praise", random(Integer.class, range(0, 10000)));
        add("source", random(FIXTURE_CIRCLES));
        add("praised", random(0, 1));
      }
    });
  }

  public static <T> ObjectFactory from(Class<T> clz) {
    return Fixture.from(clz);
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
