package com.hand.along.dispatch.common;

import com.hand.along.dispatch.common.utils.CommonUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

class CommonApplicationTests {

	@Test
	void contextLoads() {
		System.out.println(CommonUtil.humpToLine("SqlJob"));
		System.out.println(CommonUtil.lineToHump("Sql_job"));
	}

}
