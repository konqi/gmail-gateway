# Pirate Mail Gateway

**Say...**

* you want to allow others to send email from your email address, but only to certain recipients
* you want to send your mail with your google apps account but you don't want to log into that account.
  That's usually because there's that stupid mailing list which can only be accesses from inside your
  company's Google Apps account

**Now you can**

This app utilizes Google's Gmail API to send Messages in your name. 

## Here's how it works

Once you setup everything you can use your personal email and send a message to

something@appid.appspotmail.com

and the email is forwarded to predefined recipients with the email address you authorized the app with as the sender.

## Setup instructions

Download the source and place a *client_secrets.json* in src/main/resources.
You can get the *client_secrets.json* from [Google developers console](https://console.developers.google.com/project/yourappid/apiui/credential).
