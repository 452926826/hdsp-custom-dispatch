package com.hand.along.dispatch.common;

import com.hand.along.dispatch.common.utils.CommonUtil;
import org.junit.jupiter.api.Test;

class CommonApplicationTests {

	@Test
	void contextLoads() {
		System.out.println(CommonUtil.humpToLine("SqlJob"));
		System.out.println(CommonUtil.lineToHump("Sql_job"));
	}

	@Test
	void testSub() {
		String url = "http://172.23.16.63:9000/minio/download/hand/test.txt";
		String buket = "hand";
		System.out.println(url.substring(url.lastIndexOf(buket)+buket.length()+1));
	}

}
