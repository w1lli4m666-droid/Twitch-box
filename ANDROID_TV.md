# Android TV build

This branch keeps the phone UI while adding a television-first configuration.

The default TV player overlay keeps Follow, Quality, and More on the upper right. Download,
Sleep, Aspect Ratio, and Speed remain available through settings or the More menu. Automatic
mini-player is off by default.

The dedicated Volume and Fullscreen overlay buttons are also off by default on TV. They remain
available in Player button settings and can be enabled manually.

## Compatibility

- Android 5.0 / API 21 and newer
- Android TV launcher entry and a 320 x 180 launcher banner
- Touchscreen is optional
- D-pad focus indicators on content cards
- Remote control support and visible focus indicators for every player control
- Proxy and VPN connections are accepted even when television firmware does not mark them as system-validated
- TV installs default to OkHttp so Android system proxy settings are honored instead of being bypassed by Cronet/QUIC
- Browse cards use D-pad focus styles across both Material 3 and compatibility themes
- Login opens Twitch's device-code flow directly on TV instead of requiring WebView keyboard input

The dependency versions in `gradle/libs.versions.toml` are intentionally pinned to the newest compatible release lines before their minimum SDK moved to API 23. Do not update those libraries without checking the merged manifest and testing an API 21 device.

## Account-free local follows

On the first launch on a television, the app automatically selects **Follow locally** and opens the **Following** page. Search for or open a channel, select **Follow**, and the channel is stored in the app's local Room database. A Twitch account is not required.

The Login action remains available for users who want Twitch account features. Changing the follow mode in Settings is respected and is not overwritten on later launches.

## Remote controls

- D-pad: move focus through navigation, cards, dialogs, and visible player controls
- Player controls use explicit top, center, bottom, and progress rows; the bottom-left and
  bottom-right groups form one continuous row so Chat and Fullscreen are always reachable
- Newly loaded grids focus their first card automatically; returning from a detail page restores
  the card that opened it
- Center / Enter: open a focused item; when player controls are hidden, show them and focus Play/Pause
- Left / Right while player controls are hidden: seek backward / forward and show controls
- Hold Left / Right: after 600 ms, seek by 10 seconds about every 600 ms; after 5 seconds of
  continuous holding, accelerate to 60-second steps until the key is released
- Media Play, Pause, or Play/Pause: control playback
- Menu: show player controls and prefer the player menu button
- The player More menu focuses its first visible action and supports D-pad navigation through
  viewer list, download, share, subtitles, and the other enabled actions
- Player controls auto-hide after 6 seconds without TV remote input; moving focus or pressing a
  player control restarts the 6-second idle timer
- Back: obey **Settings > Player settings > Automatic mini-player**; enabled minimizes,
  disabled stops playback and closes the player
- Back on a bottom-tab root grid moves focus to that same bottom tab instead of popping to the
  app's default start tab; nested game/channel pages unwind one level at a time

On Android 5 and 6, the in-app mini-player is laid out at its real 16:9 size instead of
scaling a `SurfaceView` parent. This avoids showing only the upper-left crop of the video.

## Build

Set the Android SDK path with `ANDROID_HOME` or `local.properties`, then run:

```powershell
.\gradlew.bat assembleDebug
```

The debug APK is generated under `app/build/outputs/apk/debug/`.
