@echo off
:GoAgain
cls
netstat -anp TCP | find "11203"
ping localhost -n 2 >nul
goto GoAgain