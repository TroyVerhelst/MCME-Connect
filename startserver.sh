#!/bin/bash

while true
  do
    java -Xmx4G -jar spigot-1.12.2.jar 
#    echo start
	sleep 1
	if [ -e plugins/MCME-Connect/restart.nfo ] 
	  then
	    rm plugins/MCME-Connect/restart.nfo  
	  else
	    break
	fi
done
