# AutoClicker

Professional Android AutoClicker app with accessibility service, floating overlay controller, and point-and-click automation.

## Features

- 🎯 **Point & Click Automation**: Tap to record click points, automatic replay
- 🔄 **Accessibility Service**: Uses Android accessibility APIs for reliable automation  
- 📱 **Floating Controller**: Overlay interface that works on top of any app
- ⚙️ **Configurable Intervals**: Set custom click timing
- 🛡️ **Permission Management**: Guided setup for required permissions
- 📱 **Huawei Compatible**: Works with HMS and EMUI/HarmonyOS

## Requirements

- Android 7.0 (API 24) or higher
- Accessibility service permission
- Display over other apps permission

## Installation

1. Download the APK from the [Releases](../../releases) page
2. Install on your Android device
3. Follow the in-app setup to grant required permissions

## Usage

1. Open AutoClicker
2. Enable Accessibility Service (guided setup)
3. Grant overlay permission (guided setup)  
4. Start the floating controller
5. Tap on screen to record click points
6. Automatic clicking will begin

## Building from Source

1. Clone this repository
2. Open in Android Studio
3. Build → Build Bundle(s)/APK(s) → Build APK(s)

## Permissions

- **Accessibility Service**: Required for automated tapping
- **System Alert Window**: Required for floating overlay
- **Foreground Service**: Required for background operation

## License

MIT License - see [LICENSE](LICENSE) file for details.

## Huawei Device Notes

- Compatible with HMS (Huawei Mobile Services)
- Works on EMUI/HarmonyOS
- May require whitelisting in battery optimization
- Supports AppGallery distribution
