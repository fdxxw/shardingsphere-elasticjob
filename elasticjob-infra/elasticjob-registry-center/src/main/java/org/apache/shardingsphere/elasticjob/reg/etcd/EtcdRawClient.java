package org.apache.shardingsphere.elasticjob.reg.etcd;

import org.apache.shardingsphere.elasticjob.reg.base.RawClient;

import io.etcd.jetcd.Auth;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.Cluster;
import io.etcd.jetcd.Maintenance;
import io.etcd.jetcd.Lease;
import io.etcd.jetcd.Watch;
import io.etcd.jetcd.Lock;
import io.etcd.jetcd.Election;
import io.etcd.jetcd.Client;
import lombok.Getter;

public class EtcdRawClient implements RawClient {
  @Getter
  private final Client client;

  public EtcdRawClient(Client client) {
    this.client = client;
  }

  public Auth getAuthClient() {
    return client.getAuthClient();
  }

  public KV getKVClient() {
    return client.getKVClient();
  }

  public Cluster getClusterClient() {
    return client.getClusterClient();
  }

  public Maintenance getMaintenanceClient() {
    return client.getMaintenanceClient();
  }

  public Lease getLeaseClient() {
    return client.getLeaseClient();
  }

  public Watch getWatchClient() {
    return client.getWatchClient();
  }

  public Lock getLockClient() {
    return client.getLockClient();
  }

  public Election getElectionClient() {
    return client.getElectionClient();
  }
}
