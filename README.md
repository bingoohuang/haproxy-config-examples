# haproxy-config-examples

haproxy configuration examples

## HAProxy Hardware recommendation

Due to its way of working, HAProxy requires:

* CPU: prefer the speed and the cache size over the number of cores
* enough memory to handle all the TCP connections + HAProxy + system * network interfaces: intel ones are usually a good choice
* disk: not required, unless logging locally

In order to get the best performance, a bit of organization is required:

* Network interrupts and kernel on core 0
* HAProxy on next core on the same physical CPU

* NOTE: of course, uninstall irqbalance
* Avoid VM or public cloud with shared resources if the expected workload is important.

## Sysctls tuning

The most important sysctls are:

* net.ipv4.ip_local_port_range = "1025 65534" * net.ipv4.tcp_max_syn_backlog = 100000
* net.core.netdev_max_backlog = 100000
* net.core.somaxconn = 65534
* ipv4.tcp_rmem = "4096 16060 64060"
* ipv4.tcp_wmem = "4096 16384 262144"

Depending on the workload:

* tcp_slow_start_after_idle = 0

iptables tuning:

* net.netfilter.nf_conntrack_max = 131072
* when improperly configured, conntrack will prevent HAProxy from reaching high performance.
* NOTE: just enabling iptables with connection tracking takes 20% of CPU, even with no rules.

## MySQL cluster backup demo

1. `cd mysql; docker-compose rm -fsv; docker-compose up`
1. `cd java/jdbc-connection-pool-test; mvn clean package; java -jar target/jdbc-connection-pool-test-0.1.1-exec.jar`
1. `enter line on java console to execute sql "select * from t1" and check result`
1. `docker-compose start mysqlmaster1`
1. `enter line on java console to execute sql "select * from t1" and re-check result`

## Haproxy + Mysql Lost connection to MySQL server during query

```sql
mysql> select * from t1;
ERROR 2013 (HY000): Lost connection to MySQL server during query
```

I think i find the reason.Haproxy itself has a timeout for server and client,I set the sever timeout and client timeout as same as mysql timeout which is 8 hour.Now it seems like this:

```ini
defaults
    log                     global
    mode                    tcp
    option                  tcplog
    option                  dontlognull
    option                  http-server-close
    option                  redispatch
    retries                 3
    timeout http-request    10s
    timeout queue           1m
    timeout connect         10s
    timeout client          480m
    timeout server          480m
    timeout http-keep-alive 10s
    timeout check           10s
    maxconn                 3000
```

Hope can help others.

1. [Haproxy + Mysql Lost connection to MySQL server during query](https://dba.stackexchange.com/questions/132964/haproxy-mysql-lost-connection-to-mysql-server-during-query)
1. [ERROR 2006 (HY000): MySQL Server Has Gone Away… (HaProxy / Galera)](https://serverfault.com/questions/730403/error-2006-hy000-mysql-server-has-gone-away-haproxy-galera/730565#730565)
1. [I use haproxy as banlancer for mariadb cluster，but got lost connection during query](https://stackoverflow.com/questions/37407021/i-use-haproxy-as-banlancer-for-mariadb-cluster-but-got-lost-connection-during-qu/37426548#37426548)

## HAProxy multi-process

Advantages:

* ability to dedicate a process to a task (or application, or protocol)
    In example: 1 process for HTTP and 1 process for MySQL
* scale up: same hardware, more processing capacity by binding processes to different CPU cores
* useful when massive SSL offloading processing is required
    key generation scales almost linearly with number of processes, but TLS session resumption gets little gain over 3 processes

Limitations:

Each process has its own memory area, which means:

* debug mode cancels multi-process (a single process is started)
* frontend(s) and associated backend(s) must run on the same process
* not compatible with peers section (stick table synchronization)
* information is stored locally in each process memory area and can't be shared:
  * stick table + tracked counters
  * statistics
  * server's maxconn (queue management)
  * connection rate
* Each HAProxy process performs its health check:
  * a service is probed by each process
  * a service can temporarly have different status in each process
* managing a configuration which starts up multiple processes can be more complicated

## simple configuration

```bash
➜  simple git:(master) ✗ http :10080
HTTP/1.1 200 OK
Content-Type: text/plain
Date: Wed, 11 Sep 2019 09:31:53 GMT
Server: openresty/1.15.8.2
Transfer-Encoding: chunked

hello 8001, world 8001!

➜  simple git:(master) ✗ http :10080
HTTP/1.1 200 OK
Content-Type: text/plain
Date: Wed, 11 Sep 2019 09:31:55 GMT
Server: openresty/1.15.8.2
Transfer-Encoding: chunked

hello 8002, world 8002!

➜  simple git:(master) ✗ http :10080
HTTP/1.1 200 OK
Content-Type: text/plain
Date: Wed, 11 Sep 2019 09:31:57 GMT
Server: openresty/1.15.8.2
Transfer-Encoding: chunked

hello 8001, world 8001!
```

## mysql configuration

[mysql-check doc](http://cbonte.github.io/haproxy-dconv/1.7/configuration.html#4-option%20mysql-check)

checking `docker-compose exec mysqlmaster1 mysql -h ha1 -uroot -proot -vvv -e 'select * from bjca.t1'`.

## Thanks

1. [Haproxy best practice](https://www.slideshare.net/haproxytech/haproxy-best-practice)

## Why Alpine

[The Dockerized version of Alpine 3.6 weighs in at 3.98MB.](https://nickjanetakis.com/blog/the-3-biggest-wins-when-using-alpine-as-a-base-docker-image)

For comparison, here’s how Alpine compares to other popular distributions of Linux:

| DISTRIBUTION | VERSION | SIZE    |
|--------------|---------|---------|
| Debian       | Jessie  | 123MB   |
| CentOS       | 7       | 193MB   |
| Fedora       | 25      | 231MB   |
| Ubuntu       | 16\.04  | 118MB   |
| Alpine       | 3\.6    | 3\.98MB |

The markdown table is generated by [tableconvert](https://tableconvert.com/).

Wow, check out the difference in size. Alpine is about 30x smaller than Debian.
