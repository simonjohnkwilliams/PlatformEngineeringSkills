#update system
sudo apt update
sudo apt upgrade
#install java
sudo apt install openjdk-17-jre-headless -y
#create a nexus user
sudo adduser --disabled-login --no-create-home --gecos "" nexus
#navigate to the /opt directory and download the sonarNexus jar
cd /opt
sudo wget https://download.sonatype.com/nexus/3/latest-unix.tar.gz
sudo tar -zxvf latest-unix.tar.gz
#unzip and set the correct rights
cd /opt
sudo mv nexus-3.65.0-02/ nexus
sudo chown -R nexus:nexus /opt/nexus
sudo chown -R nexus:nexus /opt/sonatype-work
#update the nexus.RC file to run as user Nexus - look at this one. 
sudo vim  /opt/nexus/bin/nexus.rc

Add the following line:
run_as_user="nexus"
#create the run profile
vim /nexus/bin/nexus.vmoptions

-Xms1024m
-Xmx1024m
-XX:MaxDirectMemorySize=1024m
-XX:LogFile=./sonatype-work/nexus3/log/jvm.log
-XX:-OmitStackTraceInFastThrow
-Djava.net.preferIPv4Stack=true
-Dkaraf.home=.
-Dkaraf.base=.
-Dkaraf.etc=etc/karaf
-Djava.util.logging.config.file=/etc/karaf/java.util.logging.properties
-Dkaraf.data=./sonatype-work/nexus3
-Dkaraf.log=./sonatype-work/nexus3/log
-Djava.io.tmpdir=./sonatype-work/nexus3/tmp
#create a service to run this on start-up
sudo vim /etc/systemd/system/nexus.service
[Unit]
Description=nexus service
After=network.target
[Service]
Type=forking
LimitNOFILE=65536
ExecStart=/opt/nexus/bin/nexus start
ExecStop=/opt/nexus/bin/nexus stop
User=nexus
Restart=on-abort
[Install]
WantedBy=multi-user.target

#start the nexus server
sudo systemctl daemon-reload
sudo systemctl start nexus
sudo systemctl enable nexus