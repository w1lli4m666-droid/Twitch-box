# Twitch-box

<p align="center">
  <img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher.png" width="144" alt="Twitch-box app icon">
</p>

Twitch-box is an Android TV-oriented Twitch browser and player based on [Xtra](https://github.com/crackededed/Xtra). The installed application is named **Twitch**. It is designed for TV remotes, supports Android 4.1 and later, and lets you follow channels locally without signing in to a Twitch account.

## Features

- Android TV launcher support, including a TV banner and remote-friendly focus indicators.
- Compatible with Android 4.1 (API 16) and later. Android 4.0 is not supported.
- Browse categories, popular streams, followed channels, videos, and saved content.
- Follow channels locally without a Twitch account.
- Optional Twitch device-code sign-in.
- Proxy-aware network access for Twitch content.
- Remote-friendly player controls, dialogs, menus, quality selection, chat controls, and fullscreen actions.
- First-result focus and hierarchical Back navigation for categories, channels, and streams.
- Player exit restores focus to the stream, video, clip, bookmark, or offline-video card that launched playback.
- Event-driven refreshes keep followed games, live channels, channels, and account-followed videos up to date without reloading them on every visit.
- The followed Live tab invalidates its cached paging source immediately after a channel follow change and can resolve local follows without a Twitch account token.
- Optional automatic mini-player playback. It is disabled by default.

## Downloads

Download the latest APKs from the [GitHub Releases page](https://github.com/w1lli4m666-droid/Twitch-box/releases/latest).

| APK | Recommended for |
| --- | --- |
| `Twitch-box-v2.58.6.1-armeabi-v7a.apk` | 32-bit ARM TV boxes, including Android 4.1–4.4 devices |
| `Twitch-box-v2.58.6.1-arm64-v8a.apk` | 64-bit ARM Android TV devices (Android 5.0 or newer) |
| `Twitch-box-v2.58.6.1-universal.apk` | ARM devices where the architecture is unknown; includes ARMv7 and ARM64 libraries only |

## Remote control guide

### Browsing

- Use the D-pad to move focus and press **OK/Select** to open the focused item.
- The bottom navigation order is **Browse**, **Popular**, **Following**, and **Saved**.
- Newly loaded pages focus their first result automatically.
- On a results page, press the remote's **Menu/Settings** key to open the Sort card. Its first available option receives focus automatically.
- Browse categories can be sorted by **Recommended for you**, viewer count in either direction, or **Recently started**. Stream lists can be sorted by viewer count in either direction or **Recently started**.
- Sorting choices apply immediately. The redundant **Apply**, **Add tag**, and **Save filters** actions are removed from Browse and Popular stream sorting. Following and channel-video sorting also applies immediately without an extra **Apply** action.
- The empty **Filters** heading and tag row are hidden in Browse-category and Popular stream sorting, so D-pad Down moves directly to **Languages**.
- **Apply to all categories** stores a common default, while **Remember choices for this game** stores an override for only the current category.
- Following lists refresh only after a relevant follow change: channel changes refresh **Live** and **Channels**, game changes refresh **Games**, and Twitch account channel changes also refresh **Videos**.
- The **Live** list keeps its consumed follow version in the ViewModel, so a follow change cannot be lost when the page view is recreated. If the batch lookup for locally followed live channels fails, the app retries by login, then uses account Helix access when available, and finally checks channel pages without requiring a Twitch account token.
- **Back** returns one level at a time and restores focus to the item that opened that level. For example, a stream returns to its category, and the category returns to **Browse**.
- Search and Settings are available from the top navigation row.

### Video and live playback

- Press the remote's **Menu/Settings** key to show the player controls. The default upper-right actions are **Follow**, **Quality**, and **Player menu**.
- Use the D-pad to reach playback, chat, subtitle, audio, volume, fullscreen, and player-menu actions. Press **OK/Select** to activate the focused action.
- The toolbar hides after 6 seconds when focus is not moving on it.
- When player controls are visible, **Back** hides them immediately. Press **Back** again after they are hidden to close or minimize playback according to the automatic mini-player setting.
- After playback is closed, focus returns to the card that launched it. The app tracks both its list position and content identity, so focus can still be restored after paging changes.
- Press and hold **Left** or **Right** for 600 ms to start rewinding or fast-forwarding. The player then jumps 10 seconds approximately every 600 ms. After the key has been held for more than 5 seconds, each jump accelerates to 60 seconds.
- The **Player menu** contains additional actions such as viewers, download, share, and subtitles when those actions are available.
- **Volume** and **Fullscreen** buttons are disabled by default in **Settings > Player button settings**, but they can be enabled there.
- The **Playback speed** player button is enabled by default.
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

The build produces separate `armeabi-v7a`, `arm64-v8a`, and ARM-only universal APKs under `app/build/outputs/apk/release/`. The `armeabi-v7a` APK is the correct choice for Android 4.1–4.4 TV boxes.

## License

Licensed under the [GNU Affero General Public License v3.0](LICENSE).

## Upstream project

This repository is based on [crackededed/Xtra](https://github.com/crackededed/Xtra/). It preserves the upstream license and adds Android TV navigation, compatibility, and playback behavior.
