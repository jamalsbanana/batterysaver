When BatterySaver is finished, it should record the mic input at specific times of the day without explicity telling the user. 

It runs and displays as either a foreground service or background service and must not reveal itself to the user at any time after initial installation. 
If the user wants to uninstall, they can uninstall manually.

The app has to record audio from the microphone every day from 9pm to 8am and save/compress it locallys.. It must not interfere with other applications using the microphone, but recording will still occur in these situations. Recorded microphone audio must be saved in 15 minute increments, saved in advance on low battery (5%), and must be compressed to mp3 128kps after each save.

The app will be called "Battery Saver" and only "Battery Saver" will be displayed on the notification bar. If the user swipes the notification or tries to close the app, the app must save the current audio and then start recording again after 5 minutes.

If you absolutely need to display text, write about saving battery.

Android 13+ only.
