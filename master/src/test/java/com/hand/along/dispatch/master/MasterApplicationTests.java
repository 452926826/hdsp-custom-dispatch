package com.hand.along.dispatch.master;

import com.hand.along.dispatch.common.utils.RedisHelper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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

}
