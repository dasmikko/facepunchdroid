# Facepunch Droid
The all amazing app for the best forum Facepunch.

Use Facepunch with a more mobile friendly design, and features.

If you don't trust me, you are more than welcome to build the app yourself.

# UPDATE AGAIN
Facepunch have now migrated over to the new forum, which completely breaks the app.
I am currently working on making the app as compatible as possible with the new forum, but don't expect miracles.

I've seem to get the new login working with the app now, but there is a lot of styling to be done, before I can release a new version.

I can't give any estimations, as I most likely will break them, as my spare time is very limited at the moment.

Hopefully when the new API is live, I can make a better app, but only time will tell.

Feel free to throw some donations via the app, to show your interest in the making of a new app. (It's in the drawer at the bottom)

## UPDATE
I'm currently working on a new app for the new facepunch forum, in React Native.
I've got login to work without issues, so now, I actually only need to implement:

- [ ] Forums list
- [ ] Forum Viewing
- [ ] Thread Viewing
- [ ] Thread Posting
- [ ] Post rating

Repo: https://github.com/dasmikko/react-native-facepunchdroid

## Features:
- Material Design 
- Native image viewer 
- Download images directly from image viewer
- Built in dark theme, by Facepunch user Oicani Gonzales (Also known as Awfully dark theme) *
- Mobile friendly design 
- Custom themes 
- Open-source
- Pin threads to drawer
- Easy access to all the important pages everywhere like, Read Threads, Popular etc.. 
- No ads! 
- Multilingual! 
- Userscript support!
- Multi window support on Android Nougat
- Finding on page
- New features added frequently.


\* If there are issues with the dark theme style wise, you have to tell Oicani Gonzales in his thread.
https://facepunch.com/showthread.php?t=1472287

## Making custom stylesheets for Facepunch Droid
It works the usual way CSS works. 

## Translation
If the app isn't in your native language, feel free to contact me on Facepunch, and I will send you an XML file to translate.

Current supported languages:
- English
- Danish
- German


## Q&A
**Q:** You're probably wondering, how is this app going to survive more than the other versions?

**A:** My app doesn't rely on an API, it simply injects some styling to the website, to make it more mobile friendly, and do other stuff more natively. So as long Facepunch is up, my app will continue to work, unless they start to change the HTML to much (This is easily fixed though)

---------

**Q:** WTF?!?! WHY HAVEN'T YOU FIXED THIS/WHY HASN'T THERE BEEN AN UPDATE IN A WHILE

**A:** Please note, I can only work on the app in my spare time, which is already pretty limited. I can usually work on the app from/to work, or if I'm lucky a little bit when I'm home, I have a wife that would like to see me too. I also work for free, and purely from interest.

---------

**Q:** What if Google Play pulls the app?

**A:** No problem, I will simply host it myself, and make a built-in updater (Which isn't allowed on Google Play ATM)


## Changelog
    1.7.7
    - Fixed Twitter embeds not working.
    - Updated a bunch of libraries and other stuff.


    1.7.6
    - New: Disable annoying user titles
    - Fix: Navigation drawer avatar now works again.
    - Fix: Removed the deprecated notification dialog upon pinning thread.

    1.7.5
    - New: Pinned threads are now in the app shortcuts too!
    - New: Events/Report buttons are back!

    
    1.7.4
    - New: Better go to dialog. You can now go to the first and last page quicker!
    - Reduction in APK size after removal of NumberPicker library.

    1.7.3
    - Disable notifications, as the service will crash the app, because of cloudflare...
    
	1.7.2
	- Fix: App now runs on Android 4.4 again. Sorry for the issues!
	
	1.7.1
	- Fix: When trying to open notifications settings, while logged out, it would crash.
	- New: Ratings are now visible as you scroll through the threads!
	- New: Fancy animation when starting the app.
	
    1.7.0
    - Fix: When clicking on subscription notification, it would do nothing..
    - Fix: When clicking on a PM/subscription notification, while the app was running, the page would not load.

	1.6.9
	- Fix: Notification settings not detecting login state correctly.
   
    
[Full changelog here](https://github.com/dasmikko/facepunchdroid/blob/master/changelog.md)

## Official build on Google Play
[![Get it on Google Play](https://my.mixtape.moe/mjhsns.png)](https://play.google.com/store/apps/details?id=com.apps.anker.facepunchdroid)
