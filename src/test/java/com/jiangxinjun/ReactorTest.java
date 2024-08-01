package com.jiangxinjun;

import org.junit.Test;
import org.slf4j.Logger;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * @author jiangxinjun
 * @since 2024/7/31
 */
public class ReactorTest {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(ReactorTest.class);

    @Test
    public void reactorTest() {
        log.info("test");
        //just()：创建Flux序列，并声明数据流，
        Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4);//整形
        //subscribe()：订阅Flux序列，只有进行订阅后才回触发数据流，不订阅就什么都不会发生
        integerFlux.subscribe(integer -> log.info(integer.toString()));

        Flux<String> stringFlux = Flux.just("hello", "world");//字符串
        stringFlux.subscribe(System.out::println);

        //fromArray(),fromIterable()和fromStream()：可以从一个数组、Iterable 对象或Stream 对象中创建Flux序列
        Integer[] array = {1, 2, 3, 4};
        Flux.fromArray(array).subscribe(System.out::println);

        List<Integer> integers = Arrays.asList(array);
        Flux.fromIterable(integers).subscribe(System.out::println);

        Stream<Integer> stream = integers.stream();
        Flux.fromStream(stream).subscribe(System.out::println);
    }

    @Test
    public void reactorTest2() {
        //just()：创建Flux序列，并声明数据流，
        Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4).parallel().runOn(Schedulers.parallel()).sequential();//整形
        //subscribe()：订阅Flux序列，只有进行订阅后才回触发数据流，不订阅就什么都不会发生
        integerFlux.subscribe(integer -> log.info(integer.toString()));
    }

    @Test
    public void reactorTest3() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Flux<Integer> numbers = Flux.range(1, 100);
        // 并行处理
        Disposable subscribe = numbers
                .parallel()
                .runOn(Schedulers.parallel())
                .map(number -> {
                    // 模拟耗时操作
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return number * 2;
                })
                .sequential()
                .doOnComplete(() -> {
                    log.info("Done2");
                    latch.countDown();
                })
                .subscribe(System.out::println);
        latch.await(100, TimeUnit.SECONDS);
    }

}
