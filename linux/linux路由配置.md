



```shell
# ping -I 源IP 目的IP
ping -I 10.232.32.1 10.224.21.1
```

ip route add 10.174.212.180/32 via 10.232.32.254 dev bond0 

sudo echo 10.174.212.180/32 via 10.232.32.254 dev bond0 >> /etc/sysconfig/network-scripts/bond0



新增4条路由

ip 地址

10.234.88.1  10.234.88.7 10.234.88.8 10.234.88.9 



sudo ip route add 10.234.88.1/32 via 10.232.32.254 dev bond0 

sudo ip route add 10.234.88.7/32 via 10.232.32.254 dev bond0 

sudo ip route add 10.234.88.8/32 via 10.232.32.254 dev bond0 

sudo ip route add 10.234.88.9/32 via 10.232.32.254 dev bond0 

sudo vi /etc/sysconfig/network-scripts/bond0

新增4条路由

10.234.88.1/32 via 10.232.32.254 dev bond0 

10.234.88.7/32 via 10.232.32.254 dev bond0 

10.234.88.8/32 via 10.232.32.254 dev bond0 

10.234.88.9/32 via 10.232.32.254 dev bond0 







sudo ip route add 10.168.9.127/32 via 10.232.32.254 dev bond0 

sudo ip route add 10.168.9.129/32 via 10.232.32.254 dev bond0 

sudo ip route add 10.168.103.241/32 via 10.232.32.254 dev bond0 

sudo ip route add 10.168.103.242/32 via 10.232.32.254 dev bond0 

sudo ip route add 10.168.103.243/32 via 10.232.32.254 dev bond0 

sudo ip route add 10.168.103.244/32 via 10.232.32.254 dev bond0 

sudo ip route add 10.176.9.142/32 via 10.232.32.254 dev bond0 

sudo ip route add 10.176.9.141/32 via 10.232.32.254 dev bond0 

sudo ip route add 10.176.6.29/32 via 10.232.32.254 dev bond0 

sudo ip route add 10.176.8.22/32 via 10.232.32.254 dev bond0 

sudo ip route add 10.172.6.10/32 via 10.232.32.254 dev bond0 

sudo ip route add 10.172.6.11/32 via 10.232.32.254 dev bond0 

sudo ip route add 10.172.8.15/32 via 10.232.32.254 dev bond0 

sudo ip route add 10.172.8.16/32 via 10.232.32.254 dev bond0 

sudo ip route add 10.174.66.250/32 via 10.232.32.254 dev bond0 

sudo ip route add 10.172.66.252/32 via 10.232.32.254 dev bond0 

sudo vi /etc/sysconfig/network-scripts/bond0

新增4条路由

10.222.17.5/32 via 10.232.32.254 dev bond0 

10.222.17.6/32 via 10.232.32.254 dev bond0 

10.182.94.4/32 via 10.232.32.254 dev bond0 

10.182.94.5/32 via 10.232.32.254 dev bond0 

10.182.94.6/32 via 10.232.32.254 dev bond0 







sudo ip route add 10.180.30.70/32 via 10.232.32.254 dev bond0 

sudo ip route add 10.180.30.71/32 via 10.232.32.254 dev bond0 

sudo ip route add 10.180.30.72/32 via 10.232.32.254 dev bond0 

sudo vi /etc/sysconfig/network-scripts/bond0

新增4条路由

10.180.30.70/32 via 10.232.32.254 dev bond0 

10.180.30.71/32 via 10.232.32.254 dev bond0 

10.180.30.72/32 via 10.232.32.254 dev bond0 





