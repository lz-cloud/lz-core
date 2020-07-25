package com.wkclz.core.helper.gen;

import cn.hutool.core.date.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Component;

/**
 * 获取序列号【原始原子生成工具，依赖于 redis】
 * @date 2020-03-09 20:52:26
 *
 * 组成：
 * 2. datetime: 格式：yyyyMMddHHmmss, 跟随时间变化
 * 3. sequence: 范围：0~9999，循环使用。一秒内不重复，若并发超过 10^4，将产生阻塞
 * 4. 生成示例：202012312359591234
 * 5. 若出现时间突然倒退，可容差 200ms，产生阻塞。超过200ms将异常
 */
@Component
public class RedisIdGenHelper {

    @Autowired(required = false)
    private RedisTemplate redisTemplate;

    // redis key
    private final static String REDIS_KEY = "redis:sequence";
    // 时间序列格式
    private final static String DATE_FORMAT = "yyyyMMddHHmmss";
    // 上一秒
    private static long lastSecond = 0L;
    // 允许时间回拨的毫秒量 ms
    private final long timeOffset = 100L;
    // 序列最大值
    private final static int SEQUENCE_MASK = 10000;
    // 序列长度
    private final static int SEQUENCE_LENGTH = 4;
    // 计数器，单位时间内，计数不能超过10000，如果超过，需要等待到下一秒
    private static int TIME_UNIT_COUNT = 0;

    private RedisAtomicLong entityIdCounter = null;

    public String nextId() {
        long currentSecond = this.timeGen();
        // 闰秒：如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过，这个时候应当抛出异常
        if (currentSecond < lastSecond) {
            synchronized (this){
                // 校验时间偏移回拨量
                long offset = lastSecond - currentSecond;
                if (offset > timeOffset) {
                    throw new RuntimeException("Clock moved backwards, refusing to generate id for [" + offset + "ms]");
                }
                try {
                    // 时间回退timeOffset毫秒内，则允许等待2倍的偏移量后重新获取，解决小范围的时间回拨问题
                    this.wait(offset << 1);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                // 再次获取
                currentSecond = this.timeGen();
                // 再次校验
                if (currentSecond < lastSecond) {
                    throw new RuntimeException("Clock moved backwards, refusing to generate id for [" + offset + "ms]");
                }
            }
        }

        // 超出范围，重置序列
        if (entityIdCounter == null){
            entityIdCounter = new RedisAtomicLong(REDIS_KEY, redisTemplate.getConnectionFactory());
        }
        Long increment = entityIdCounter.getAndIncrement();
        long sequence = increment % SEQUENCE_MASK;

        // 控制每秒产生数量不能超过最大值。同一秒内，计数器累加，不同秒，重置为0.
        if (lastSecond == currentSecond) {
            TIME_UNIT_COUNT ++;
            // 单位时间内超过限制，等待到下一秒才能生成
            if (TIME_UNIT_COUNT >= SEQUENCE_MASK){
                currentSecond = tilNextSecond(currentSecond);
            }
        } else {
            TIME_UNIT_COUNT = 0;
        }
        lastSecond = currentSecond;

        DateTime dateTime = new DateTime(currentSecond * 1000);
        String currentTime = dateTime.toString(DATE_FORMAT);

        StringBuffer sb = new StringBuffer();
        sb.append(currentTime);

        for (int i = 0; i < SEQUENCE_LENGTH-getLength(sequence); i++) {
            sb.append("0");
        }
        sb.append(sequence);
        return sb.toString();
    }

    /**
     * 保证返回的秒数在参数之后(阻塞到下一个秒，直到获得新的时间戳)——CAS
     */
    private long tilNextSecond(long lastSecond) {
        long timeSecond = this.timeGen();
        while (timeSecond <= lastSecond) {
            // 如果发现时间回拨，则自动重新获取（可能会处于无限循环中）
            lastSecond = this.timeGen();
        }
        return lastSecond;
    }

    /**
     * 获得系统当前秒时间戳
     */
    private long timeGen() {
        long millis = SystemClock.INSTANCE.currentTimeMillis();
        return millis/1000;
    }

    /**
     * 获取数字的长度
     */
    private static int getLength(long number){
        int length = 1;
        while ((number = number / 10) != 0) {
            length++;
        }
        return length;
    }

}
