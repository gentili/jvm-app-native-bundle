export TARGET="/home/gentili/videogamez.ca/htdocs/demurrage/GameLauncher/"
export RELEASE="${releaseName}"
export EXENAME="${exeName}"
export FINALJARNAME="DemurrageLauncher.jar"
export FINALEXENAME="DemurrageLauncher.exe"
echo Installing ${RELEASE} to videogamez.ca...
scp *.TXT gentili@mcpnet.ca:${TARGET}
scp ../${RELEASE} gentili@mcpnet.ca:videogamez.ca/htdocs/demurrage/GameLauncher/
scp ../${EXENAME} gentili@mcpnet.ca:videogamez.ca/htdocs/demurrage/GameLauncher/
ssh gentili@mcpnet.ca "rm ${TARGET}${FINALJARNAME}"
ssh gentili@mcpnet.ca "ln -s ${TARGET}${RELEASE} ${TARGET}${FINALJARNAME}"
ssh gentili@mcpnet.ca "rm ${TARGET}${FINALEXENAME}"
ssh gentili@mcpnet.ca "ln -s ${TARGET}${EXENAME} ${TARGET}${FINALEXENAME}"
