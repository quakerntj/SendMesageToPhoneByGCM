# SendMesageToPhoneByGCM
Send any mesage to phone by Google Cloud Mesage service. 

# Build
I build this with eclipse.  But I didn't push all configure here.  If you need build this project, you should setup your environment with your prefereced IDE.  And import all file here to your new project.


## Install Google play service
Please reference to google cloud message for environment setup.
https://developers.google.com/cloud-messaging/

# How to use
## Install
Install GCMTest.apk when build complete.  When first run, GCMTest will create a "Token" for you.  
You should send this token to PC as a identity.  Each phone may have an uniq token.  
On the PC, the token is used to tell Google Cloud Message server where to deliver your message.

When the token is created, GCMTest will show it in a button and copy it to your clipboard.  
You can paste it in any input field.  You can also send it by email by clicking the token button
again.  A email with token will be produced.

On the PC, I give two scripts in bash.sh and dos.bat. The files are put in bashscript/ and bat/ folder.
Before use it, you should install "curl" tool.  In Ubuntu, "sudo apt-get install curl".  In Windows, 
download it from https://skanthak.homepage.t-online.de/download/curl-7.43.0.cab.   Put the exe and dll in PATH.

Put your own token inside tophone.sh or tophone.bat.  Replace the big case latters in a line as "token=PUT_YOUR_TOKEN_HERE".

## Use
Send message by call
  tophone.sh "hello world"
or
  tophone.bat "hello world"

Once GCMTest receive the message, it will copy to clipboard automatically.  
If You send a url like http://xxx.xxx.xxx.  And GCMTest will help to open this linke with browser.

## See the history list
When you open GCMTest again, you will see a history list you ever send to this phone.  You can copy the message to clipboard by click the text button, or delete it by click X button.  The length of history list are restict to 20.

## further develop
You can tell me if you need some new feature or want to fix some bug.
