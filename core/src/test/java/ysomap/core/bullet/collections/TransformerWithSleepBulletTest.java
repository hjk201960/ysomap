package ysomap.core.bullet.collections;

import org.junit.Test;
import ysomap.core.bean.Bullet;
import ysomap.core.bean.Payload;
import ysomap.core.payload.java.collections.CommonsCollections8;

/**
 * @author wh1t3P1g
 * @since 2020/10/27
 */
public class TransformerWithSleepBulletTest {

    @Test
    public void getObject() throws Exception {
        Payload payload = new CommonsCollections8();
        Bullet bullet = new TransformerWithSleepBullet();
        bullet.set("sleep", "5");
        payload.setBullet(bullet);
//        Serializer serializer = payload.getSerializer();
//        serializer.deserialize(serializer.serialize(payload.getObject()));
    }
}