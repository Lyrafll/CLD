### Deploy the elastic load balancer

In this task you will create a load balancer in AWS that will receive
the HTTP requests from clients and forward them to the Drupal
instances.

![Schema](./img/CLD_AWS_INFA.PNG)

## Task 01 Prerequisites for the ELB

* Create a dedicated security group

|Key|Value|
|:--|:--|
|Name|SG-DEVOPSTEAM[XX]-LB|
|Inbound Rules|Application Load Balancer|
|Outbound Rules|Refer to the infra schema|

```bash
[INPUT]
aws ec2 create-security-group \
    --group-name SG-DEVOPSTEAM11-LB \
    --description "Security group for the ELB" \
    --vpc-id vpc-03d46c285a2af77ba \
    --tag-specifications 'ResourceType=security-group,Tags=[{Key=Name,Value=SG-DEVOPSTEAM11-LB}]'

[OUTPUT]
{
    "GroupId": "sg-058134b41a33889e6",
    "Tags": [
        {
            "Key": "Name",
            "Value": "SG-DEVOPSTEAM11-LB"
        }
    ]
}

[INPUT]
aws ec2 authorize-security-group-ingress \
    --group-id sg-058134b41a33889e6 \
    --protocol tcp \
    --port 8080 \
    --cidr 10.0.0.0/28
    
[OUTPUT]
{
    "Return": true,
    "SecurityGroupRules": [
        {
            "SecurityGroupRuleId": "sgr-0e5f18be54a6742b9",
            "GroupId": "sg-058134b41a33889e6",
            "GroupOwnerId": "709024702237",
            "IsEgress": false,
            "IpProtocol": "tcp",
            "FromPort": 8080,
            "ToPort": 8080,
            "CidrIpv4": "10.0.0.0/28"
        }
    ]
}
```

* Create the Target Group

|Key|Value|
|:--|:--|
|Target type|Instances|
|Name|TG-DEVOPSTEAM[XX]|
|Protocol and port|Refer to the infra schema|
|Ip Address type|IPv4|
|VPC|Refer to the infra schema|
|Protocol version|HTTP1|
|Health check protocol|HTTP|
|Health check path|/|
|Port|Traffic port|
|Healthy threshold|2 consecutive health check successes|
|Unhealthy threshold|2 consecutive health check failures|
|Timeout|5 seconds|
|Interval|10 seconds|
|Success codes|200|

```bash
[INPUT]
aws elbv2 create-target-group \
    --vpc-id vpc-03d46c285a2af77ba \
    --name TG-DEVOPSTEAM11 \
    --target-type instance \
    --protocol HTTP \
    --port 8080 \
    --ip-address-type ipv4 \
    --protocol-version HTTP1 \
    --health-check-protocol HTTP \
    --health-check-port traffic-port \
    --health-check-path / \
    --health-check-enabled \
    --health-check-interval-seconds 10 \
    --health-check-timeout-seconds 5 \
    --healthy-threshold-count 2 \
    --unhealthy-threshold-count 2 \
    --matcher HttpCode=200    
    --tags Key=Name,Value=TG-DEVOPSTEAM11


[OUTPUT]
{
    "TargetGroups": [
        {
            "TargetGroupArn": "arn:aws:elasticloadbalancing:eu-west-3:709024702237:targetgroup/TG-DEVOPSTEAM11/90cb43924e683a66",
            "TargetGroupName": "TG-DEVOPSTEAM11",
            "Protocol": "HTTP",
            "Port": 8080,
            "VpcId": "vpc-03d46c285a2af77ba",
            "HealthCheckProtocol": "HTTP",
            "HealthCheckPort": "traffic-port",
            "HealthCheckEnabled": true,
            "HealthCheckIntervalSeconds": 10,
            "HealthCheckTimeoutSeconds": 5,
            "HealthyThresholdCount": 2,
            "UnhealthyThresholdCount": 2,
            "HealthCheckPath": "/",
            "Matcher": {
                "HttpCode": "200"
            },
            "TargetType": "instance",
            "ProtocolVersion": "HTTP1",
            "IpAddressType": "ipv4"
        }
    ]
}

[INPUT]
aws elbv2 register-targets \
    --target-group-arn arn:aws:elasticloadbalancing:eu-west-3:709024702237:targetgroup/TG-DEVOPSTEAM11/90cb43924e683a66 \
    --targets Id=i-06b723229daa766c4 Id=i-03149d6d8ac989c16

[NO OUTPUT]
```


## Task 02 Deploy the Load Balancer

[Source](https://aws.amazon.com/elasticloadbalancing/)

* Create the Load Balancer

|Key|Value|
|:--|:--|
|Type|Application Load Balancer|
|Name|ELB-DEVOPSTEAM99|
|Scheme|Internal|
|Ip Address type|IPv4|
|VPC|Refer to the infra schema|
|Security group|Refer to the infra schema|
|Listeners Protocol and port|Refer to the infra schema|
|Target group|Your own target group created in task 01|

Provide the following answers (leave any
field not mentioned at its default value):

```bash
[INPUT]
aws elbv2 create-load-balancer \
    --type application \
    --name ELB-DEVOPSTEAM11 \
    --scheme internal \
    --ip-address-type ipv4 \
    --subnets subnet-0d8a7c4a04c59189d subnet-038104e68d83eeda0 \
    --security-groups sg-058134b41a33889e6

[OUTPUT]
{
    "LoadBalancers": [
        {
            "LoadBalancerArn": "arn:aws:elasticloadbalancing:eu-west-3:709024702237:loadbalancer/app/ELB-DEVOPSTEAM11/04fd2be8d0270c3c",
            "DNSName": "internal-ELB-DEVOPSTEAM11-974715717.eu-west-3.elb.amazonaws.com",
            "CanonicalHostedZoneId": "Z3Q77PNBQS71R4",
            "CreatedTime": "2024-03-28T14:46:59.290000+00:00",
            "LoadBalancerName": "ELB-DEVOPSTEAM11",
            "Scheme": "internal",
            "VpcId": "vpc-03d46c285a2af77ba",
            "State": {
                "Code": "provisioning"
            },
            "Type": "application",
            "AvailabilityZones": [
                {
                    "ZoneName": "eu-west-3b",
                    "SubnetId": "subnet-038104e68d83eeda0",
                    "LoadBalancerAddresses": []
                },
                {
                    "ZoneName": "eu-west-3a",
                    "SubnetId": "subnet-0d8a7c4a04c59189d",
                    "LoadBalancerAddresses": []
                }
            ],
            "SecurityGroups": [
                "sg-058134b41a33889e6"
            ],
            "IpAddressType": "ipv4"
        }
    ]
}

[INPUT]
aws elbv2 create-listener \
    --load-balancer-arn arn:aws:elasticloadbalancing:eu-west-3:709024702237:loadbalancer/app/ELB-DEVOPSTEAM11/04fd2be8d0270c3c \
    --protocol HTTP \
    --port 8080 \
    --default-actions Type=forward,TargetGroupArn=arn:aws:elasticloadbalancing:eu-west-3:709024702237:targetgroup/TG-DEVOPSTEAM11/90cb43924e683a66

[OUTPUT]
{
    "Listeners": [
        {
            "ListenerArn": "arn:aws:elasticloadbalancing:eu-west-3:709024702237:listener/app/ELB-DEVOPSTEAM11/04fd2be8d0270c3c/106fd9f82ac38dab",
            "LoadBalancerArn": "arn:aws:elasticloadbalancing:eu-west-3:709024702237:loadbalancer/app/ELB-DEVOPSTEAM11/04fd2be8d0270c3c",
            "Port": 8080,
            "Protocol": "HTTP",
            "DefaultActions": [
                {
                    "Type": "forward",
                    "TargetGroupArn": "arn:aws:elasticloadbalancing:eu-west-3:709024702237:targetgroup/TG-DEVOPSTEAM11/90cb43924e683a66",
                    "ForwardConfig": {
                        "TargetGroups": [
                            {
                                "TargetGroupArn": "arn:aws:elasticloadbalancing:eu-west-3:709024702237:targetgroup/TG-DEVOPSTEAM11/90cb43924e683a66",
                                "Weight": 1
                            }
                        ],
                        "TargetGroupStickinessConfig": {
                            "Enabled": false
                        }
                    }
                }
            ]
        }
    ]
}
```

### Update Security group
note this step is for allowing EC2 instance to communicate with ELB

```bash
[INPUT]
aws ec2 authorize-security-group-ingress \
    --group-id sg-09866346f8d32d27d \
    --protocol tcp \
    --port 8080 \
    --cidr 10.0.11.0/28

[OUTPUT]
{
    "Return": true,
    "SecurityGroupRules": [
        {
            "SecurityGroupRuleId": "sgr-05669ef01f2c7427f",
            "GroupId": "sg-09866346f8d32d27d",
            "GroupOwnerId": "709024702237",
            "IsEgress": false,
            "IpProtocol": "tcp",
            "FromPort": 8080,
            "ToPort": 8080,
            "CidrIpv4": "10.0.11.0/28"
        }
    ]
}


[INPUT]
aws ec2 authorize-security-group-ingress \
    --group-id sg-09866346f8d32d27d \
    --protocol tcp \
    --port 8080 \
    --cidr 10.0.11.128/28

[OUTPUT]
{
    "Return": true,
    "SecurityGroupRules": [
        {
            "SecurityGroupRuleId": "sgr-067cba5c31f9f704a",
            "GroupId": "sg-09866346f8d32d27d",
            "GroupOwnerId": "709024702237",
            "IsEgress": false,
            "IpProtocol": "tcp",
            "FromPort": 8080,
            "ToPort": 8080,
            "CidrIpv4": "10.0.11.128/28"
        }
    ]
}
```


* Get the ELB FQDN (DNS NAME - A Record)

```bash
[INPUT]
aws elbv2 describe-load-balancers --names ELB-DEVOPSTEAM11 --query 'LoadBalancers[].DNSName'

[OUTPUT]
[
    "internal-ELB-DEVOPSTEAM11-974715717.eu-west-3.elb.amazonaws.com"
]
```

* Get the ELB deployment status

Note : In the EC2 console select the Target Group. In the
       lower half of the panel, click on the **Targets** tab. Watch the
       status of the instance go from **unused** to **initial**.

* Ask the DMZ administrator to register your ELB with the reverse proxy via the private teams channel

* Update your string connection to test your ELB and test it

```bash
//connection string updated
ssh devopsteam11@15.188.43.46 -i "C:\Users\aurel\.ssh\CLD_KEY_DMZ_DEVOPSTEAM11.pem" \
-L 2230::internal-ELB-DEVOPSTEAM11-974715717.eu-west-3.elb.amazonaws.com:8080
```

* Test your application through your ssh tunneling

```bash
[INPUT]
curl localhost:2230

[OUTPUT]
HTTP/1.1 200 OK
Date: Thu, 28 Mar 2024 16:20:25 GMT
Content-Type: text/html; charset=UTF-8
Content-Length: 16554
Connection: keep-alive
Server: Apache
Cache-Control: must-revalidate, no-cache, private
X-Drupal-Dynamic-Cache: MISS
Content-language: en
X-Content-Type-Options: nosniff
X-Frame-Options: SAMEORIGIN
Expires: Sun, 19 Nov 1978 05:00:00 GMT
X-Generator: Drupal 10 (https://www.drupal.org)
X-Drupal-Cache: MISS
```

#### Questions - Analysis

* On your local machine resolve the DNS name of the load balancer into
  an IP address using the `nslookup` command (works on Linux, macOS and Windows). Write
  the DNS name and the resolved IP Address(es) into the report.

```
nslookup internal-ELB-DEVOPSTEAM11-974715717.eu-west-3.elb.amazonaws.com
[OUTPUT]
(...)
Name:	internal-ELB-DEVOPSTEAM11-974715717.eu-west-3.elb.amazonaws.com
Address: 10.0.11.132
Name:	internal-ELB-DEVOPSTEAM11-974715717.eu-west-3.elb.amazonaws.com
Address: 10.0.11.6

```

* From your Drupal instance, identify the ip from which requests are sent by the Load Balancer.

Help : execute `tcpdump port 8080`

```
16:41:01.191980 IP 10.0.11.10.http-alt > 10.0.11.132.59424: Flags [.], ack 132, win 489, options [nop,nop,TS val 1352906535 ecr 416587998], length 0
16:41:01.192295 IP 10.0.11.132.59424 > 10.0.11.10.http-alt: Flags [.], ack 5624, win 175, options [nop,nop,TS val 416587999 ecr 1352906534], length 0
```
We can see that the request is sent from 10.0.11.10, which is in availability zone eu-west-3a, and 10.0.11.132, which is in availability zone eu-west-3b. As we use Application Load Balancer we will perform cross-zone load balancing.

* In the Apache access log identify the health check accesses from the
  load balancer and copy some samples into the report.

```
cat /opt/bitnami/apache/logs/access_log

10.0.11.6 - - [28/Mar/2024:16:43:31 +0000] "GET / HTTP/1.1" 200 5147
10.0.11.132 - - [28/Mar/2024:16:43:41 +0000] "GET / HTTP/1.1" 200 5147
10.0.11.6 - - [28/Mar/2024:16:43:41 +0000] "GET / HTTP/1.1" 200 5147
10.0.11.132 - - [28/Mar/2024:16:43:51 +0000] "GET / HTTP/1.1" 200 5147
10.0.11.6 - - [28/Mar/2024:16:43:51 +0000] "GET / HTTP/1.1" 200 5147
```
