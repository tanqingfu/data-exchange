package com.sdy.dataexchange.biz.constants;

public class MqConstants {
    public interface Topics {
        String TOPIC_DEMO = "topic_data-exchange";
        String TOPIC_DATAEXCHANGE_PREFIX = "topic_dataexchange_";
        String TOPIC_MYSQL_CANAL = "canal_example";
    }
    public interface Tags {
        String TAG_DEMO1 = "tag_demo1";
        String TAG_DEMO2 = "tag_demo2";
    }
    
    public interface Consumer {
        String CONSUMER_GROUP_PREFIX = "g_de_";
    }
}
