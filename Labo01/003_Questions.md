**What is the smallest and the biggest instance type (in terms of virtual CPUs and memory) that you can choose from when creating an instance?**

```
The smallest instance type is t2.nano with 1 vCPU and 0.5 GB of memory.
The biggest instance type are the u-24tb1.112xlarge and u-24tb1.metal with 448
vCPUs and 24 TB of memory.
```

Sources:

- [AWS EC2 Instance Types](https://aws.amazon.com/ec2/instance-types/)

- [AWS EC2 High Memory Instances](https://aws.amazon.com/ec2/instance-types/high-memory/)


**How long did it take for the new instance to get into the _running_ state?**

``````
The setup process typically lasted between 10 to 20 seconds. It's worth noting that this timeframe can fluctuate based on factors such as geographical region, the type of instance utilized, the operating system (Linux setups are generally quicker than Windows), and the current workload on EC2.
``````

Sources:

- [EC2 Instance lifecycle](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/ec2-instance-lifecycle.html)

- [Blog comparing launch-times](https://www.martysweet.co.uk/ec2-launch-times/)

**Using the commands to explore the machine listed earlier, respond to the following questions and explain how you came to the answer:**

**What's the difference between time here in Switzerland and the time set on**
**the machine?**

On Linux, the time zone can be querried by running either `date`.

Running the aforementioned commands on our local host, we get 'CET'.
Running the command on the EC2 instance, we get 'UTC'.

All Linux instances are set to UTC time by default. Depending on the season, Switzerland
uses CET or CEST. This translate to a 1 to 2 hours difference between the instance's
time and the local time.

Sources:

- [Set the time for your Linux instance](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/set-time.html)

**What's the name of the hypervisor?**

```
cat /sys/hypervisor/type
xen
```

**How much free space does the disk have?**

```
df -h
Filesystem      Size  Used Avail Use% Mounted on
udev            476M     0  476M   0% /dev
tmpfs            98M  500K   97M   1% /run
/dev/xvda1      7.7G  1.4G  5.9G  19% /
tmpfs           488M     0  488M   0% /dev/shm
tmpfs           5.0M     0  5.0M   0% /run/lock
/dev/xvda15     124M   12M  113M  10% /boot/efi
tmpfs            98M     0   98M   0% /run/user/1019
tmpfs            98M     0   98M   0% /run/user/1009
tmpfs            98M     0   98M   0% /run/user/1007
```

**Try to ping the instance ssh srv from your local machine. What do you see? Explain. Change the configuration to make it work. Ping the instance, record 5 round-trip times.**

We cannot ping ping the instance, it's due to to security group settings that does not allow ICMP traffic. After allowing ICMP traffic in the security group.
After changing the settings, the ping command work.

```
bitnami@ip-10-0-11-10:~$ ping 10.0.0.5 -c 5

PING 10.0.0.5 (10.0.0.5) 56(84) bytes of data.

--- 10.0.0.5 ping statistics ---

5 packets transmitted, 0 received, 100% packet loss, time 4092ms
```

```
devopsteam07@ip-10-0-0-5:~$ ping 10.0.11.10 -c 5
PING 10.0.7.10 (10.0.7.10) 56(84) bytes of data.
64 bytes from 10.0.11.10: icmp_seq=1 ttl=64 time=0.362 ms
64 bytes from 10.0.11.10: icmp_seq=2 ttl=64 time=0.324 ms
64 bytes from 10.0.11.10: icmp_seq=3 ttl=64 time=0.341 ms
64 bytes from 10.0.11.10: icmp_seq=4 ttl=64 time=0.337 ms
64 bytes from 10.0.11.10: icmp_seq=5 ttl=64 time=0.339 ms


--- 10.0.11.10 ping statistics ---
5 packets transmitted, 5 received, 0% packet loss, time 4081ms
rtt min/avg/max/mdev = 0.324/0.340/0.362/0.012 ms
```

**Determine the IP address seen by the operating system in the EC2 instance by running the `ifconfig` command. What type of address is it? Compare it to the address displayed by the ping command earlier. How do you explain that you can successfully communicate with the machine?**

```ifconfig``` doesn't work ```ip add``` to find the internal IP address of the EC2 instance.

```
devopsteam11@ip-10-0-0-5:~$ ip add
1: lo: <LOOPBACK,UP,LOWER_UP> mtu 65536 qdisc noqueue state UNKNOWN group default qlen 1000
    link/loopback 00:00:00:00:00:00 brd 00:00:00:00:00:00
    inet 127.0.0.1/8 scope host lo
       valid_lft forever preferred_lft forever
    inet6 ::1/128 scope host noprefixroute
       valid_lft forever preferred_lft forever
2: enX0: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 9001 qdisc fq_codel state UP group default qlen 1000
    link/ether 06:d9:0b:3d:1a:85 brd ff:ff:ff:ff:ff:ff
    inet 10.0.0.5/28 metric 100 brd 10.0.0.15 scope global dynamic enX0
       valid_lft 2414sec preferred_lft 2414sec
    inet6 fe80::4d9:bff:fe3d:1a85/64 scope link
       valid_lft forever preferred_lft forever
```

```
bitnami@ip-10-0-11-10:~$ ip add
1: lo: <LOOPBACK,UP,LOWER_UP> mtu 65536 qdisc noqueue state UNKNOWN group default qlen 1000
    link/loopback 00:00:00:00:00:00 brd 00:00:00:00:00:00
    inet 127.0.0.1/8 scope host lo
       valid_lft forever preferred_lft forever
    inet6 ::1/128 scope host
       valid_lft forever preferred_lft forever
2: ens5: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 9001 qdisc mq state UP group default qlen 1000
    link/ether 06:c7:cd:21:bf:ff brd ff:ff:ff:ff:ff:ff
    altname enp0s5
    inet 10.0.11.10/28 brd 10.0.11.15 scope global dynamic ens5
       valid_lft 2358sec preferred_lft 2358sec
    inet6 fe80::4c7:cdff:fe21:bfff/64 scope link
       valid_lft forever preferred_lft forever
```
