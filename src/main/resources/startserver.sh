#!/bin/bash
counter="0"
echo "Started: $(date)" > reboot.log
while true
  do
    java -Dname=dev -Xms5G -Xmx5G -XX:+UseG1GC -XX:+ParallelRefProcEnabled -XX:+UnlockExperimentalVMOptions -XX:+DisableExplicitGC -XX:-OmitStackTraceInFastThrow -XX:+AlwaysPreTouch -XX:MaxGCPauseMillis=100 -XX:G1NewSizePercent=40 -XX:G1MaxNewSizePercent=50 -XX:G1HeapRegionSize=16M -XX:G1ReservePercent=20 -XX:G1HeapWastePercent=5 -XX:G1MixedGCCountTarget=8 -XX:InitiatingHeapOccupancyPercent=20 -XX:G1MixedGCLiveThresholdPercent=90 -XX:G1RSetUpdatingPauseTimePercent=5 -XX:SurvivorRatio=32 -XX:MaxTenuringThreshold=1 -Dusing.aikars.flags=true -Daikars.new.flags=true -jar paper.jar
    sleep 1
    if ! [ -e plugins/MCME-Connect/restart.nfo ] 
      then
        echo "$(date): Server stopped." >> reboot.log
	    break
	  else
        counter=$(expr $counter + 1)
		if [ -e plugins/MCME-Connect/connectPlugin.nfo ]
		  then
		    echo "$(date): Restart no. $counter. Reboot by connect plugin command." >> reboot.log
		  else
            echo "$(date): Restart no. $counter. Reboot after server shutdown (did it crash?)." >> reboot.log
	    break
	fi
done
