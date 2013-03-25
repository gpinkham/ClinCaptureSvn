@ECHO off

REM This script is for configuring OpenClinica LOGIN page

IF %1%. == . GOTO No1

IF %1% == DEV GOTO DEV
IF %1% == PROD GOTO PROD
IF %1% == TRAINING GOTO TRAINING

:No1
ECHO Syntax:  config [ DEV ] [ PROD ] [ TRAINING ]
ECHO Example: config DEV
GOTO End1

:DEV
ECHO Development DataBase > WEB-INF\jsp\login-include\login-dbtitle.jsp
DEL images\login_BG.gif 
COPY includes\NewLoginStyles_DEV.css includes\NewLoginStyles.css
GOTO End1

:PROD
ECHO. > WEB-INF\jsp\login-include\login-dbtitle.jsp
COPY images\login_BG_PROD.gif images\login_BG.gif
COPY includes\NewLoginStyles_PROD.css includes\NewLoginStyles.css
GOTO End1

:TRAINING
ECHO Training DataBase > WEB-INF\jsp\login-include\login-dbtitle.jsp
COPY images\login_BG_PROD.gif images\login_BG.gif
COPY includes\NewLoginStyles_PROD.css includes\NewLoginStyles.css

:End1