package org.apache.shardingsphere.elasticjob.reg.etcd;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * 
 */
@Getter
@Setter
@RequiredArgsConstructor
public final class EtcdConfiguration {
  /**
   * etcd server list
   */
  private final String[] endpoints;
}
