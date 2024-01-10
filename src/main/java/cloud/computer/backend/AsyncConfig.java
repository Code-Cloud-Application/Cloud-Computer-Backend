package cloud.computer.backend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {

    @Value("${thread-pool.desktop_creating.core_pool_size}")
    private int desktop_creating_core_pool_size;
    @Value("${thread-pool.desktop_creating.max_pool_size}")
    private int desktop_creating_max_pool_size;
    @Value("${thread-pool.desktop_creating.queue_capacity}")
    private int desktop_creating_queue_capacity;

    @Bean(name = "desktop")
    public Executor customThreadPool() {
        /*
        自定义线程池
         */
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(desktop_creating_core_pool_size); // 最小线程数
        executor.setMaxPoolSize(desktop_creating_max_pool_size); // 最大线程数
        executor.setQueueCapacity(desktop_creating_queue_capacity); // 任务队列容量
        return executor;
    }
}
