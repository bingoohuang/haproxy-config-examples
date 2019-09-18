# keepalived-nginx-master-master

```bash
➜  keepalived-nginx-master-master git:(master) ✗ docker-compose up -d --build
➜  keepalived-nginx-master-master git:(master) ✗ docker-compose ps; http :8000; http :8001;

                    Name                                   Command               State                       Ports
---------------------------------------------------------------------------------------------------------------------------------------
keepalived-nginx-master-master_nginx_master_1   /entrypoint.sh                   Up      80/tcp
keepalived-nginx-master-master_nginx_slave_1    /entrypoint.sh                   Up      80/tcp
keepalived-nginx-master-master_proxy_1          /docker-entrypoint.sh hapr ...   Up      0.0.0.0:8000->6301/tcp, 0.0.0.0:8001->6302/tcp
HTTP/1.1 200 OK
Accept-Ranges: bytes
Content-Length: 7
Content-Type: text/html
Date: Wed, 18 Sep 2019 08:54:43 GMT
ETag: "5d81ee9e-7"
Last-Modified: Wed, 18 Sep 2019 08:45:18 GMT
Server: nginx/1.13.5

Primary

HTTP/1.1 200 OK
Accept-Ranges: bytes
Content-Length: 9
Content-Type: text/html
Date: Wed, 18 Sep 2019 08:54:43 GMT
ETag: "5d81eea4-9"
Last-Modified: Wed, 18 Sep 2019 08:45:24 GMT
Server: nginx/1.13.5

Secondary

➜  keepalived-nginx-master-master git:(master) ✗ docker-compose pause nginx_master; http :8000; http :8001;  docker-compose ps; http :8000; http :8001;

Pausing keepalived-nginx-master-master_nginx_master_1 ... done
HTTP/1.0 504 Gateway Time-out
Cache-Control: no-cache
Connection: close
Content-Type: text/html


HTTP/1.1 200 OK
Accept-Ranges: bytes
Content-Length: 9
Content-Type: text/html
Date: Wed, 18 Sep 2019 08:54:53 GMT
ETag: "5d81eea4-9"
Last-Modified: Wed, 18 Sep 2019 08:45:24 GMT
Server: nginx/1.13.5

Secondary

                    Name                                   Command               State                        Ports
----------------------------------------------------------------------------------------------------------------------------------------
keepalived-nginx-master-master_nginx_master_1   /entrypoint.sh                   Paused   80/tcp
keepalived-nginx-master-master_nginx_slave_1    /entrypoint.sh                   Up       80/tcp
keepalived-nginx-master-master_proxy_1          /docker-entrypoint.sh hapr ...   Up       0.0.0.0:8000->6301/tcp, 0.0.0.0:8001->6302/tcp
HTTP/1.1 200 OK
Accept-Ranges: bytes
Content-Length: 9
Content-Type: text/html
Date: Wed, 18 Sep 2019 08:54:54 GMT
ETag: "5d81eea4-9"
Last-Modified: Wed, 18 Sep 2019 08:45:24 GMT
Server: nginx/1.13.5

Secondary

HTTP/1.1 200 OK
Accept-Ranges: bytes
Content-Length: 9
Content-Type: text/html
Date: Wed, 18 Sep 2019 08:54:54 GMT
ETag: "5d81eea4-9"
Last-Modified: Wed, 18 Sep 2019 08:45:24 GMT
Server: nginx/1.13.5

Secondary

➜  keepalived-nginx-master-master git:(master) ✗ docker-compose unpause nginx_master; http :8000; http :8001; docker-compose ps;

Unpausing keepalived-nginx-master-master_nginx_master_1 ... done
HTTP/1.1 200 OK
Accept-Ranges: bytes
Content-Length: 7
Content-Type: text/html
Date: Wed, 18 Sep 2019 08:55:13 GMT
ETag: "5d81ee9e-7"
Last-Modified: Wed, 18 Sep 2019 08:45:18 GMT
Server: nginx/1.13.5

Primary

HTTP/1.1 200 OK
Accept-Ranges: bytes
Content-Length: 9
Content-Type: text/html
Date: Wed, 18 Sep 2019 08:55:13 GMT
ETag: "5d81eea4-9"
Last-Modified: Wed, 18 Sep 2019 08:45:24 GMT
Server: nginx/1.13.5

Secondary

                    Name                                   Command               State                       Ports
---------------------------------------------------------------------------------------------------------------------------------------
keepalived-nginx-master-master_nginx_master_1   /entrypoint.sh                   Up      80/tcp
keepalived-nginx-master-master_nginx_slave_1    /entrypoint.sh                   Up      80/tcp
keepalived-nginx-master-master_proxy_1          /docker-entrypoint.sh hapr ...   Up      0.0.0.0:8000->6301/tcp, 0.0.0.0:8001->6302/tcp
➜  keepalived-nginx-master-master git:(master) ✗ docker-compose pause nginx_slave; http :8000; http :8001; docker-compose ps; http :8000; http :8001;

Pausing keepalived-nginx-master-master_nginx_slave_1 ... done
HTTP/1.1 200 OK
Accept-Ranges: bytes
Content-Length: 7
Content-Type: text/html
Date: Wed, 18 Sep 2019 08:55:19 GMT
ETag: "5d81ee9e-7"
Last-Modified: Wed, 18 Sep 2019 08:45:18 GMT
Server: nginx/1.13.5

Primary

HTTP/1.0 504 Gateway Time-out
Cache-Control: no-cache
Connection: close
Content-Type: text/html


                    Name                                   Command               State                        Ports
----------------------------------------------------------------------------------------------------------------------------------------
keepalived-nginx-master-master_nginx_master_1   /entrypoint.sh                   Up       80/tcp
keepalived-nginx-master-master_nginx_slave_1    /entrypoint.sh                   Paused   80/tcp
keepalived-nginx-master-master_proxy_1          /docker-entrypoint.sh hapr ...   Up       0.0.0.0:8000->6301/tcp, 0.0.0.0:8001->6302/tcp
HTTP/1.1 200 OK
Accept-Ranges: bytes
Content-Length: 7
Content-Type: text/html
Date: Wed, 18 Sep 2019 08:55:25 GMT
ETag: "5d81ee9e-7"
Last-Modified: Wed, 18 Sep 2019 08:45:18 GMT
Server: nginx/1.13.5

Primary

HTTP/1.1 200 OK
Accept-Ranges: bytes
Content-Length: 7
Content-Type: text/html
Date: Wed, 18 Sep 2019 08:55:25 GMT
ETag: "5d81ee9e-7"
Last-Modified: Wed, 18 Sep 2019 08:45:18 GMT
Server: nginx/1.13.5

Primary

➜  keepalived-nginx-master-master git:(master) ✗ docker-compose unpause nginx_slave; http :8000; http :8001; docker-compose ps;

Unpausing keepalived-nginx-master-master_nginx_slave_1 ... done
HTTP/1.1 200 OK
Accept-Ranges: bytes
Content-Length: 7
Content-Type: text/html
Date: Wed, 18 Sep 2019 08:55:29 GMT
ETag: "5d81ee9e-7"
Last-Modified: Wed, 18 Sep 2019 08:45:18 GMT
Server: nginx/1.13.5

Primary

HTTP/1.1 200 OK
Accept-Ranges: bytes
Content-Length: 9
Content-Type: text/html
Date: Wed, 18 Sep 2019 08:55:30 GMT
ETag: "5d81eea4-9"
Last-Modified: Wed, 18 Sep 2019 08:45:24 GMT
Server: nginx/1.13.5

Secondary

                    Name                                   Command               State                       Ports
---------------------------------------------------------------------------------------------------------------------------------------
keepalived-nginx-master-master_nginx_master_1   /entrypoint.sh                   Up      80/tcp
keepalived-nginx-master-master_nginx_slave_1    /entrypoint.sh                   Up      80/tcp
keepalived-nginx-master-master_proxy_1          /docker-entrypoint.sh hapr ...   Up      0.0.0.0:8000->6301/tcp, 0.0.0.0:8001->6302/tcp
```

## 参考文章

1. [keepalived双实例配置](https://blog.51cto.com/jiayimeng/1896830)


docker-compose ps; http :8000; http :8001;
docker-compose pause nginx_master; http :8000; http :8001; docker-compose ps; http :8000; http :8001;
docker-compose unpause nginx_master; http :8000; http :8001; docker-compose ps;

docker-compose pause nginx_slave; http :8000; http :8001; docker-compose ps; http :8000; http :8001;
docker-compose unpause nginx_slave; http :8000; http :8001; docker-compose ps;

