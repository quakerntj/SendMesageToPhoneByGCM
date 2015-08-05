REM # 需先安裝CURL window版本
REM # https://skanthak.homepage.t-online.de/download/curl-7.43.0.cab
REM # intel64bit處理器應該使用AMD64版本
REM # 將AMD64/*放在可被執行的目錄下(%PATH%中有出現的) 


REM # 然後把此檔案存成tophone.bat 放在可被執行的目錄下(%PATH%中有出現的)

@echo on

set api_key=AIzaSyD_e8YpgV0tv8geFIAK7YVCe2WfoBkOkUs
set token=PUT_YOUR_TOKEN_HERE

if NOT "%1" == "" CURL.EXE --header Authorization:key="%api_key%" --header Content-Type:"application/json" https://gcm-http.googleapis.com/gcm/send -d {\"to\":\"%token%\",\"data\":{\"message\":\"%1\"}}

