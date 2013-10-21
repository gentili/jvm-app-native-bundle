export TARGET="/home/gentili/videogamez.ca/htdocs/demurrage/GameLauncher/"
export RELEASE="${releaseName}"
export NAME="DemurrageLauncher.jar"
echo Installing ${RELEASE} to videogamez.ca...
scp *.TXT gentili@mcpnet.ca:${TARGET}
scp ../${RELEASE} gentili@mcpnet.ca:videogamez.ca/htdocs/demurrage/GameLauncher/
ssh gentili@mcpnet.ca "rm ${TARGET}${NAME}"
ssh gentili@mcpnet.ca "ln -s ${TARGET}${RELEASE} ${TARGET}${NAME}"
