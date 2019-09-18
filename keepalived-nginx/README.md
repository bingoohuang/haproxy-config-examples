# nginx-keepalived-docker-demo

This is orginally forked from [hengfengli/nginx-keepalived-docker-demo](https://github.com/hengfengli/nginx-keepalived-docker-demo).

A small demo to run two nginx containers in active-passive mode by using keepalived and VIP.

This demo is made to simulate a scenario with two running nginx servers, one as a master and another as a backup, in order to achieve high availability.

There are some limitations in this demo:

* I run it in a single host. Normally, it would be better to create a scenario with two hosts.
* I didn't spend time to map the virtual IP from docker network to the host. Instead, I use a haproxy to do this task.
* Running both nginx and keepalived in an Alpine container is not easy. rc-service could not manage keepalived properly. I thought to use supervisor or circus, but they are a bit heavy in space. So I directly run keepalived as a background daemon. However, the drawback is if it fails, it will not restart (not robust).

## How to run

Run the following command:

```bash
docker-compose up -d
```

Now, visit `localhost:8000` and you would see `Primary`.

```bash
$ docker ps
CONTAINER ID        IMAGE                                    COMMAND                  CREATED             STATUS              PORTS                    NAMES
42cc6637255b        nginxkeepaliveddockerdemo_nginx_slave    "/entrypoint.sh"         28 seconds ago      Up 24 seconds       80/tcp                   nginxkeepaliveddockerdemo_nginx_slave_1
3f39a7e356ce        haproxy:1.7-alpine                       "/docker-entrypoin..."   28 seconds ago      Up 25 seconds       0.0.0.0:8000->6301/tcp   nginxkeepaliveddockerdemo_proxy_1
6151af0d50db        nginxkeepaliveddockerdemo_nginx_master   "/entrypoint.sh"         28 seconds ago      Up 24 seconds       80/tcp                   nginxkeepaliveddockerdemo_nginx_master_1
```

Try to pause the master server:

```bash
docker pause nginxkeepaliveddockerdemo_nginx_master_1
```

Now, visit `localhost:8000` and you should see `Secondary`.

Recover the master server and pause the slave server:

```bash
docker unpause nginxkeepaliveddockerdemo_nginx_master_1
docker pause nginxkeepaliveddockerdemo_nginx_slave_1
```

Visit `localhost:8000` and you should see `Primary` again.

As you can see, when a master server is down, a backup server does automatic failover.

```bash
➜  docker-compose ps; http :8000;  docker-compose pause nginx_master; http :8000;  docker-compose ps; http :8000;
                       Name                                     Command               State           Ports
--------------------------------------------------------------------------------------------------------------------
nginx-keepalived-docker-demo-master_nginx_master_1   /entrypoint.sh                   Up      80/tcp
nginx-keepalived-docker-demo-master_nginx_slave_1    /entrypoint.sh                   Up      80/tcp
nginx-keepalived-docker-demo-master_proxy_1          /docker-entrypoint.sh hapr ...   Up      0.0.0.0:8000->6301/tcp
HTTP/1.1 200 OK
Accept-Ranges: bytes
Content-Length: 16
Content-Type: text/html
Date: Wed, 18 Sep 2019 06:15:12 GMT
ETag: "5a91328c-10"
Last-Modified: Sat, 24 Feb 2018 09:38:20 GMT
Server: nginx/1.13.5

Primary

Pausing nginx-keepalived-docker-demo-master_nginx_master_1 ... done
HTTP/1.0 504 Gateway Time-out
Cache-Control: no-cache
Connection: close
Content-Type: text/html


                       Name                                     Command               State            Ports
---------------------------------------------------------------------------------------------------------------------
nginx-keepalived-docker-demo-master_nginx_master_1   /entrypoint.sh                   Paused   80/tcp
nginx-keepalived-docker-demo-master_nginx_slave_1    /entrypoint.sh                   Up       80/tcp
nginx-keepalived-docker-demo-master_proxy_1          /docker-entrypoint.sh hapr ...   Up       0.0.0.0:8000->6301/tcp
HTTP/1.1 200 OK
Accept-Ranges: bytes
Content-Length: 18
Content-Type: text/html
Date: Wed, 18 Sep 2019 06:15:18 GMT
ETag: "5a91328c-12"
Last-Modified: Sat, 24 Feb 2018 09:38:20 GMT
Server: nginx/1.13.5

Secondary

➜  docker-compose ps; docker-compose unpause nginx_master; http :8000;  docker-compose ps;
                       Name                                     Command               State            Ports
---------------------------------------------------------------------------------------------------------------------
nginx-keepalived-docker-demo-master_nginx_master_1   /entrypoint.sh                   Paused   80/tcp
nginx-keepalived-docker-demo-master_nginx_slave_1    /entrypoint.sh                   Up       80/tcp
nginx-keepalived-docker-demo-master_proxy_1          /docker-entrypoint.sh hapr ...   Up       0.0.0.0:8000->6301/tcp
Unpausing nginx-keepalived-docker-demo-master_nginx_master_1 ... done
HTTP/1.1 200 OK
Accept-Ranges: bytes
Content-Length: 16
Content-Type: text/html
Date: Wed, 18 Sep 2019 06:15:42 GMT
ETag: "5a91328c-10"
Last-Modified: Sat, 24 Feb 2018 09:38:20 GMT
Server: nginx/1.13.5

Primary

                       Name                                     Command               State           Ports
--------------------------------------------------------------------------------------------------------------------
nginx-keepalived-docker-demo-master_nginx_master_1   /entrypoint.sh                   Up      80/tcp
nginx-keepalived-docker-demo-master_nginx_slave_1    /entrypoint.sh                   Up      80/tcp
nginx-keepalived-docker-demo-master_proxy_1          /docker-entrypoint.sh hapr ...   Up      0.0.0.0:8000->6301/tcp
```

## diff between `docker pause/unpause` and `docker stop/start`

The[`docker pause`](https://docs.docker.com/engine/reference/commandline/pause/) command suspends all processes in the specified containers. On Linux, this uses the cgroups freezer. Traditionally, when suspending a process the `SIGSTOP` signal is used, which is observable by the process being suspended

The [`docker stop`](https://docs.docker.com/engine/reference/commandline/stop/#options) command. The main process inside the container will receive `SIGTERM`, and after a grace period, `SIGKILL`.

`SIGTERM` is the termination signal. The default behavior is to terminate the process, but it also can be caught or ignored. The intention is to kill the process, gracefully or not, but to first allow it a chance to cleanup.

`SIGKILL` is the kill signal. The only behavior is to kill the process, immediately. As the process cannot catch the signal, it cannot cleanup, and thus this is a signal of last resort.

`SIGSTOP` is the pause signal. The only behavior is to pause the process; the signal cannot be caught or ignored. The shell uses pausing (and its counterpart, resuming via `SIGCONT`) to implement job control.

参考 [pause vs stop in docker]((https://stackoverflow.com/questions/51466148/pause-vs-stop-in-docker))

## docker中的权限

docker提供了下面几个参数，用于管理容器的权限：

* –cap-add: Add Linux capabilities
* –cap-drop: Drop Linux capabilities
* –privileged=false: Give extended privileges to this container
* –device=[]: Allows you to run devices inside the container without the –privileged flag.

默认情况下，docker的容器中的root的权限是有严格限制的，比如，网络管理（NET_ADMIN等很多权限都是没有的。

```bash
[root@yinye ~]# docker run -it --rm ubuntu:14.04 /bin/bash
root@fdf8fc8ecf4c:/# ip link set eth0 down
RTNETLINK answers: Operation not permitted
```

可以看到，默认情况下，在容器中进行网络管理相关操作会失败。

```bash
[root@yinye ~]# docker run --cap-add=NET_ADMIN -it --rm ubuntu:14.04 /bin/bash
root@0355d3b31934:/# ip link set eth0 down
加上NET_ADMIN便可顺利执行。
```

参考 [Docker解析：配置与权限管理](https://hustcat.github.io/docker-config-capabilities/)

## 参考文章

1. [Nginx+Keepalived实现站点高可用](https://yq.aliyun.com/articles/47355)
