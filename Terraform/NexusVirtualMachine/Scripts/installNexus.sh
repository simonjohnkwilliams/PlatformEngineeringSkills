#! /bin/bash
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
sudo mv nexus-3.69.0-02/ nexus
sudo chown -R nexus:nexus /opt/nexus
sudo chown -R nexus:nexus /opt/sonatype-work
sudo ln -s /opt/nexus-3.69.0-02/bin/nexus /etc/init.d/nexus

#update the chkconfig
cd /etc/init.d
sudo chkconfig --add nexus
sudo chkconfig --levels 345 nexus on

#update the nexus.RC file to run as user Nexus - look at this one. 
sudo touch  ./nexus/bin/nexus.rc
echo "run_as_user="nexus"" | sudo tee ./nexus/bin/nexus.rc
#create the run profile
sudo touch ./nexus/bin/nexus.vmoptions
echo "-Xms1024m" | sudo tee ./nexus/bin/nexus.vmoptions
echo "-Xmx1024m" | sudo tee -a ./nexus/bin/nexus.vmoptions
echo "-XX:MaxDirectMemorySize=1024m" | sudo tee -a ./nexus/bin/nexus.vmoptions
echo "-XX:LogFile=./sonatype-work/nexus3/log/jvm.log" | sudo tee -a ./nexus/bin/nexus.vmoptions
echo "-XX:-OmitStackTraceInFastThrow" | sudo tee -a ./nexus/bin/nexus.vmoptions
echo "-Djava.net.preferIPv4Stack=true" | sudo tee -a ./nexus/bin/nexus.vmoptions
echo "-Dkaraf.home=." | sudo tee -a ./nexus/bin/nexus.vmoptions
echo "-Dkaraf.base=." | sudo tee -a ./nexus/bin/nexus.vmoptions
echo "-Dkaraf.etc=etc/karaf" | sudo tee -a ./nexus/bin/nexus.vmoptions
echo "-Djava.util.logging.config.file=/etc/karaf/java.util.logging.properties" | sudo tee -a ./nexus/bin/nexus.vmoptions
echo "-Dkaraf.data=./sonatype-work/nexus3" | sudo tee -a ./nexus/bin/nexus.vmoptions
echo "-Dkaraf.log=./sonatype-work/nexus3/log" | sudo tee -a ./nexus/bin/nexus.vmoptions
echo "-Djava.io.tmpdir=./sonatype-work/nexus3/tmp" | sudo tee -a ./nexus/bin/nexus.vmoptions

#create a service to run this on start-up
sudo touch /etc/systemd/system/nexus.service
echo "[Unit]" | sudo tee /etc/systemd/system/nexus.service
echo "Description=nexus service" | sudo tee -a /etc/systemd/system/nexus.service
echo "After=network.target" | sudo tee -a /etc/systemd/system/nexus.service
echo "[Service]" | sudo tee -a /etc/systemd/system/nexus.service
echo "Type=forking" | sudo tee -a /etc/systemd/system/nexus.service
echo "LimitNOFILE=65536" | sudo tee -a /etc/systemd/system/nexus.service
echo "ExecStart=/opt/nexus/bin/nexus start" | sudo tee -a /etc/systemd/system/nexus.service
echo "ExecStop=/opt/nexus/bin/nexus stop" | sudo tee -a /etc/systemd/system/nexus.service
echo "User=nexus" | sudo tee -a /etc/systemd/system/nexus.service
echo "Restart=on-abort" | sudo tee -a /etc/systemd/system/nexus.service
echo "[Install]" | sudo tee -a /etc/systemd/system/nexus.service
echo "WantedBy=multi-user.target" | sudo tee -a /etc/systemd/system/nexus.service

#start the nexus server
sudo systemctl daemon-reload
sudo systemctl enable nexus.service
sudo systemctl start nexus.service