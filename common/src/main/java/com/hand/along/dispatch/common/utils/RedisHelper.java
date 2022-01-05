package com.hand.along.dispatch.common.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
public class RedisHelper  implements InitializingBean {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private ValueOperations<String, String> valueOpr;
    @Autowired
    private HashOperations<String, String, String> hashOpr;
    @Autowired
    private ListOperations<String, String> listOpr;
    @Autowired
    private SetOperations<String, String> setOpr;
    @Autowired
    private ZSetOperations<String, String> zSetOpr;
    public static final long DEFAULT_EXPIRE = 86400L;
    public static final long NOT_EXPIRE = -1L;

    public RedisHelper() {
    }

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.getRedisTemplate(), "redisTemplate must not be null.");
        Assert.notNull(this.getValueOperations(), "redisTemplate must not be null.");
        Assert.notNull(this.getHashOperations(), "redisTemplate must not be null.");
        Assert.notNull(this.getListOperations(), "redisTemplate must not be null.");
        Assert.notNull(this.getSetOperations(), "redisTemplate must not be null.");
        Assert.notNull(this.getZSetOperations(), "redisTemplate must not be null.");
    }

    public static ObjectMapper getObjectMapper() {
        return JSON.getObjectMapper();
    }

    public void setCurrentDatabase(int database) {
    }

    public void clearCurrentDatabase() {
    }

    public void delKey(String key) {
        this.getRedisTemplate().delete(key);
    }

    public Boolean hasKey(String key) {
        return this.getRedisTemplate().hasKey(key);
    }

    public Long getExpire(String key) {
        return this.getRedisTemplate().getExpire(key);
    }

    public Long getExpire(String key, final TimeUnit timeUnit) {
        return this.getRedisTemplate().getExpire(key, timeUnit);
    }

    public Boolean setExpire(String key) {
        return this.setExpire(key, 86400L, TimeUnit.SECONDS);
    }

    public Boolean setExpire(String key, long expire) {
        return this.setExpire(key, expire, TimeUnit.SECONDS);
    }

    public Boolean setExpire(String key, long expire, TimeUnit timeUnit) {
        return this.getRedisTemplate().expire(key, expire, timeUnit == null ? TimeUnit.SECONDS : timeUnit);
    }

    public void delKeys(Collection<String> keys) {
        Set<String> hs = new HashSet();
        Iterator var3 = keys.iterator();

        while(var3.hasNext()) {
            String key = (String)var3.next();
            hs.add(key);
        }

        this.getRedisTemplate().delete(hs);
    }

    private void deleteFullKey(String fullKey) {
        this.getRedisTemplate().delete(fullKey);
    }

    private void deleteFullKeys(Collection<String> fullKeys) {
        this.getRedisTemplate().delete(fullKeys);
    }

    public void strSet(String key, String value, long expire, TimeUnit timeUnit) {
        this.getValueOperations().set(key, value);
        if (expire != -1L) {
            this.setExpire(key, expire, timeUnit == null ? TimeUnit.SECONDS : timeUnit);
        }

    }

    public void strSet(String key, String value) {
        this.getValueOperations().set(key, value);
    }

    public String strGet(String key) {
        return (String)this.getValueOperations().get(key);
    }

    @Nonnull
    public List<String> strMultiGet(Collection<String> keys) {
        return (List)Optional.ofNullable(this.getValueOperations().multiGet(keys)).orElse(Collections.emptyList());
    }

    public Map<String, String> strMultiGetMap(List<String> keys) {
        List<String> resultList = this.strMultiGet(keys);
        Map<String, String> resultMap = new HashMap(keys.size() + 4);

        for(int i = 0; i < keys.size(); ++i) {
            resultMap.put(keys.get(i), Optional.ofNullable(resultList.get(i)).map(String::valueOf).orElse( null));
        }

        return resultMap;
    }

    public String strGet(String key, long expire, TimeUnit timeUnit) {
        String value = (String)this.getValueOperations().get(key);
        if (expire != -1L) {
            this.setExpire(key, expire, timeUnit == null ? TimeUnit.SECONDS : timeUnit);
        }

        return value;
    }

    public <T> T strGet(String key, Class<T> clazz) {
        String value = (String)this.getValueOperations().get(key);
        return value == null ? null : this.fromJson(value, clazz);
    }

    @Nonnull
    public <T> List<T> strMultiGet(Collection<String> keys, Class<T> clazz) {
        List<String> values = this.getValueOperations().multiGet(keys);
        return values != null && values.size() > 0 ? (List)values.stream().map((item) -> {
            return this.fromJson(item, clazz);
        }).collect(Collectors.toList()) : Collections.emptyList();
    }

    public <T> Map<String, T> strMultiGetMap(List<String> keys, Class<T> clazz) {
        List<T> resultList = this.strMultiGet(keys, clazz);
        Map<String, T> resultMap = new HashMap(keys.size() + 4);

        for(int i = 0; i < keys.size(); ++i) {
            resultMap.put(keys.get(i), resultList.get(i));
        }

        return resultMap;
    }

    public <T> T strGet(String key, Class<T> clazz, long expire, TimeUnit timeUnit) {
        String value = (String)this.getValueOperations().get(key);
        if (expire != -1L) {
            this.setExpire(key, expire, timeUnit == null ? TimeUnit.SECONDS : timeUnit);
        }

        return value == null ? null : this.fromJson(value, clazz);
    }

    public String strGet(String key, Long start, Long end) {
        return this.getValueOperations().get(key, start, end);
    }

    public Boolean strSetIfAbsent(String key, String value) {
        return this.getValueOperations().setIfAbsent(key, value);
    }

    public Boolean strMultiSetIfAbsent(Map<String, String> map) {
        return this.getValueOperations().multiSetIfAbsent(map);
    }

    public Long strIncrement(String key, Long delta) {
        return this.getValueOperations().increment(key, delta);
    }

    public Long lstLeftPush(String key, String value) {
        return this.getListOperations().leftPush(key, value);
    }

    public Long lstLeftPushAll(String key, Collection<String> values) {
        return this.getListOperations().leftPushAll(key, values);
    }

    public Long lstRightPush(String key, String value) {
        return this.getListOperations().rightPush(key, value);
    }

    public Long lstRightPushAll(String key, Collection<String> values) {
        return this.getListOperations().rightPushAll(key, values);
    }

    public List<String> lstRange(String key, long start, long end) {
        return this.getListOperations().range(key, start, end);
    }

    public List<String> lstAll(String key) {
        return this.lstRange(key, 0L, this.lstLen(key));
    }

    public String lstLeftPop(String key) {
        return (String)this.getListOperations().leftPop(key);
    }

    public String lstRightPop(String key) {
        return (String)this.getListOperations().rightPop(key);
    }

    public String lstLeftPop(String key, long timeout, TimeUnit timeUnit) {
        return (String)this.getListOperations().leftPop(key, timeout, timeUnit);
    }

    public String lstRightPop(String key, long timeout, TimeUnit timeUnit) {
        return (String)this.getListOperations().rightPop(key, timeout, timeUnit);
    }

    public Long lstLen(String key) {
        return this.getListOperations().size(key);
    }

    public void lstSet(String key, long index, String value) {
        this.getListOperations().set(key, index, value);
    }

    public Long lstRemove(String key, long index, String value) {
        return this.getListOperations().remove(key, index, value);
    }

    public Object lstIndex(String key, long index) {
        return this.getListOperations().index(key, index);
    }

    public void lstTrim(String key, long start, long end) {
        this.getListOperations().trim(key, start, end);
    }

    public Long setAdd(String key, String[] values) {
        return this.getSetOperations().add(key, values);
    }

    public Long setIrt(String key, String... values) {
        return this.getSetOperations().add(key, values);
    }

    public Set<String> setMembers(String key) {
        return this.getSetOperations().members(key);
    }

    public Boolean setIsmember(String key, String o) {
        return this.getSetOperations().isMember(key, o);
    }

    public Long setSize(String key) {
        return this.getSetOperations().size(key);
    }

    public Set<String> setIntersect(String key, String otherKey) {
        return this.getSetOperations().intersect(key, otherKey);
    }

    public Set<String> setUnion(String key, String otherKey) {
        return this.getSetOperations().union(key, otherKey);
    }

    public Set<String> setUnion(String key, Collection<String> otherKeys) {
        return this.getSetOperations().union(key, otherKeys);
    }

    public Set<String> setDifference(String key, String otherKey) {
        return this.getSetOperations().difference(key, otherKey);
    }

    public Set<String> setDifference(String key, Collection<String> otherKeys) {
        return this.getSetOperations().difference(key, otherKeys);
    }

    public Long setDel(String key, String value) {
        return this.getSetOperations().remove(key, new Object[]{value});
    }

    public Long setRemove(String key, Object[] value) {
        return this.getSetOperations().remove(key, value);
    }

    public Boolean zSetAdd(String key, String value, double score) {
        return this.getZSetOperations().add(key, value, score);
    }

    public Double zSetScore(String key, String value) {
        return this.getZSetOperations().score(key, value);
    }

    public Double zSetIncrementScore(String key, String value, double delta) {
        return this.getZSetOperations().incrementScore(key, value, delta);
    }

    public Long zSetRank(String key, String value) {
        return this.getZSetOperations().rank(key, value);
    }

    public Long zSetReverseRank(String key, String value) {
        return this.getZSetOperations().reverseRank(key, value);
    }

    public Long zSetSize(String key) {
        return this.getZSetOperations().size(key);
    }

    public Long zSetRemove(String key, String value) {
        return this.getZSetOperations().remove(key, new Object[]{value});
    }

    public Long zSetRemoveByScore(String key, double min, double max) {
        return this.getZSetOperations().removeRangeByScore(key, min, max);
    }

    public Set<String> zSetRange(String key, Long start, Long end) {
        return this.getZSetOperations().range(key, start, end);
    }

    public Set<String> zSetReverseRange(String key, Long start, Long end) {
        return this.getZSetOperations().reverseRange(key, start, end);
    }

    public Set<String> zSetRangeByScore(String key, Double min, Double max) {
        return this.getZSetOperations().rangeByScore(key, min, max);
    }

    public Set<String> zSetReverseRangeByScore(String key, Double min, Double max) {
        return this.getZSetOperations().reverseRangeByScore(key, min, max);
    }

    public Set<String> zSetRangeByScore(String key, Double min, Double max, Long offset, Long count) {
        return this.getZSetOperations().rangeByScore(key, min, max, offset, count);
    }

    public Set<String> zSetReverseRangeByScore(String key, Double min, Double max, Long offset, Long count) {
        return this.getZSetOperations().reverseRangeByScore(key, min, max, offset, count);
    }

    public Long zSetCount(String key, Double min, Double max) {
        return this.getZSetOperations().count(key, min, max);
    }

    public void hshPut(String key, String hashKey, String value) {
        this.getHashOperations().put(key, hashKey, value);
    }

    public void hshPutAll(String key, Map<String, String> map) {
        this.getHashOperations().putAll(key, map);
    }

    public String hshGet(String key, String hashKey) {
        return (String)this.getHashOperations().get(key, hashKey);
    }

    public List<String> hshMultiGet(String key, Collection<String> hashKeys) {
        return this.getHashOperations().multiGet(key, hashKeys);
    }

    public Map<String, String> hshGetAll(String key) {
        return this.getHashOperations().entries(key);
    }

    public Boolean hshHasKey(String key, String hashKey) {
        return this.getHashOperations().hasKey(key, hashKey);
    }

    public Set<String> hshKeys(String key) {
        return this.getHashOperations().keys(key);
    }

    public List<String> hshVals(String key) {
        return this.getHashOperations().values(key);
    }

    public List<String> hshVals(String key, Collection<String> hashKeys) {
        return this.getHashOperations().multiGet(key, hashKeys);
    }

    public Long hshSize(String key) {
        return this.getHashOperations().size(key);
    }

    public void hshDelete(String key, Object... hashKeys) {
        this.getHashOperations().delete(key, hashKeys);
    }

    public void hshRemove(String key, Object[] hashKeys) {
        this.getHashOperations().delete(key, hashKeys);
    }

    public <T> T executeScript(RedisScript<T> redisScript, List<String> keys, List<Object> args) {
        return this.getRedisTemplate().execute(redisScript, keys, args.toArray());
    }

    /** @deprecated */
    @Deprecated
    public <T> String toJson(T object) {
        return JSON.toJson(object);
    }

    /** @deprecated */
    @Deprecated
    public <T> T fromJson(String json, Class<T> clazz) {
        return JSON.fromJson(json, clazz);
    }

    /** @deprecated */
    @Deprecated
    public <T> T fromJson(String json, TypeReference<T> valueTypeRef) {
        return JSON.fromJson(json, valueTypeRef);
    }

    /** @deprecated */
    @Deprecated
    public <T> List<T> fromJsonList(String json, Class<T> clazz) {
        return JSON.fromJsonList(json, clazz);
    }

    public <T> void objectSet(String key, T object) {
        this.strSet(key, this.toJson(object));
    }

    /** @deprecated */
    @Deprecated
    public int deleteKeysWithPrefix(String keyPrefix) {
        Set<String> keys = this.keys(keyPrefix + '*');
        if (keys != null && !keys.isEmpty()) {
            this.deleteFullKeys(keys);
            return keys.size();
        } else {
            return 0;
        }
    }

    /** @deprecated */
    @Deprecated
    public Set<String> keys(String pattern) {
        return this.getRedisTemplate().keys(pattern);
    }

    public RedisTemplate<String, String> getRedisTemplate() {
        return this.redisTemplate;
    }

    protected ValueOperations<String, String> getValueOperations() {
        return this.valueOpr;
    }

    protected HashOperations<String, String, String> getHashOperations() {
        return this.hashOpr;
    }

    protected ListOperations<String, String> getListOperations() {
        return this.listOpr;
    }

    protected SetOperations<String, String> getSetOperations() {
        return this.setOpr;
    }

    protected ZSetOperations<String, String> getZSetOperations() {
        return this.zSetOpr;
    }

}

