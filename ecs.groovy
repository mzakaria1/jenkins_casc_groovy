import java.util.Arrays
import java.util.logging.Logger
Logger logger = Logger.getLogger("ecs-cluster")

logger.info("Loading Jenkins")
import jenkins.model.*
instance = Jenkins.getInstance()

import com.cloudbees.jenkins.plugins.amazonecs.*
ECSCloud.metaClass.properties.each {println it.name+":\t"+it.type }

import com.cloudbees.hudson.plugins.folder.*

import com.cloudbees.jenkins.plugins.amazonecs.ECSTaskTemplate.MountPointEntry
def mounts = Arrays.asList(
  new MountPointEntry(
    name="jenkins",
    sourcePath="/home/jenkins",
    containerPath="/home/jenkins",
    readOnly=false),
)

logger.info("Creating template")
import com.cloudbees.jenkins.plugins.amazonecs.ECSTaskTemplate
def ecsTemplate = new ECSTaskTemplate(
  templateName="jnlp-slave",
  label="ecs-with-docker",
  image="jnlp-slave-with-docker:latest",
  remoteFSRoot=null,
  memory=2048,
  cpu=512,
  privileged=false,
  logDriverOptions=null,
  environments=null,
  extraHosts=null,
  mountPoints=mounts
)

logger.info("Retrieving ecs cloud config by descriptor")
import com.cloudbees.jenkins.plugins.amazonecs.ECSCloud
ecsCloud = new ECSCloud(
  name="name",
  templates=Arrays.asList(ecsTemplate),
  credentialsId=null,
  cluster="arn:aws:ecs:us-east-1:123456789:cluster/ecs-jenkins-slave",
  regionName="us-east-1",
  jenkinsUrl="http://34.220.126.142:8080",
  slaveTimoutInSeconds=60
)

logger.info("Gettings clouds")
def clouds = instance.clouds
clouds.add(ecsCloud)
logger.info("Saving jenkins")
instance.save()