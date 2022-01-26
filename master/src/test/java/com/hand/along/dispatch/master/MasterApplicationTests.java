package com.hand.along.dispatch.master;

import com.hand.along.dispatch.common.domain.JobNode;
import com.hand.along.dispatch.common.utils.RedisHelper;
import com.hand.along.dispatch.master.domain.Workflow;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
class MasterApplicationTests {
	@Autowired
	private RedisHelper redisHelper;
	@Test
	void testRedis() {
		redisHelper.setIrt("test","1");
		redisHelper.setIrt("test","3");
		redisHelper.setIrt("test","4");
		redisHelper.setIrt("test","1");
	}

	@Test
	void testMap() {
		Map<String, Object> tmp =new HashMap<>(5);
		tmp.put("a","1");
		tmp.put("b","2");
		tmp.put("c","3");
		Workflow workflow = new Workflow();
		workflow.setParamMap(tmp);
		JobNode jobNode = new JobNode();
		jobNode.setGlobalParamMap(tmp);
		Thread t = new Thread(()->{
			Map<String, Object> paramMap = jobNode.getGlobalParamMap();
			paramMap.put("a","3");
			paramMap.put("b","3");
			paramMap.put("c","5");
		});
		t.start();
		while(true){
			System.out.println(workflow.toString());
		}

	}

}
