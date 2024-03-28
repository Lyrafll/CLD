# Custom AMI and Deploy the second Drupal instance

In this task you will update your AMI with the Drupal settings and deploy it in the second availability zone.

## Task 01 - Create AMI

### [Create AMI](https://awscli.amazonaws.com/v2/documentation/api/latest/reference/ec2/create-image.html)

Note : stop the instance before

|Key|Value for GUI Only|
|:--|:--|
|Name|AMI_DRUPAL_DEVOPSTEAM[XX]_LABO02_RDS|
|Description|Same as name value|

```bash
[INPUT]

aws ec2 create-image \
    --instance-id i-033f6122a15ed98cb \
    --name "AMI_DRUPAL_DEVOPSTEAM11_LABO02_RDS" \
    --description "AMI_DRUPAL_DEVOPSTEAM11_LABO02_RDS" \
    --tag-specifications ResourceType=image,Tags=[{Key=Name,Value=AMI_DRUPAL_DEVOPSTEAM11_LABO02_RDS}]

[OUTPUT]

{
    "ImageId": "ami-0d2fbe56c551919d6"
}
```

## Task 02 - Deploy Instances

* Restart Drupal Instance in Az1

* Deploy Drupal Instance based on AMI in Az2

|Key|Value for GUI Only|
|:--|:--|
|Name|EC2_PRIVATE_DRUPAL_DEVOPSTEAM[XX]_B|
|Description|Same as name value|

```bash
[INPUT]
aws ec2 run-instances \
   --image-id ami-0d2fbe56c551919d6 \
   --count 1 \
   --instance-type t3.micro \
   --key-name CLD_KEY_DRUPAL_DEVOPSTEAM11 \
   --private-ip-address 10.0.11.140 \
   --security-group-ids sg-09866346f8d32d27d \
   --subnet-id subnet-038104e68d83eeda0 \
   --tag-specifications ResourceType=instance,Tags=[{Key=Name,Value=EC2_PRIVATE_DRUPAL_DEVOPSTEAM11_B}] \
   --placement AvailabilityZone=eu-west-3b

[OUTPUT]
{
    "Groups": [],
    "Instances": [
        {
            "AmiLaunchIndex": 0,
            "ImageId": "ami-0d2fbe56c551919d6",
            "InstanceId": "i-03149d6d8ac989c16",
            "InstanceType": "t3.micro",
            "KeyName": "CLD_KEY_DRUPAL_DEVOPSTEAM11",
            "LaunchTime": "2024-03-28T14:04:19+00:00",
            "Monitoring": {
                "State": "disabled"
            },
            "Placement": {
                "AvailabilityZone": "eu-west-3b",
                "GroupName": "",
                "Tenancy": "default"
            },
            "PrivateDnsName": "ip-10-0-11-140.eu-west-3.compute.internal",
            "PrivateIpAddress": "10.0.11.140",
            "ProductCodes": [],
            "PublicDnsName": "",
            "State": {
                "Code": 0,
                "Name": "pending"
            },
            "StateTransitionReason": "",
            "SubnetId": "subnet-038104e68d83eeda0",
            "VpcId": "vpc-03d46c285a2af77ba",
            "Architecture": "x86_64",
            "BlockDeviceMappings": [],
            "ClientToken": "fba3d92f-d50f-4ed5-988b-51f5ae3418ea",
            "EbsOptimized": false,
            "EnaSupport": true,
            "Hypervisor": "xen",
            "NetworkInterfaces": [
                {
                    "Attachment": {
                        "AttachTime": "2024-03-28T14:04:19+00:00",
                        "AttachmentId": "eni-attach-05c15cbad68b1d52a",
                        "DeleteOnTermination": true,
                        "DeviceIndex": 0,
                        "Status": "attaching",
                        "NetworkCardIndex": 0
                    },
                    "Description": "",
                    "Groups": [
                        {
                            "GroupName": "CLD-SG-DEVOPSTREAM11",
                            "GroupId": "sg-09866346f8d32d27d"
                        }
                    ],
                    "Ipv6Addresses": [],
                    "MacAddress": "0a:d0:63:97:9a:71",
                    "NetworkInterfaceId": "eni-073edee3c60558c90",
                    "OwnerId": "709024702237",
                    "PrivateIpAddress": "10.0.11.140",
                    "PrivateIpAddresses": [
                        {
                            "Primary": true,
                            "PrivateIpAddress": "10.0.11.140"
                        }
                    ],
                    "SourceDestCheck": true,
                    "Status": "in-use",
                    "SubnetId": "subnet-038104e68d83eeda0",
                    "VpcId": "vpc-03d46c285a2af77ba",
                    "InterfaceType": "interface"
                }
            ],
            "RootDeviceName": "/dev/xvda",
            "RootDeviceType": "ebs",
            "SecurityGroups": [
                {
                    "GroupName": "CLD-SG-DEVOPSTREAM11",
                    "GroupId": "sg-09866346f8d32d27d"
                }
            ],
            "SourceDestCheck": true,
            "StateReason": {
                "Code": "pending",
                "Message": "pending"
            },
            "Tags": [
                {
                    "Key": "Name",
                    "Value": "EC2_PRIVATE_DRUPAL_DEVOPSTEAM11_B"
                }
            ],
            "VirtualizationType": "hvm",
            "CpuOptions": {
                "CoreCount": 1,
                "ThreadsPerCore": 2
            },
            "CapacityReservationSpecification": {
                "CapacityReservationPreference": "open"
            },
            "MetadataOptions": {
                "State": "pending",
                "HttpTokens": "optional",
                "HttpPutResponseHopLimit": 1,
                "HttpEndpoint": "enabled",
                "HttpProtocolIpv6": "disabled",
                "InstanceMetadataTags": "disabled"
            },
            "EnclaveOptions": {
                "Enabled": false
            },
            "PrivateDnsNameOptions": {
                "HostnameType": "ip-name",
                "EnableResourceNameDnsARecord": false,
                "EnableResourceNameDnsAAAARecord": false
            },
            "MaintenanceOptions": {
                "AutoRecovery": "default"
            },
            "CurrentInstanceBootMode": "legacy-bios"
        }
    ],
    "OwnerId": "709024702237",
    "ReservationId": "r-0941226a9766c7252"
}
```

## Task 03 - Test the connectivity

### Update your ssh connection string to test

* add tunnels for ssh and http pointing on the B Instance

```bash
ssh devopsteam11@15.188.43.46  -i "C:\Users\aurel\.ssh\CLD_KEY_DMZ_DEVOPSTEAM11.pem" -L 2225:10.0.11.10:22 -L 2226:10.0.11.140:22 -L 2230:10.0.11.10:8080 -L 2231:10.0.11.10:8080

ssh bitnami@localhost -p 2226 -i "C:\Users\aurel\.ssh\CLD_KEY_DRUPAL_DEVOPSTEAM11.pem"
```

## Check SQL Accesses

```sql
[INPUT]
//sql string connection from A

bitnami@ip-10-0-11-10:~$ mariadb -h dbi-devopsteam11.cshki92s4w5p.eu-west-3.rds.amazonaws.com -u bn_drupal -p'[PASSWORD]' -e "SHOW DATABASES;"

[OUTPUT]
+--------------------+
| Database           |
+--------------------+
| bitnami_drupal     |
| information_schema |
+--------------------+
```

```sql
[INPUT]
//sql string connection from B

bitnami@ip-10-0-11-140:~$ mariadb -h dbi-devopsteam11.cshki92s4w5p.eu-west-3.rds.amazonaws.com -u bn_drupal -p'[PASSWORD]' -e "SHOW DATABASES;"

[OUTPUT]
+--------------------+
| Database           |
+--------------------+
| bitnami_drupal     |
| information_schema |
+--------------------+
```

### Check HTTP Accesses

```bash
curl -I http://localhost:2230
curl -I http://localhost:2231
```

### Read and write test through the web app

* Login in both webapps (same login)

* Change the users' email address on a webapp... refresh the user's profile page on the second and validated that they are communicating with the same db (rds).

* Observations ?

```
Changes are reflected on both webapps, as the informations is stored in the same db, so it is shared
```

### Change the profil picture

* Observations ?

```
The profile picture is sadly stored on the instances itself, so it is not shared.
```