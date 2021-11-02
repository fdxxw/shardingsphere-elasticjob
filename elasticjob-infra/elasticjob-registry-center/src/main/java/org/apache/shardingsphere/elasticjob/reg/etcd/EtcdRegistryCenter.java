package org.apache.shardingsphere.elasticjob.reg.etcd;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.shardingsphere.elasticjob.reg.base.CoordinatorRegistryCenter;
import org.apache.shardingsphere.elasticjob.reg.base.RawCache;
import org.apache.shardingsphere.elasticjob.reg.base.RawClient;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.AccessLevel;

@Slf4j
public class EtcdRegistryCenter implements CoordinatorRegistryCenter {

  @Getter(AccessLevel.PROTECTED)
  private final EtcdConfiguration etcdConfig;

  private final Map<String, RawCache> caches = new ConcurrentHashMap<>();

  public EtcdRegistryCenter(EtcdConfiguration etcdConfig) {
    this.etcdConfig = etcdConfig;
  }

  @Getter
  private EtcdRawClient client;

  @Override
  public void init() {
    this.client = new EtcdRawClient(Client.builder().endpoints(this.etcdConfig.getEndpoints()).build());
  }

  @Override
  public void close() {
    // TODO Auto-generated method stub

  }

  @Override
  @SneakyThrows
  public String get(String key) {
    this.client.getKVClient().get(ByteSequence.from(key.getBytes())).get();
    return null;
  }

  @Override
  public boolean isExisted(String key) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void persist(String key, String value) {
    // TODO Auto-generated method stub

  }

  @Override
  public void update(String key, String value) {
    // TODO Auto-generated method stub

  }

  @Override
  public void remove(String key) {
    // TODO Auto-generated method stub

  }

  @Override
  public long getRegistryCenterTime(String key) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public RawClient getRawClient() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getDirectly(String key) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<String> getChildrenKeys(String key) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int getNumChildren(String key) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public void persistEphemeral(String key, String value) {
    // TODO Auto-generated method stub

  }

  @Override
  public String persistSequential(String key, String value) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void persistEphemeralSequential(String key) {
    // TODO Auto-generated method stub

  }

  @Override
  public void addCacheData(String cachePath) {
    // TODO Auto-generated method stub

  }

  @Override
  public void evictCacheData(String cachePath) {
    // TODO Auto-generated method stub

  }

  @Override
  public Object getRawCache(String cachePath) {
    // TODO Auto-generated method stub
    return null;
  }

}
