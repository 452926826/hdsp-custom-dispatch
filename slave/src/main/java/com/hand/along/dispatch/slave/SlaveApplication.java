package com.hand.along.dispatch.slave;

import com.hand.along.dispatch.slave.infra.netty.NettyClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan({
		"com.hand.**.mapper"
})
public class SlaveApplication {

	public static void main(String[] args) {
		try {
			SpringApplication.run(SlaveApplication.class, args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
