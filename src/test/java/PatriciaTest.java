import com.alibaba.fastjson.JSON;
import com.wxcbk.nlp.ptct.NodeItem;
import com.wxcbk.nlp.ptct.PatriciaTrie;
import javafx.util.Pair;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author :owen
 * @date :2020/11/12 14:59
 * @Description :
 */
public class PatriciaTest {


    @Test
    public void insert1() {
        PatriciaTrie<String> patriciaTrie = new PatriciaTrie<>();
        Pair<String, String> pair1 = new Pair<>("北京", "首都");
        patriciaTrie.insertWord(pair1);
        Pair<String, String> pair2 = new Pair<>("上海", "魔都");
        patriciaTrie.insertWord(pair2);
        System.out.println(JSON.toJSONString(patriciaTrie));
    }

    @Test
    public void insert2() {
        Pair<String, String> pair1 = new Pair<>("北京", "首都");
        Pair<String, String> pair2 = new Pair<>("上海", "魔都");
        List<Pair<String, String>> list = new ArrayList<>();
        list.add(pair1);
        list.add(pair2);
        PatriciaTrie patriciaTrie = new PatriciaTrie<String>().buildPatriciaTrie(list);
        System.out.println(JSON.toJSONString(patriciaTrie));
    }

    @Test
    public void query() {
        Pair<String, String> pair1 = new Pair<>("北京", "首都");
        Pair<String, String> pair2 = new Pair<>("上海", "魔都");
        List<Pair<String, String>> list = new ArrayList<>();
        list.add(pair1);
        list.add(pair2);
        PatriciaTrie<String> patriciaTrie = new PatriciaTrie<String>().buildPatriciaTrie(list);
        String query = "北京上海的同事";
        List<NodeItem<String>> nodeItem = patriciaTrie.search(query);
        System.out.println(JSON.toJSONString(nodeItem));

    }


}
