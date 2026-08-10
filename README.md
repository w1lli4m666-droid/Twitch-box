# Twitch-box

<p align="center">
  <img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher.png" width="144" alt="Twitch-box app icon">
</p>

Twitch-box is an Android TV-oriented Twitch browser and player based on [Xtra](https://github.com/crackededed/Xtra). It is designed for TV remotes, supports Android 5.0 and later, and lets you follow channels locally without signing in to a Twitch account.

## Features

- Android TV launcher support, including a TV banner and remote-friendly focus indicators.
- Compatible with Android 5.0 (API 21) and later.
- Browse categories, popular streams, followed channels, videos, and saved content.
- Follow channels locally without a Twitch account.
- Optional Twitch device-code sign-in.
- Proxy-aware network access for Twitch content.
- Remote-friendly player controls, dialogs, menus, quality selection, chat controls, and fullscreen actions.
- First-result focus and hierarchical Back navigation for categories, channels, and streams.
- Optional automatic mini-player playback. It is disabled by default.

## Downloads

Download the latest APKs from the [GitHub Releases page](https://github.com/w1lli4m666-droid/Twitch-box/releases/latest).

| APK | Recommended for |
| --- | --- |
| `Twitch-box-v2.58.5-TV6-armeabi-v7a.apk` | 32-bit ARM TV boxes and older Android devices |
| `Twitch-box-v2.58.5-TV6-arm64-v8a.apk` | 64-bit ARM Android TV devices |
| `Twitch-box-v2.58.5-TV6-universal.apk` | Devices where the architecture is unknown; includes ARMv7, ARM64, x86, and x86_64 libraries |

## Remote control guide

### Browsing

- Use the D-pad to move focus and press **OK/Select** to open the focused item.
- The bottom navigation order is **Browse**, **Popular**, **Following**, and **Saved**.
- Newly loaded pages focus their first result automatically.
- **Back** returns one level at a time and restores focus to the item that opened that level. For example, a stream returns to its category, and the category returns to **Browse**.
- Search and Settings are available from the top navigation row.

### Video and live playback

- Press the remote's **Menu/Settings** key to show the player controls. The default upper-right actions are **Follow**, **Quality**, and **Player menu**.
- Use the D-pad to reach playback, chat, subtitle, audio, volume, fullscreen, and player-menu actions. Press **OK/Select** to activate the focused action.
- The toolbar hides after 6 seconds when focus is not moving on it.
- Press and hold **Left** or **Right** for 600 ms to start rewinding or fast-forwarding. The player then jumps 10 seconds approximately every 600 ms. After the key has been held for more than 5 seconds, each jump accelerates to 60 seconds.
- The **Player menu** contains additional actions such as viewers, download, share, and subtitles when those actions are available.
- **Volume** and **Fullscreen** buttons are disabled by default in **Settings > Player button settings**, but they can be enabled there.
- **Automatic mini-player playback** is disabled by default. Enable it in Settings if playback should continue in a small window after pressing **Back**.

## Local follows and Twitch sign-in

You can follow channels locally without a Twitch account. Local follows remain on the device. Twitch sign-in is optional and uses the device-code flow, which is suitable for entering the authorization code on a phone or computer.

## Building

Requirements:

- Android SDK with API 37 installed
- JDK 21

On Windows:

```powershell
.\gradlew.bat assembleRelease
```

On Linux or macOS:

```bash
./gradlew assembleRelease
```

The build produces separate `armeabi-v7a`, `arm64-v8a`, and universal APKs under `app/build/outputs/apk/release/`.

## License

Licensed under the [GNU Affero General Public License v3.0](LICENSE).

## Upstream project

This repository is based on [crackededed/Xtra](https://github.com/crackededed/Xtra/). It preserves the upstream license and adds Android TV navigation, compatibility, and playback behavior.
