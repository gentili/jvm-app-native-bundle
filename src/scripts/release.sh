export TARGET="/home/gentili/videogamez.ca/htdocs/demurrage/GameLauncher/"
export JARNAME="${jarName}"
export EXENAME="${exeName}"
export APPZIPNAME="${appName}.zip"
export FINALJARNAME="DemurrageLauncher.jar"
export FINALEXENAME="DemurrageLauncher.exe"
export FINALAPPNAME="DemurrageLauncher.app.zip"
echo Installing ${JARNAME} to videogamez.ca...
scp *.TXT gentili@mcpnet.ca:${TARGET}
scp ../${JARNAME} gentili@mcpnet.ca:videogamez.ca/htdocs/demurrage/GameLauncher/
scp ../${EXENAME} gentili@mcpnet.ca:videogamez.ca/htdocs/demurrage/GameLauncher/
scp ../${APPZIPNAME} gentili@mcpnet.ca:videogamez.ca/htdocs/demurrage/GameLauncher/
ssh gentili@mcpnet.ca "rm ${TARGET}${FINALJARNAME}"
ssh gentili@mcpnet.ca "ln -s ${TARGET}${JARNAME} ${TARGET}${FINALJARNAME}"
ssh gentili@mcpnet.ca "rm ${TARGET}${FINALEXENAME}"
ssh gentili@mcpnet.ca "ln -s ${TARGET}${EXENAME} ${TARGET}${FINALEXENAME}"
ssh gentili@mcpnet.ca "rm ${TARGET}${FINALAPPNAME}"
ssh gentili@mcpnet.ca "ln -s ${TARGET}${APPZIPNAME} ${TARGET}${FINALAPPNAME}"
